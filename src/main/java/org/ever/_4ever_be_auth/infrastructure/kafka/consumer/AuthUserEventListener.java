package org.ever._4ever_be_auth.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.saga.SagaTransactionManager;
import org.ever._4ever_be_auth.common.saga.SagaTransactionStatusService;
import org.ever._4ever_be_auth.infrastructure.kafka.config.KafkaTopicConfig;
import org.ever._4ever_be_auth.infrastructure.kafka.producer.KafkaProducerService;
import org.ever._4ever_be_auth.user.application.port.in.AuthUserSagaPort;
import org.ever.event.CreateAuthUserEvent;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthUserEventListener {

    private final SagaTransactionManager sagaTransactionManager;
    private final SagaTransactionStatusService sagaTransactionStatusService;
    private final AuthUserSagaPort authUserSagaPort;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(
            topics = KafkaTopicConfig.CREATE_USER_TOPIC,
            groupId = "auth-user-saga"
    )
    public void handleEvent(CreateAuthUserEvent event) {
        log.info("[KAFKA] CreateAuthUserEvent 수신 - transactionId: {}, userId: {}", event.getTransactionId(), event.getUserId());

        String transactionId = event.getTransactionId();

        if (transactionId == null || transactionId.isBlank()) {
            log.error("[KAFKA] 트랜잭션 ID가 없는 이벤트입니다.: {}", event);
            return;
        }

        if (!sagaTransactionStatusService.startProcessing(transactionId)) {
            log.warn("[KAFKA] 트랜잭션 {} 은 이미 처리된 상태입니다. 이벤트를 무시합니다.", transactionId);
            return;
        }

        sagaTransactionManager.executeSagaWithId(transactionId, () -> {
            try {
                CreateAuthUserResultEvent result = authUserSagaPort.handleCreateAuthUser(event);
                sagaTransactionStatusService.markCompleted(transactionId);
                kafkaProducerService.sendEventSync(
                        KafkaTopicConfig.AUTH_USER_RESULT_TOPIC,
                        result.getUserId(),
                        result
                );
                log.info("[KAFKA] 인증 사용자 생성 완료 이벤트 발행 완료 - transactionId: {}", transactionId);
            } catch (Exception error) {
                log.error("[KAFKA] 인증 사용자 생성 처리 실패 - transactionId: {}, cause: {}", transactionId, error.getMessage(), error);
                sagaTransactionStatusService.markFailed(transactionId);

                CreateAuthUserResultEvent failureEvent =
                        CreateAuthUserResultEvent.builder()
                                .eventId(event.getEventId())
                                .transactionId(event.getTransactionId())
                                .success(false)
                                .userId(event.getUserId())
                                .failureReason(error.getMessage())
                                .build();
                kafkaProducerService.sendEventSync(
                        KafkaTopicConfig.AUTH_USER_RESULT_TOPIC,
                        event.getUserId(),
                        failureEvent
                );
            }
            return null;
        });
    }
}
