package org.ever._4ever_be_business.infrastructure.kafka.producer.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever.event.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public CompletableFuture<SendResult<String, Object>> sendPaymentEvent(String topic, PaymentEvent event) {
        return sendEvent(topic, event.getPaymentId(), event);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendUserEvent(UserEvent event) {
        return sendEvent(USER_EVENT_TOPIC, event.getUserId(), event);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendScmEvent(ScmEvent event) {
        return sendEvent(SCM_EVENT_TOPIC, event.getOrderId(), event);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendBusinessEvent(BusinessEvent event) {
        return sendEvent(BUSINESS_EVENT_TOPIC, event.getBusinessId(), event);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendAlarmEvent(AlarmEvent event) {
        return sendEvent(ALARM_REQUEST_TOPIC, event.getAlarmId(), event);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendToTopic(String topic, String key, Object event) {
        return sendEvent(topic, key, event);
    }

    @Override
    public void sendCreateUserEvent(CreateUserEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(CREATE_USER_TOPIC, event.getEventId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("사용자 생성 이벤트 전송 성공: {}, offset: {}",
                        event.getEventId(), result.getRecordMetadata().offset());
                } else {
                    log.error("사용자 생성 이벤트 전송 실패: {}", ex.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Kafka 메시지 전송 중 오류 발생: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.KAFKA_PRODUCER_ERROR,
                    "사용자 생성 이벤트 전송 중 오류가 발생했습니다");
        }
    }

    @Override
    public void sendEventSync(String topic, String key, Object event) {
        try {
            SendResult<String, Object> result = sendEvent(topic, key, event).get();
            log.info("동기 이벤트 발행 완료 - Topic: {}, Partition: {}, Offset: {}",
                topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("동기 이벤트 발행 실패 - Topic: {}, Key: {}", topic, key, e);
            throw new RuntimeException("Kafka 메시지 전송 실패", e);
        }
    }

    /**
     * 공통 이벤트 발행 메서드
     */
    private CompletableFuture<SendResult<String, Object>> sendEvent(String topic, String key, Object event) {
        log.info("이벤트 발행 시작 - Topic: {}, Key: {}, EventType: {}",
            topic, key, event.getClass().getSimpleName());

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("이벤트 발행 실패 - Topic: {}, Key: {}, Error: {}",
                    topic, key, ex.getMessage(), ex);
            } else {
                log.info("이벤트 발행 성공 - Topic: {}, Partition: {}, Offset: {}",
                    topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            }
        });

        return future;
    }
}
