package org.ever._4ever_be_alarm.infrastructure.kafka.consumer.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.infrastructure.kafka.consumer.handler.AlarmEventHandler;
import org.ever.event.AlarmEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmEventHandlerImpl implements AlarmEventHandler {

    @Override
    public void handleAlarmRequest(AlarmEvent event) {
        log.info("[ALARM-HANDLER] 알림 요청 처리 시작 - EventId: {}, AlarmId: {}",
            event.getEventId(), event.getAlarmId());

        try {
            // TODO: 실제 비즈니스 로직 구현
            // 1. 알림 정보 검증
            // 2. 알림 발송
            // 3. DB 저장
            log.debug("[ALARM-HANDLER] 알림 요청 처리 중 - EventId: {}", event.getEventId());
            log.info("[ALARM-HANDLER] 알림 요청 처리 완료 - EventId: {}", event.getEventId());

        } catch (Exception e) {
            log.error("[ALARM-HANDLER] 알림 요청 처리 실패 - EventId: {}, AlarmId: {}, Error: {}",
                event.getEventId(), event.getAlarmId(), e.getMessage(), e);
            throw new RuntimeException("알림 요청 처리 실패", e);
        }
    }

    @Override
    public void handleAlarmComplete(AlarmEvent event) {
        log.info("[ALARM-HANDLER] 알림 완료 처리 시작 - AlarmId: {}", event.getAlarmId());

        try {
            // TODO: 실제 비즈니스 로직 구현
            // 1. 알림 완료 상태 업데이트
            // 2. 관련 서비스에 알림
            // 3. 사용자에게 알림 발송
            log.debug("[ALARM-HANDLER] 알림 완료 처리 중 - AlarmId: {}", event.getAlarmId());
            log.info("[ALARM-HANDLER] 알림 완료 처리 완료 - AlarmId: {}", event.getAlarmId());

        } catch (Exception e) {
            log.error("[ALARM-HANDLER] 알림 완료 처리 실패 - AlarmId: {}, Error: {}",
                event.getAlarmId(), e.getMessage(), e);
            throw new RuntimeException("알림 완료 처리 실패", e);
        }
    }

    @Override
    public void handleAlarmCancel(AlarmEvent event) {
        log.info("[ALARM-HANDLER] 알림 취소 처리 시작 - AlarmId: {}", event.getAlarmId());

        try {
            // TODO: 실제 비즈니스 로직 구현
            // 1. 알림 취소 처리
            // 2. 롤백 처리
            // 3. 상태 업데이트
            log.debug("[ALARM-HANDLER] 알림 취소 처리 중 - AlarmId: {}", event.getAlarmId());
            log.info("[ALARM-HANDLER] 알림 취소 처리 완료 - AlarmId: {}", event.getAlarmId());

        } catch (Exception e) {
            log.error("[ALARM-HANDLER] 알림 취소 처리 실패 - AlarmId: {}, Error: {}",
                event.getAlarmId(), e.getMessage(), e);
            throw new RuntimeException("알림 취소 처리 실패", e);
        }
    }
}
