package org.ever._4ever_be_alarm.infrastructure.kafka.consumer.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.infrastructure.kafka.consumer.handler.MultiTopicEventHandler;
import org.ever._4ever_be_alarm.notification.service.SseEmitterService;
import org.ever.event.AlarmEvent;
import org.ever.event.BusinessEvent;
import org.ever.event.ScmEvent;
import org.ever.event.UserEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MultiTopicEventHandlerImpl implements MultiTopicEventHandler {

    private final SseEmitterService sseEmitterService;

    @Override
    public void handleUserEvent(UserEvent event) {
        log.info("[EVENT-HANDLER] 사용자 이벤트 처리 시작 - UserId: {}, Action: {}", 
            event.getUserId(), event.getAction());

        try {
            // TODO: 실제 비즈니스 로직 구현
            // 1. 사용자 정보 업데이트
            // 2. 알림 발송
            log.debug("[EVENT-HANDLER] 사용자 이벤트 처리 중 - UserId: {}", event.getUserId());
            log.info("[EVENT-HANDLER] 사용자 이벤트 처리 완료 - UserId: {}", event.getUserId());

        } catch (Exception e) {
            log.error("[EVENT-HANDLER] 사용자 이벤트 처리 실패 - UserId: {}, Error: {}",
                event.getUserId(), e.getMessage(), e);
            throw new RuntimeException("사용자 이벤트 처리 실패", e);
        }
    }

    @Override
    public void handleScmEvent(ScmEvent event) {
        log.info("[EVENT-HANDLER] SCM 이벤트 처리 시작 - OrderId: {}, Action: {}", 
            event.getOrderId(), event.getAction());

        try {
            // TODO: 실제 비즈니스 로직 구현
            // 1. 재고 정보 확인
            // 2. 주문 상태 업데이트
            log.debug("[EVENT-HANDLER] SCM 이벤트 처리 중 - OrderId: {}", event.getOrderId());
            log.info("[EVENT-HANDLER] SCM 이벤트 처리 완료 - OrderId: {}", event.getOrderId());

        } catch (Exception e) {
            log.error("[EVENT-HANDLER] SCM 이벤트 처리 실패 - OrderId: {}, Error: {}",
                event.getOrderId(), e.getMessage(), e);
            throw new RuntimeException("SCM 이벤트 처리 실패", e);
        }
    }

    @Override
    public void handleBusinessEvent(BusinessEvent event) {
        log.info("[EVENT-HANDLER] 비즈니스 이벤트 처리 시작 - BusinessId: {}, Action: {}", 
            event.getBusinessId(), event.getAction());

        try {
            // TODO: 실제 비즈니스 로직 구현
            // 1. 비즈니스 로직 처리
            // 2. 데이터 동기화
            log.debug("[EVENT-HANDLER] 비즈니스 이벤트 처리 중 - BusinessId: {}", event.getBusinessId());
            log.info("[EVENT-HANDLER] 비즈니스 이벤트 처리 완료 - BusinessId: {}", event.getBusinessId());

        } catch (Exception e) {
            log.error("[EVENT-HANDLER] 비즈니스 이벤트 처리 실패 - BusinessId: {}, Error: {}",
                event.getBusinessId(), e.getMessage(), e);
            throw new RuntimeException("비즈니스 이벤트 처리 실패", e);
        }
    }

    @Override
    public void handleAlarmEvent(AlarmEvent event) {
        log.info("[EVENT-HANDLER] 알림 이벤트 처리 시작 - EventId: {}, Type: {}", 
            event.getEventId(), event.getAlarmType());

        try {
            // TODO: 실제 비즈니스 로직 구현
            // 1. 알림 전송 확인
            // 2. 알림 이력 저장
            log.debug("[EVENT-HANDLER] 알림 이벤트 처리 중 - EventId: {}", event.getEventId());
            
            // TODO: SSE 발송 로직 구현
            // sseEmitterService.sendEvent(event.getUserId(), "alarm-event", event);
            
            log.info("[EVENT-HANDLER] 알림 이벤트 처리 완료 - EventId: {}", event.getEventId());

        } catch (Exception e) {
            log.error("[EVENT-HANDLER] 알림 이벤트 처리 실패 - EventId: {}, Error: {}",
                event.getEventId(), e.getMessage(), e);
            throw new RuntimeException("알림 이벤트 처리 실패", e);
        }
    }
}
