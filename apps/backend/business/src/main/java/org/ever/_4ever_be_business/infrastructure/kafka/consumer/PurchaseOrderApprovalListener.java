package org.ever._4ever_be_business.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.saga.SagaTransactionManager;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever._4ever_be_business.voucher.entity.PurchaseVoucher;
import org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus;
import org.ever._4ever_be_business.voucher.repository.PurchaseVoucherRepository;
import org.ever.event.PurchaseOrderApprovalCompletionEvent;
import org.ever.event.PurchaseOrderApprovalEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.PURCHASE_ORDER_APPROVAL_COMPLETION_TOPIC;
import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.PURCHASE_ORDER_APPROVAL_TOPIC;

/**
 * 구매주문 승인 이벤트 리스너
 * SCM에서 구매주문이 승인되면 PurchaseVoucher를 생성
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PurchaseOrderApprovalListener {

    private final SagaTransactionManager sagaTransactionManager;
    private final PurchaseVoucherRepository purchaseVoucherRepository;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = PURCHASE_ORDER_APPROVAL_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handlePurchaseOrderApproval(PurchaseOrderApprovalEvent event, Acknowledgment acknowledgment) {
        log.info("구매주문 승인 이벤트 수신: transactionId={}, purchaseOrderId={}, supplierCompanyId={}",
                event.getTransactionId(), event.getPurchaseOrderId(), event.getSupplierCompanyId());

        try {
            // Saga 트랜잭션으로 실행
            sagaTransactionManager.executeSagaWithId(event.getTransactionId(), () -> {
                // 1. PurchaseVoucher 코드 생성
                String voucherCode = generatePurchaseVoucherCode();

                // 2. PurchaseVoucher 생성
                PurchaseVoucher purchaseVoucher = PurchaseVoucher.builder()
                        .supplierCompanyId(event.getSupplierCompanyId())
                        .productOrderId(event.getPurchaseOrderId())
                        .voucherCode(voucherCode)
                        .issueDate(LocalDateTime.now())
                        .dueDate(event.getDueDate() != null ? event.getDueDate() : LocalDateTime.now().plusDays(30))
                        .totalAmount(event.getTotalAmount())
                        .status(PurchaseVoucherStatus.UNPAID)
                        .memo(event.getMemo() != null ? event.getMemo() : "")
                        .build();

                purchaseVoucherRepository.save(purchaseVoucher);

                log.info("PurchaseVoucher 생성 완료: voucherId={}, voucherCode={}, productOrderId={}",
                        purchaseVoucher.getId(), voucherCode, event.getPurchaseOrderId());

                return null;
            });

            // 3. 완료 이벤트 발송
            PurchaseOrderApprovalCompletionEvent completionEvent = PurchaseOrderApprovalCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .purchaseOrderId(event.getPurchaseOrderId())
                    .success(true)
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(PURCHASE_ORDER_APPROVAL_COMPLETION_TOPIC,
                    event.getPurchaseOrderId(), completionEvent);

            log.info("구매주문 승인 완료 이벤트 발송: transactionId={}", event.getTransactionId());

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("구매주문 승인 처리 실패: transactionId={}, purchaseOrderId={}",
                    event.getTransactionId(), event.getPurchaseOrderId(), e);

            // 실패 이벤트 발송
            PurchaseOrderApprovalCompletionEvent completionEvent = PurchaseOrderApprovalCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .purchaseOrderId(event.getPurchaseOrderId())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(PURCHASE_ORDER_APPROVAL_COMPLETION_TOPIC,
                    event.getPurchaseOrderId(), completionEvent);

            acknowledgment.acknowledge();
        }
    }

    /**
     * PurchaseVoucher 코드 생성
     */
    private String generatePurchaseVoucherCode() {
        String uuid = UUID.randomUUID().toString().replace("-", "");

        return "PV-" + uuid.substring(uuid.length() - 7);
    }
}
