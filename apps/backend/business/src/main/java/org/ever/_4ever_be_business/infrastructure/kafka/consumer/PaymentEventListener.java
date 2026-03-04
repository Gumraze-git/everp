package org.ever._4ever_be_business.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.infrastructure.kafka.consumer.handler.PaymentEventHandler;
import org.ever.event.PaymentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final PaymentEventHandler paymentEventHandler;

    /**
     * Payment Event 이벤트 리스너 (BUSINESS 모듈에서 Payment 서비스의 이벤트를 수신)
     */
    @KafkaListener(
        topics = PAYMENT_EVENT_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentEvent(
        @Payload PaymentEvent event,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.RECEIVED_PARTITION) long offset,
        Acknowledgment acknowledgment
    ) {
        try {
            log.info("결제 이벤트 수신 - Topic: {}, Partition: {}, Offset: {}, PaymentId: {}",
                topic, partition, offset, event.getPaymentId());

            // 결제 이벤트 처리 로직 (BUSINESS 모듈에서 필요한 처리)
            paymentEventHandler.handlePaymentRequest(event);

            // 수동 커밋
            acknowledgment.acknowledge();
            log.info("결제 이벤트 처리 완료 - PaymentId: {}", event.getPaymentId());

        } catch (Exception e) {
            log.error("결제 이벤트 처리 실패 - PaymentId: {}", event.getPaymentId(), e);
            // 에러 처리 로직
            // acknowledgment를 호출하지 않으면 메시지가 재처리됨
        }
    }
}
