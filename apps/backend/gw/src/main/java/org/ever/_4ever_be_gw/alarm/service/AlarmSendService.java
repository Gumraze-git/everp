package org.ever._4ever_be_gw.alarm.service;

import org.ever.event.AlarmSentEvent;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AlarmSendService {

    // 사용자별 SseEmitter 추가
    SseEmitter addEmitter(String userId);

    // 사용자별 SseEmitter 제거
    void removeEmitter(String userId);

    // 사용자별 알림 메시지 전송 (event: notification)
    void sendAlarmMessage(AlarmSentEvent event);

    // 사용자별 keepalive 전송 (event: keepalive)
    void sendKeepAlive(String userId);

    // 전체 브로드캐스트 keepalive (event: keepalive)
    void broadcastKeepAlive();

    // 사용자별 안 읽은 개수 전송 (event: unreadCount)
    void sendUnreadCount(String userId, long unreadCount);
}
