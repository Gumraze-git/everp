package org.ever._4ever_be_alarm.notification.adapter.kafka.out;

import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.ALARM_SENT_TOPIC;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.common.util.UuidGenerator;
import org.ever._4ever_be_alarm.notification.domain.model.Noti;
import org.ever._4ever_be_alarm.notification.domain.port.out.NotificationDispatchPort;
import org.ever.event.AlarmSentEvent;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@RequiredArgsConstructor
public class NotificationEventProducerAdapter implements NotificationDispatchPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void dispatch(Noti notification) {
        log.info("[DISPATCH-KAFKA] 알림 이벤트 전송 시작 - NotificationId: {}, Title: {}",
            notification.getId(), notification.getTitle());

        try {
            // Notification을 AlarmEvent로 변환
            log.debug("[DISPATCH-KAFKA] 이벤트 변환 시작 - NotificationId: {}", notification.getId());
            AlarmSentEvent event = convertToAlarmSentEvent(notification);
            log.debug("[DISPATCH-KAFKA] 이벤트 변환 완료 - EventId: {}", event.getEventId());

            // Gateway로 Kafka 이벤트 전송
            log.debug("[DISPATCH-KAFKA] Kafka 전송 시작 - Topic: {}, EventId: {}",
                ALARM_SENT_TOPIC, event.getEventId());

            kafkaTemplate.send(ALARM_SENT_TOPIC, event.getAlarmId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("[DISPATCH-KAFKA] 알림 이벤트 전송 실패 - NotificationId: {}, Error: {}",
                            notification.getId(), ex.getMessage(), ex);
                        // TODO: 실패 처리 로직 추가 (재시도 또는 DLQ)
                    } else {
                        log.info(
                            "[DISPATCH-KAFKA] 알림 이벤트 전송 성공 - NotificationId: {}, Partition: {}, Offset: {}",
                            notification.getId(), result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset());
                    }
                });

        } catch (Exception e) {
            log.error("[DISPATCH-KAFKA] 알림 이벤트 전송 중 오류 발생 - NotificationId: {}, Error: {}",
                notification.getId(), e.getMessage(), e);
            throw new RuntimeException("알림 이벤트 전송 실패", e);
        }
    }

    private AlarmSentEvent convertToAlarmSentEvent(Noti notification) {
        return AlarmSentEvent.builder()
            .eventId(UuidGenerator.generateV7Random().toString())
            .eventType(AlarmSentEvent.class.getName())
            .timestamp(LocalDateTime.now())
            .source("ALARM")
            .alarmId(notification.getId().toString())
            .alarmType(notification.getSource().name())
            .targetId(notification.getTargetId().toString())
            .title(notification.getTitle())
            .message(notification.getMessage())
            .linkId(notification.getReferenceId().toString())
            .linkType(notification.getReferenceType().name())
            .build();
    }
}
