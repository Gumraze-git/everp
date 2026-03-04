package org.ever._4ever_be_alarm.notification.adapter.kafka.in;

import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.ALARM_REQUEST_STATUS_TOPIC;
import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.ALARM_REQUEST_TOPIC;
import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.ALARM_SENT_STATUS_TOPIC;
import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.ALARM_SENT_TOPIC;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.notification.domain.port.in.NotificationSendUseCase;
import org.ever.event.AlarmEvent;
import org.ever.event.AlarmSentEvent;
import org.ever.event.StatusEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {

    private final NotificationSendUseCase notificationSendUseCase;

    /**
     * Alarm 외 서버에서 Alarm Request 이벤트 리스너
     */
    @KafkaListener(
        topics = ALARM_REQUEST_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAlarmRequest(
        @Payload AlarmEvent event,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment acknowledgment
    ) {
        log.info("[KAFKA-RECEIVE] 이벤트 수신 - Topic: {}, Partition: {}, Offset: {}, AlarmId: {}",
            topic, partition, offset, event.getAlarmId());

        try {
            // 알림 생성 처리
            log.info("[KAFKA-PROCESS] 알림 생성 시작 - AlarmId: {}, TargetType: {}",
                event.getAlarmId(), event.getTargetType());
            
            UUID notificationId = notificationSendUseCase.createNotification(event);
            
            log.info("[KAFKA-PROCESS] 알림 생성 완료 - AlarmId: {}, NotificationId: {}",
                event.getAlarmId(), notificationId);

            // 수동 커밋
            acknowledgment.acknowledge();
            log.info("[KAFKA-COMMIT] 오프셋 커밋 완료 - Topic: {}, Partition: {}, Offset: {}",
                topic, partition, offset);

        } catch (IllegalArgumentException e) {
            log.error("[KAFKA-ERROR] 잘못된 인자 - AlarmId: {}, error: {}",
                event.getAlarmId(), e.getMessage(), e);
            // TODO: 잘못된 메시지는 즉시 커밋하고 종료 (무한 재시도 방지)
            acknowledgment.acknowledge();
        } catch (UnsupportedOperationException e) {
            log.error("[KAFKA-ERROR] 지원하지 않는 기능 - AlarmId: {}, error: {}",
                event.getAlarmId(), e.getMessage(), e);
            // TODO: 지원하지 않는 기능은 즉시 커밋하고 종료
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("[KAFKA-ERROR] 예상치 못한 오류 - AlarmId: {}, error: {}",
                event.getAlarmId(), e.getMessage(), e);
            // acknowledgment를 호출하지 않으면 메시지가 재처리됨
            // TODO: 최대 재시도 횟수 제한 로직 추가 필요
            log.warn("[KAFKA-RETRY] 메시지 재처리를 위해 커밋하지 않음 - Topic: {}, Offset: {}",
                topic, offset);
        }
    }

    /**
     * 알림 발송 요청 결과 이벤트 리스너
     */
    @KafkaListener(
        topics = ALARM_SENT_STATUS_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotificationStatus(
        @Payload StatusEvent event,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment acknowledgment
    ) {
        log.info("[KAFKA-RECEIVE] 알람 발송 결과 이벤트 수신 - Topic: {}, Partition: {}, Offset: {}, EventId: {}",
            topic, partition, offset, event.getEventId());

        try {
            log.info("[KAFKA-PROCESS] 알람 발송 결과 처리 시작 - EventId: {}", event.getEventId());

            // TODO: 알람 발송 실패 처리 로직 구현
            log.debug("[KAFKA-PROCESS] 알람 발송 결과 처리 중 - EventId: {}", event.getEventId());

            acknowledgment.acknowledge();
            log.info("[KAFKA-COMMIT] 오프셋 커밋 완료 - Topic: {}, Partition: {}, Offset: {}",
                topic, partition, offset);
            log.info("[KAFKA-PROCESS] 알람 발송 결과 처리 완료 - EventId: {}", event.getEventId());

        } catch (Exception e) {
            log.error("[KAFKA-ERROR] 알람 발송 결과 이벤트 처리 실패 - EventId: {}, Error: {}",
                event.getEventId(), e.getMessage(), e);
            log.warn("[KAFKA-RETRY] 메시지 재처리를 위해 커밋하지 않음 - Topic: {}, Offset: {}",
                topic, offset);
            // TODO: 최대 재시도 횟수 제한 로직 추가 필요
        }
    }

    /**
     * TODO : ALARM -> ALARM 외 서버
     * 현재는 불필요한 상황 알림이 중요한 서비스는 아니기 때문에 구현 보류
     *
     * Alarm 외 서버에서 Alarm Request의 결과 이벤트 리스너
     */
    @KafkaListener(
        topics = ALARM_REQUEST_STATUS_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAlarmRequestResult(
        @Payload StatusEvent event,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
        @Header(KafkaHeaders.OFFSET) long offset,
        Acknowledgment acknowledgment
    ) {
        log.info("[KAFKA-RECEIVE] 알람 요청 결과 이벤트 수신 - Topic: {}, Partition: {}, Offset: {}, EventId: {}",
            topic, partition, offset, event.getEventId());

        try {
            log.info("[KAFKA-PROCESS] 알람 요청 결과 처리 시작 - EventId: {}", event.getEventId());

            // TODO: 알람 요청 결과 처리 로직 구현
            log.debug("[KAFKA-PROCESS] 알람 요청 결과 처리 중 - EventId: {}", event.getEventId());

            acknowledgment.acknowledge();
            log.info("[KAFKA-COMMIT] 오프셋 커밋 완료 - Topic: {}, Partition: {}, Offset: {}",
                topic, partition, offset);
            log.info("[KAFKA-PROCESS] 알람 요청 결과 처리 완료 - EventId: {}", event.getEventId());

        } catch (Exception e) {
            log.error("[KAFKA-ERROR] 알람 요청 결과 이벤트 처리 실패 - EventId: {}, Error: {}",
                event.getEventId(), e.getMessage(), e);
            log.warn("[KAFKA-RETRY] 메시지 재처리를 위해 커밋하지 않음 - Topic: {}, Offset: {}",
                topic, offset);
            // TODO: 최대 재시도 횟수 제한 로직 추가 필요
        }
    }


    /**
     * TODO : ALARM -> GW
     *
     * 알림 발송 요청 이벤트 리스너
     */
    @KafkaListener(
        topics = ALARM_SENT_TOPIC,
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleNotificationRequest(
        @Payload AlarmSentEvent event,
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, // 기본 제공 : 토픽 이름
        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition, // 기본 제공 : 파티션 번호
        @Header(KafkaHeaders.OFFSET) long offset, // 기본 제공 : 오프셋 번호
        Acknowledgment acknowledgment
    ) {
        log.info("[KAFKA-RECEIVE] 알림 발송 이벤트 수신 - Topic: {}, Partition: {}, Offset: {}, EventId: {}",
            topic, partition, offset, event.getEventId());

        try {
            log.info("[KAFKA-PROCESS] 알림 발송 처리 시작 - EventId: {}, AlarmId: {}",
                event.getEventId(), event.getAlarmId());

            // TODO: 알림 발송 로직 구현
            log.debug("[KAFKA-PROCESS] 알림 발송 처리 중 - EventId: {}", event.getEventId());

            log.info("[KAFKA-PROCESS] 알림 발송 처리 완료 - EventId: {}", event.getEventId());

            acknowledgment.acknowledge();
            log.info("[KAFKA-COMMIT] 오프셋 커밋 완료 - Topic: {}, Partition: {}, Offset: {}",
                topic, partition, offset);

        } catch (Exception e) {
            log.error("[KAFKA-ERROR] 알림 발송 이벤트 처리 실패 - EventId: {}, Error: {}",
                event.getEventId(), e.getMessage(), e);
            log.warn("[KAFKA-RETRY] 메시지 재처리를 위해 커밋하지 않음 - Topic: {}, Offset: {}",
                topic, offset);
            // TODO: 최대 재시도 횟수 제한 로직 추가 필요
        }
    }

}
