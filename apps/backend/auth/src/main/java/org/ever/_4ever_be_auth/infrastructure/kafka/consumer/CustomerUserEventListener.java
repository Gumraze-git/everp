package org.ever._4ever_be_auth.infrastructure.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.saga.SagaTransactionManager;
import org.ever._4ever_be_auth.common.saga.SagaTransactionStatusService;
import org.ever._4ever_be_auth.infrastructure.kafka.config.KafkaTopicConfig;
import org.ever._4ever_be_auth.infrastructure.kafka.producer.KafkaProducerService;
import org.ever._4ever_be_auth.user.application.port.in.CustomerUserSagaPort;
import org.ever.event.CreateAuthUserResultEvent;
import org.ever.event.CreateCustomerUserEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerUserEventListener {

    private final SagaTransactionManager sagaTransactionManager;
    private final SagaTransactionStatusService sagaTransactionStatusService;
    private final CustomerUserSagaPort customerUserSagaPort;
    private final KafkaProducerService kafkaProducerService;

    @KafkaListener(
            topics = KafkaTopicConfig.CREATE_CUSTOMER_USER_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleEvent(
            CreateCustomerUserEvent event,
            Acknowledgment acknowledgement
    ) {
        log.info("[INFO][KAFKA][CUSTOMER] CreateCustomerUserEvent 수신 - txId: {}, userId: {}", event.getTransactionId(), event.getUserId());

        String transactionId = event.getTransactionId();

        if (transactionId == null || transactionId.isBlank()) {
            log.error("[ERROR][KAFKA][CUSTOMER] 트랜잭션 ID가 없는 이벤트입니다.: {}", event);
            acknowledgement.acknowledge(); // 수동 커밋
            return;
        }

        if (!sagaTransactionStatusService.startProcessing(transactionId)) {
            log.warn("[WARN][KAFKA][CUSTOMER] 트랜잭션 {} 은 이미 처리된 상태입니다. 이벤트를 무시합니다.", transactionId);
            acknowledgement.acknowledge(); // 수동 커밋
            return;
        }

        sagaTransactionManager.executeSagaWithId(transactionId, () -> {
            try {
                CreateAuthUserResultEvent result = customerUserSagaPort.handleCreateCustomerUser(event);
                sagaTransactionStatusService.markCompleted(transactionId);
                kafkaProducerService.sendEventSync(
                        KafkaTopicConfig.CUSTOMER_USER_RESULT_TOPIC,
                        result.getUserId(),
                        result
                );
                log.info("[INFO][KAFKA][CUSTOMER] 고객사 사용자 생성 완료 이벤트 발행 - txId: {}", transactionId);
                acknowledgement.acknowledge(); // 수동 커밋
            } catch (Exception error) {
                log.error("[ERROR][KAFKA][CUSTOMER] 고객사 사용자 생성 처리 실패 - txId: {}, cause: {}", transactionId, error.getMessage(), error);
                sagaTransactionStatusService.markFailed(transactionId);

                CreateAuthUserResultEvent failureEvent = CreateAuthUserResultEvent.builder()
                        .eventId(event.getEventId())
                        .transactionId(event.getTransactionId())
                        .success(false)
                        .userId(event.getUserId())
                        .failureReason(error.getMessage())
                        .build();
                kafkaProducerService.sendEventSync(
                        KafkaTopicConfig.CUSTOMER_USER_RESULT_TOPIC,
                        event.getUserId(),
                        failureEvent
                );
                acknowledgement.acknowledge(); // 수동 커밋
            }
            return null;
        });
    }
}
