package org.ever._4ever_be_business.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.saga.SagaTransactionManager;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.entity.OrderStatus;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever.event.MesStartEvent;
import org.ever.event.MesStartCompletionEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.MES_START_COMPLETION_TOPIC;
import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.MES_START_TOPIC;

/**
 * MES 시작 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MesStartListener {

    private final SagaTransactionManager sagaTransactionManager;
    private final OrderRepository orderRepository;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(topics = MES_START_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleMesStart(MesStartEvent event, Acknowledgment acknowledgment) {
        log.info("MES 시작 이벤트 수신: transactionId={}, mesId={}, quotationId={}",
                event.getTransactionId(), event.getMesId(), event.getQuotationId());

        try {
            // Saga 트랜잭션으로 실행
            sagaTransactionManager.executeSagaWithId(event.getTransactionId(), () -> {
                // 1. QuotationId로 Order 조회
                Order order = orderRepository.findAll().stream()
                        .filter(o -> o.getQuotation() != null &&
                                     o.getQuotation().getId().equals(event.getQuotationId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Order not found for quotationId: " + event.getQuotationId()));

                // 2. Order 상태를 IN_PRODUCTION으로 변경
                order.setStatus(OrderStatus.IN_PRODUCTION);
                orderRepository.save(order);

                log.info("Order 상태 업데이트 완료: orderId={}, status=IN_PRODUCTION", order.getId());

                return null;
            });

            // 3. 완료 이벤트 발송
            MesStartCompletionEvent completionEvent = MesStartCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .mesId(event.getMesId())
                    .success(true)
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(MES_START_COMPLETION_TOPIC, event.getMesId(), completionEvent);

            log.info("MES 시작 완료 이벤트 발송: transactionId={}", event.getTransactionId());

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("MES 시작 처리 실패: transactionId={}, mesId={}",
                    event.getTransactionId(), event.getMesId(), e);

            // 실패 이벤트 발송
            MesStartCompletionEvent completionEvent = MesStartCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .mesId(event.getMesId())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(MES_START_COMPLETION_TOPIC, event.getMesId(), completionEvent);

            acknowledgment.acknowledge();
        }
    }
}
