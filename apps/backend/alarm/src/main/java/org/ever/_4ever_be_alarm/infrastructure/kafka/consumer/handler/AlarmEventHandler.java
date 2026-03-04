package org.ever._4ever_be_alarm.infrastructure.kafka.consumer.handler;

import org.ever.event.AlarmEvent;

/**
 * Payment 이벤트 처리 핸들러 인터페이스
 */
public interface AlarmEventHandler {

    /**
     * 결제 요청 이벤트 처리
     */
    void handleAlarmRequest(AlarmEvent event);

    /**
     * 결제 완료 이벤트 처리
     */
    void handleAlarmComplete(AlarmEvent event);

    /**
     * 결제 취소 이벤트 처리
     */
    void handleAlarmCancel(AlarmEvent event);
}
