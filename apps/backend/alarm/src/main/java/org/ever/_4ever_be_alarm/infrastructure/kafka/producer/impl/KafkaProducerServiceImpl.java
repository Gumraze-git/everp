package org.ever._4ever_be_alarm.infrastructure.kafka.producer.impl;

import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.ALARM_SENT_TOPIC;
import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.BUSINESS_EVENT_TOPIC;
import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.SCM_EVENT_TOPIC;
import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.USER_EVENT_TOPIC;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.infrastructure.kafka.producer.KafkaProducerService;
import org.ever.event.AlarmEvent;
import org.ever.event.BusinessEvent;
import org.ever.event.ScmEvent;
import org.ever.event.UserEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public CompletableFuture<SendResult<String, Object>> sendAlarmEvent(String topic,
        AlarmEvent event) {
        log.debug("[KAFKA-PRODUCER] 알림 이벤트 전송 시작 - Topic: {}, AlarmId: {}", 
            topic, event.getAlarmId());
        return sendEvent(topic, event.getAlarmId(), event);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendUserEvent(UserEvent event) {
        log.debug("[KAFKA-PRODUCER] 사용자 이벤트 전송 시작 - UserId: {}, Action: {}", 
            event.getUserId(), event.getAction());
        return sendEvent(USER_EVENT_TOPIC, event.getUserId(), event);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendScmEvent(ScmEvent event) {
        log.debug("[KAFKA-PRODUCER] SCM 이벤트 전송 시작 - OrderId: {}, Action: {}", 
            event.getOrderId(), event.getAction());
        return sendEvent(SCM_EVENT_TOPIC, event.getOrderId(), event);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendBusinessEvent(BusinessEvent event) {
        log.debug("[KAFKA-PRODUCER] 비즈니스 이벤트 전송 시작 - BusinessId: {}, Action: {}", 
            event.getBusinessId(), event.getAction());
        return sendEvent(BUSINESS_EVENT_TOPIC, event.getBusinessId(), event);
    }

    @Override
    public CompletableFuture<SendResult<String, Object>> sendAlarmEvent(AlarmEvent event) {
        log.debug("[KAFKA-PRODUCER] 알림 이벤트 전송 시작 - EventId: {}, AlarmId: {}", 
            event.getEventId(), event.getAlarmId());
        return sendEvent(ALARM_SENT_TOPIC, event.getEventId(), event);
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
    private CompletableFuture<SendResult<String, Object>> sendEvent(String topic, String key,
        Object event) {
        log.info("[KAFKA-PRODUCER] 이벤트 발행 시작 - Topic: {}, Key: {}, EventType: {}",
            topic, key, event.getClass().getSimpleName());

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key,
                event);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("[KAFKA-PRODUCER] 이벤트 발행 실패 - Topic: {}, Key: {}, Error: {}",
                        topic, key, ex.getMessage(), ex);
                } else {
                    log.info("[KAFKA-PRODUCER] 이벤트 발행 성공 - Topic: {}, Partition: {}, Offset: {}",
                        topic, result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });

            return future;
        } catch (Exception e) {
            log.error("[KAFKA-PRODUCER] 이벤트 발행 중 예외 발생 - Topic: {}, Key: {}, Error: {}",
                topic, key, e.getMessage(), e);
            throw e;
        }
    }
}
