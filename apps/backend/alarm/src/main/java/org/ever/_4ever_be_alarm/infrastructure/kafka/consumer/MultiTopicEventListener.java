package org.ever._4ever_be_alarm.infrastructure.kafka.consumer;

import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.BUSINESS_EVENT_TOPIC;
import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.PAYMENT_EVENT_TOPIC;
import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.SCM_EVENT_TOPIC;
import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.USER_EVENT_TOPIC;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.infrastructure.kafka.consumer.handler.MultiTopicEventHandler;
import org.ever.event.AlarmEvent;
import org.ever.event.BusinessEvent;
import org.ever.event.ScmEvent;
import org.ever.event.UserEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MultiTopicEventListener {

    private final ObjectMapper objectMapper;
    private final MultiTopicEventHandler multiTopicEventHandler;

    /**
     * 여러 토픽을 동시에 구독하는 Multi-topic Listener
     */
    @KafkaListener(
        topics = {USER_EVENT_TOPIC, SCM_EVENT_TOPIC, BUSINESS_EVENT_TOPIC, PAYMENT_EVENT_TOPIC},
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleMultiTopicEvents(
        @Payload String message,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment acknowledgment
    ) {
        log.info("[KAFKA-MULTI] 멀티 토픽 메시지 수신 - Topic: {}, Partition: {}, Offset: {}",
            topic, partition, offset);

        try {
            // 토픽별로 다른 처리
            switch (topic) {
                case USER_EVENT_TOPIC:
                    log.debug("[KAFKA-MULTI] USER_EVENT 처리 시작");
                    handleUserEvent(message);
                    break;
                case SCM_EVENT_TOPIC:
                    log.debug("[KAFKA-MULTI] SCM_EVENT 처리 시작");
                    handleScmEvent(message);
                    break;
                case BUSINESS_EVENT_TOPIC:
                    log.debug("[KAFKA-MULTI] BUSINESS_EVENT 처리 시작");
                    handleBusinessEvent(message);
                    break;
                case PAYMENT_EVENT_TOPIC:
                    log.debug("[KAFKA-MULTI] PAYMENT_EVENT 처리 시작");
                    handlePaymentEvent(message);
                    break;
                default:
                    log.warn("[KAFKA-MULTI] 알 수 없는 토픽 - Topic: {}", topic);
            }

            acknowledgment.acknowledge();
            log.info("[KAFKA-MULTI] 멀티 토픽 메시지 처리 완료 - Topic: {}", topic);

        } catch (Exception e) {
            log.error("[KAFKA-MULTI] 멀티 토픽 메시지 처리 실패 - Topic: {}, Error: {}",
                topic, e.getMessage(), e);
            // TODO: 에러 발생 시 재시도 또는 DLQ 전송 로직 추가 필요
        }
    }

    private void handleUserEvent(String message) {
        try {
            log.debug("[KAFKA-MULTI] 사용자 이벤트 파싱 시작");
            UserEvent event = objectMapper.readValue(message, UserEvent.class);
            log.info("[KAFKA-MULTI] 사용자 이벤트 처리 중 - UserId: {}, Action: {}", 
                event.getUserId(), event.getAction());

            multiTopicEventHandler.handleUserEvent(event);
            log.debug("[KAFKA-MULTI] 사용자 이벤트 처리 완료");

        } catch (Exception e) {
            log.error("[KAFKA-MULTI] 사용자 이벤트 파싱 실패 - Error: {}", e.getMessage(), e);
            // TODO: 파싱 실패 시 재처리 또는 DLQ 전송
            throw new RuntimeException("사용자 이벤트 처리 실패", e);
        }
    }

    private void handleScmEvent(String message) {
        try {
            log.debug("[KAFKA-MULTI] SCM 이벤트 파싱 시작");
            ScmEvent event = objectMapper.readValue(message, ScmEvent.class);
            log.info("[KAFKA-MULTI] SCM 이벤트 처리 중 - OrderId: {}, Action: {}", 
                event.getOrderId(), event.getAction());

            multiTopicEventHandler.handleScmEvent(event);
            log.debug("[KAFKA-MULTI] SCM 이벤트 처리 완료");

        } catch (Exception e) {
            log.error("[KAFKA-MULTI] SCM 이벤트 파싱 실패 - Error: {}", e.getMessage(), e);
            throw new RuntimeException("SCM 이벤트 처리 실패", e);
        }
    }

    private void handleBusinessEvent(String message) {
        try {
            log.debug("[KAFKA-MULTI] 비즈니스 이벤트 파싱 시작");
            BusinessEvent event = objectMapper.readValue(message, BusinessEvent.class);
            log.info("[KAFKA-MULTI] 비즈니스 이벤트 처리 중 - BusinessId: {}, Action: {}",
                event.getBusinessId(), event.getAction());

            multiTopicEventHandler.handleBusinessEvent(event);
            log.debug("[KAFKA-MULTI] 비즈니스 이벤트 처리 완료");

        } catch (Exception e) {
            log.error("[KAFKA-MULTI] 비즈니스 이벤트 파싱 실패 - Error: {}", e.getMessage(), e);
            throw new RuntimeException("비즈니스 이벤트 처리 실패", e);
        }
    }

    private void handlePaymentEvent(String message) {
        try {
            log.debug("[KAFKA-MULTI] 결제 이벤트 파싱 시작");
            AlarmEvent event = objectMapper.readValue(message, AlarmEvent.class);
            log.info("[KAFKA-MULTI] 결제 이벤트 처리 중 - EventId: {}, AlarmId: {}", 
                event.getEventId(), event.getAlarmId());

            multiTopicEventHandler.handleAlarmEvent(event);
            log.debug("[KAFKA-MULTI] 결제 이벤트 처리 완료");

        } catch (Exception e) {
            log.error("[KAFKA-MULTI] 결제 이벤트 파싱 실패 - Error: {}", e.getMessage(), e);
            throw new RuntimeException("결제 이벤트 처리 실패", e);
        }
    }
}
