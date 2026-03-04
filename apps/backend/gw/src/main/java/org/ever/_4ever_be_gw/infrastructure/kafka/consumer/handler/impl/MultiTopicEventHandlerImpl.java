package org.ever._4ever_be_gw.infrastructure.kafka.consumer.handler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.service.AlarmSendService;
import org.ever._4ever_be_gw.infrastructure.kafka.consumer.handler.MultiTopicEventHandler;
import org.ever.event.AlarmEvent;
import org.ever.event.BusinessEvent;
import org.ever.event.ScmEvent;
import org.ever.event.UserEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MultiTopicEventHandlerImpl implements MultiTopicEventHandler {

    private final AlarmSendService alarmSendService;

    @Override
    public void handleUserEvent(UserEvent event) {
        // User 서비스에서 받은 이벤트 처리
        log.debug("사용자 이벤트 처리 완료 - Action: {}", event.getAction());

        // TODO: 실제 비즈니스 로직 구현
        // 1. 사용자 정보 업데이트
        // 2. 다른 서비스로 이벤트 라우팅
    }

    @Override
    public void handleScmEvent(ScmEvent event) {
        // SCM 서비스에서 받은 이벤트 처리
        log.debug("SCM 이벤트 처리 완료 - Action: {}", event.getAction());

        // TODO: 실제 비즈니스 로직 구현
        // 1. 재고 정보 확인
        // 2. 주문 상태 업데이트
    }

    @Override
    public void handleBusinessEvent(BusinessEvent event) {
        // Business 서비스에서 받은 이벤트 처리
        log.debug("비즈니스 이벤트 처리 완료 - Action: {}", event.getAction());

        // TODO: 실제 비즈니스 로직 구현
        // 1. 비즈니스 로직 처리
        // 2. 데이터 동기화
    }

    @Override
    public void handleAlarmEvent(AlarmEvent event) {
        log.info("알림 이벤트 수신 - eventId: {}, Title: {}, Message: {}",
            event.getEventId(), event.getTitle(), event.getMessage());

        // SSE를 통해 알림 전송
//        alarmSendService.sendAlarmMessage(
//            event.getUserId(),
//            "새 알림이 도착했습니다.",
//            event
//        );

        log.info("알림 이벤트 처리 완료 - eventId: {}", event.getEventId());
    }
}
