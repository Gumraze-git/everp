package org.ever._4ever_be_business.hr.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.integration.port.UserServicePort;
import org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever.event.CreateAuthUserEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

// 사가 코레오그래피 발화자로 Auth 서버에 내부 사용자 생성을 요청하는 이벤트를
// 카프카로 발행하여 사가 흐름을 시작함.
// 분산 트랜잭션을 직접 조정(오케스트레이션)하지 않고, 자신의 단계(발행)만 수행함.

@Slf4j
@Component("kafkaUserServicePort")
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.hr.user-service.adapter", havingValue = "kafka", matchIfMissing = true)
public class KafkaUserServiceAdapter implements UserServicePort {

    private final KafkaProducerService kafkaProducerService;

    // CompletableFuture: 비동기 프로그래밍을 지원하는 클래스임.
    // - 코레오그래피에서는 발생 성공(브로커 저장)만 보장하면 충분함.
    // - 최종 성공/실패는 완료 이벤트(process-completed)에서 확정함.
    @Override
    public CompletableFuture<Void> createAuthUserPort(
            CreateAuthUserEvent event
    ) {
        try {
            // 파티션 키: 모든 이벤트가 동일한 파티션을 라우팅 되도록 보장하는 키
            // 파티션 키로 사용자 ID를 사용함.
            String key = event.getUserId();

            // 동기 발행(sendEventSync)로 카프카 메시지 발생
            kafkaProducerService.sendEventSync(
                    KafkaTopicConfig.CREATE_USER_TOPIC,
                    key,
                    event
            );

            log.info("[KAFKA] 내부 사용자 생성 요청 발행 완료 - transactionId: {}, eventId: {}, key: {}, email: {}",
                    event.getTransactionId(), event.getEventId(), key, event.getEmail());

            return CompletableFuture.completedFuture(null);
        } catch (Exception error) {
            log.error("[KAFKA] 내부 사용자 생성 이벤트 발행 실패 - transactionId: {}, error: {}",
                    event.getTransactionId(), error.getMessage(), error);
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(error);
            return future;
        }
    }
}
