package org.ever._4ever_be_business.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.saga.SagaTransactionManager;
import org.ever._4ever_be_business.common.util.CodeGenerator;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.entity.OrderItem;
import org.ever._4ever_be_business.order.entity.OrderStatus;
import org.ever._4ever_be_business.order.entity.Quotation;
import org.ever._4ever_be_business.order.entity.QuotationApproval;
import org.ever._4ever_be_business.order.entity.QuotationItem;
import org.ever._4ever_be_business.order.enums.ApprovalStatus;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever._4ever_be_business.order.repository.QuotationApprovalRepository;
import org.ever._4ever_be_business.order.repository.QuotationItemRepository;
import org.ever._4ever_be_business.order.repository.QuotationRepository;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.voucher.entity.SalesVoucher;
import org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus;
import org.ever._4ever_be_business.voucher.repository.SalesVoucherRepository;
import org.ever.event.AlarmEvent;
import org.ever.event.QuotationUpdateEvent;
import org.ever.event.QuotationUpdateCompletionEvent;
import org.ever.event.alarm.AlarmType;
import org.ever.event.alarm.LinkType;
import org.ever.event.alarm.SourceType;
import org.ever.event.alarm.TargetType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.QUOTATION_UPDATE_COMPLETION_TOPIC;
import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.QUOTATION_UPDATE_TOPIC;

/**
 * 견적 업데이트 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QuotationUpdateListener {

    private final SagaTransactionManager sagaTransactionManager;
    private final QuotationRepository quotationRepository;
    private final QuotationApprovalRepository quotationApprovalRepository;
    private final QuotationItemRepository quotationItemRepository;
    private final OrderRepository orderRepository;
    private final org.ever._4ever_be_business.order.repository.OrderItemRepository orderItemRepository;
    private final KafkaProducerService kafkaProducerService;
    private final CustomerUserRepository customerUserRepository;
    private final SalesVoucherRepository salesVoucherRepository;

    @KafkaListener(topics = QUOTATION_UPDATE_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleQuotationUpdate(QuotationUpdateEvent event, Acknowledgment acknowledgment) {
        log.info("견적 업데이트 이벤트 수신: transactionId={}, quotationId={}, dueDate={}, status={}",
                event.getTransactionId(), event.getQuotationId(), event.getDueDate(), event.getQuotationStatus());

        try {
            // Saga 트랜잭션으로 실행
            sagaTransactionManager.executeSagaWithId(event.getTransactionId(), () -> {
                // 1. Quotation 조회
                Quotation quotation = quotationRepository.findById(event.getQuotationId())
                        .orElseThrow(() -> new RuntimeException("Quotation not found: " + event.getQuotationId()));

                quotation.setAvailableStatus("CHECKED");
                quotationRepository.save(quotation);

                // 2. CustomerCompany 조회
                org.ever._4ever_be_business.hr.entity.CustomerUser customerUser =
                        customerUserRepository.findById(quotation.getCustomerUserId())
                                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
                CustomerCompany customerCompany = customerUser.getCustomerCompany();
                if (customerCompany == null) {
                    throw new BusinessException(ErrorCode.CUSTOMER_COMPANY_NOT_FOUND);
                }

                // 3. QuotationApproval 상태를 APPROVAL로 변경
                if (quotation.getQuotationApproval() != null) {
                    QuotationApproval approval = quotation.getQuotationApproval();
                    approval.setApprovalStatus(ApprovalStatus.valueOf(event.getQuotationStatus()));
                    quotationApprovalRepository.save(approval);
                    log.info("견적 승인 상태 업데이트 완료: approvalId={}, status={}",
                            approval.getId(), event.getQuotationStatus());
                }

                // 4. DueDate 업데이트
                LocalDateTime newDueDate = event.getDueDate().atStartOfDay();
                quotation.setDueDate(newDueDate);
                quotationRepository.save(quotation);
                log.info("견적 납기일 업데이트 완료: quotationId={}, newDueDate={}",
                        quotation.getId(), newDueDate);

                // 5. Order 생성 (PENDING 상태로)
                String orderId = createOrderFromQuotation(quotation, customerCompany);
                log.info("주문 생성 완료: orderId={}, status=PENDING", orderId);

                // TODO 알람 필요 : 견적 확정 -> 고객사
                log.info("[ALARM] 견적서 상태 변경 알림 생성 - : {}", quotation.getId());
                String targetId = quotation.getCustomerUserId();
                AlarmEvent alarmEventForCreate = AlarmEvent.builder()
                    .eventId(UuidV7Generator.generate())
                    .eventType(AlarmEvent.class.getName())
                    .timestamp(LocalDateTime.now())
                    .source(SourceType.BUSINESS.name())
                    .alarmId(UuidV7Generator.generate())
                    .alarmType(AlarmType.SD)
                    .targetId(targetId)
                    .targetType(TargetType.CUSTOMER)
                    .title("견적서 상태 변경")
                    .message("해당 견적서가 확정되었습니다. 견저서 번호 = " + quotation.getQuotationCode())
                    .linkId(quotation.getId())
                    .linkType(LinkType.QUOTATION)
                    .scheduledAt(null)
                    .build();

                log.info("[ALARM] 알림 요청 전송 준비 - alarmId: {}, targetId: {}, targetType: {}, linkType: {}",
                    alarmEventForCreate.getAlarmId(), targetId, alarmEventForCreate.getTargetType(),
                    alarmEventForCreate.getLinkType());
                kafkaProducerService.sendAlarmEvent(alarmEventForCreate)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("[ALARM] 알림 요청 전송 실패 - alarmId: {}, targetId: {}, error: {}",
                                alarmEventForCreate.getAlarmId(), targetId, ex.getMessage(), ex);
                        } else if (result != null) {
                            log.info("[ALARM] 알림 요청 전송 성공 - topic: {}, partition: {}, offset: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                        } else {
                            log.warn("[ALARM] 알림 요청 전송 결과가 null 입니다 - alarmId: {}, targetId: {}",
                                alarmEventForCreate.getAlarmId(), targetId);
                        }
                    });

                return null;
            });

            // 5. 완료 이벤트 발송
            QuotationUpdateCompletionEvent completionEvent = QuotationUpdateCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .quotationId(event.getQuotationId())
                    .success(true)
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(QUOTATION_UPDATE_COMPLETION_TOPIC,
                    event.getQuotationId(), completionEvent);

            log.info("견적 업데이트 완료 이벤트 발송: transactionId={}", event.getTransactionId());

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("견적 업데이트 처리 실패: transactionId={}, quotationId={}",
                    event.getTransactionId(), event.getQuotationId(), e);

            // 실패 이벤트 발송
            QuotationUpdateCompletionEvent completionEvent = QuotationUpdateCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .quotationId(event.getQuotationId())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(QUOTATION_UPDATE_COMPLETION_TOPIC,
                    event.getQuotationId(), completionEvent);

            acknowledgment.acknowledge();
        }
    }

    /**
     * Quotation으로부터 Order 생성 (PENDING 상태)
     */
    private String createOrderFromQuotation(Quotation quotation,  CustomerCompany customerCompany) {
        // QuotationItem 조회
        List<QuotationItem> quotationItems = quotationItemRepository.findAll().stream()
                .filter(item -> item.getQuotation().getId().equals(quotation.getId()))
                .toList();

        // 총 금액 계산
        BigDecimal totalPrice = quotationItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Order 생성
        String orderId = UuidV7Generator.generate();
        String orderCode = CodeGenerator.generateOrderCode();

        Order order = new Order();
        order.setId(orderId);
        order.setOrderCode(orderCode);
        order.setQuotation(quotation);
        order.setCustomerUserId(quotation.getCustomerUserId());
        order.setTotalPrice(totalPrice);
        order.setOrderDate(LocalDateTime.now());
        order.setDueDate(quotation.getDueDate());
        order.setStatus(OrderStatus.PENDING); // PENDING 상태로 생성

        Order savedOrder = orderRepository.save(order);
        log.info("Order 생성 완료: orderId={}, orderCode={}", savedOrder.getId(), orderCode);

        // OrderItem 생성
        for (QuotationItem quotationItem : quotationItems) {
            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    quotationItem.getProductId(),
                    quotationItem.getCount(),
                    quotationItem.getUnit(),
                    quotationItem.getPrice().longValue()
            );
            orderItemRepository.save(orderItem);
        }
        log.info("OrderItem 생성 완료: count={}", quotationItems.size());

        // SalesVoucher 생성
        String voucherCode = CodeGenerator.generateCode("SV");
        SalesVoucher salesVoucher = new SalesVoucher(
                customerCompany,
                savedOrder,  // savedOrder 사용
                voucherCode,
                LocalDateTime.now(),  // issueDate
                quotation.getDueDate(),  // dueDate
                quotation.getTotalPrice(),
                SalesVoucherStatus.UNPAID,
                "견적서 승인을 통한 자동 생성"
        );

        // SalesVoucher 저장
        SalesVoucher savedVoucher = salesVoucherRepository.save(salesVoucher);
        log.info("매출전표 생성 완료: voucherId={}, voucherCode={}, status=PENDING",
                savedVoucher.getId(), voucherCode);

        return savedOrder.getId();
    }
}
