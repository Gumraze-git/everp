package org.ever._4ever_be_business.sd.integration.adapter;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever._4ever_be_business.sd.integration.port.CustomerUserServicePort;
import org.ever.event.CreateCustomerUserEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 고객사 사용자 계정 생성 요청을 Kafka로 발행하는 어댑터.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.sd.customer-user.adapter", havingValue = "kafka", matchIfMissing = true)
public class KafkaCustomerUserServiceAdapter implements CustomerUserServicePort {

    private final KafkaProducerService kafkaProducerService;

    @Override
    public CompletableFuture<Void> createCustomerUser(CreateCustomerUserEvent event) {
        try {
            String key = event.getUserId();

            kafkaProducerService.sendEventSync(
                KafkaTopicConfig.CREATE_CUSTOMER_USER_TOPIC,
                key,
                event
            );

            log.info("[KAFKA] 고객사 사용자 생성 요청 발행 완료 - txId: {}, eventId: {}, key: {}, email: {}",
                event.getTransactionId(), event.getEventId(), key, event.getManagerEmail());

            return CompletableFuture.completedFuture(null);
        } catch (Exception error) {
            log.error("[KAFKA] 고객사 사용자 생성 이벤트 발행 실패 - txId: {}, cause: {}",
                event.getTransactionId(), error.getMessage(), error);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(error);
            return future;
        }
    }
}
