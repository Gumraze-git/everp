//package org.ever._4ever_be_alarm.infrastructure.kafka.consumer;
//
//import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.ALARM_FAILED_TOPIC;
//import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.ALARM_REQUEST_TOPIC;
//import static org.ever._4ever_be_alarm.infrastructure.kafka.config.KafkaTopicConfig.ALARM_SENT_TOPIC;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.ever._4ever_be_alarm.infrastructure.kafka.consumer.handler.AlarmEventHandler;
//import org.ever.event.AlarmEvent;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.kafka.support.KafkaHeaders;
//import org.springframework.messaging.handler.annotation.Header;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class AlarmEventListener {
//
//    private final AlarmEventHandler alarmEventHandler;
//
//    /**
//     * Alarm Request 이벤트 리스너
//     */
//    @KafkaListener(
//        topics = ALARM_REQUEST_TOPIC,
//        groupId = "${spring.kafka.consumer.group-id}",
//        containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void handleAlarmRequest(
//        @Payload AlarmEvent event,
//        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
//        @Header(KafkaHeaders.OFFSET) long offset,
//        Acknowledgment acknowledgment
//    ) {
//        try {
//            log.info("알람 요청 이벤트 수신 - Topic: {}, Partition: {}, Offset: {}, AlarmId: {}",
//                topic, partition, offset, event.getAlarmId());
//
//            // 알람 요청 처리 로직
//            alarmEventHandler.handleAlarmRequest(event);
//
//            // 수동 커밋
//            acknowledgment.acknowledge();
//            log.info("알람 요청 이벤트 처리 완료 - AlarmId: {}", event.getAlarmId());
//
//        } catch (Exception e) {
//            log.error("알람 요청 이벤트 처리 실패 - AlarmId: {}", event.getAlarmId(), e);
//            // 에러 처리 로직
//            // acknowledgment를 호출하지 않으면 메시지가 재처리됨
//        }
//    }
//
//    /**
//     * Alarm Sent 이벤트 리스너
//     */
//    @KafkaListener(
//        topics = ALARM_SENT_TOPIC,
//        groupId = "${spring.kafka.consumer.group-id}",
//        containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void handleAlarmSent(
//        @Payload AlarmEvent event,
//        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//        Acknowledgment acknowledgment
//    ) {
//        try {
//            log.info("알람 발송 완료 이벤트 수신 - Topic: {}, AlarmId: {}", topic, event.getAlarmId());
//
//            // 알람 발송 완료 후처리 로직
//            alarmEventHandler.handleAlarmComplete(event);
//
//            acknowledgment.acknowledge();
//            log.info("알람 발송 완료 이벤트 처리 완료 - AlarmId: {}", event.getAlarmId());
//
//        } catch (Exception e) {
//            log.error("알람 발송 완료 이벤트 처리 실패 - AlarmId: {}", event.getAlarmId(), e);
//        }
//    }
//
//    /**
//     * Alarm Failed 이벤트 리스너
//     */
//    @KafkaListener(
//        topics = ALARM_FAILED_TOPIC,
//        groupId = "${spring.kafka.consumer.group-id}",
//        containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void handleAlarmFailed(
//        @Payload AlarmEvent event,
//        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
//        Acknowledgment acknowledgment
//    ) {
//        try {
//            log.info("알람 발송 실패 이벤트 수신 - Topic: {}, AlarmId: {}", topic, event.getAlarmId());
//
//            // 알람 발송 실패 처리 로직
//            alarmEventHandler.handleAlarmCancel(event);
//
//            acknowledgment.acknowledge();
//            log.info("알람 발송 실패 이벤트 처리 완료 - AlarmId: {}", event.getAlarmId());
//
//        } catch (Exception e) {
//            log.error("알람 발송 실패 이벤트 처리 실패 - AlarmId: {}", event.getAlarmId(), e);
//        }
//    }
//}
