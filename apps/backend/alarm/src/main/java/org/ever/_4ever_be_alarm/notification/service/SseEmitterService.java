package org.ever._4ever_be_alarm.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterService {

    // 유저별 SseEmitter 추가, 반환
    SseEmitter addEmitter(String userId);

    void removeEmitter(String userId);

    // 특정 유저에게 이벤트 전송
    void sendEvent(String userId, String eventName, Object data);

}
