package org.ever._4ever_be_gw.alarm.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.service.AlarmSendService;
import org.ever.event.AlarmSentEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Slf4j
@RequiredArgsConstructor
public class AlarmSendServiceImpl implements AlarmSendService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1 hour

    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @Override
    public SseEmitter addEmitter(String userId) {
        log.info("[SSE][EMITTER-ADD-START] userId={}", userId);

        // 이미 존재하는 Emitter가 있다면 제거
        SseEmitter existingEmitter = emitterMap.remove(userId);
        if (existingEmitter != null) {
            log.info("[SSE][EMITTER-REPLACE] 기존 Emitter 제거 - userId={}, oldEmitterHash={}",
                userId, System.identityHashCode(existingEmitter));
            try {
                log.warn("기존 SSE Emitter가 존재하여 종료 처리 - UserId: {}, oldEmitterHash={}",
                    userId, System.identityHashCode(existingEmitter));
                existingEmitter.complete();
            } catch (Exception e) {
                log.warn("기존 SSE Emitter 종료 중 오류 - UserId: {}, oldEmitterHash={}",
                    userId, System.identityHashCode(existingEmitter), e);
            }
        }

        // 새로운 Emitter 생성
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        final int emitterHash = System.identityHashCode(emitter);
        log.debug("[SSE][EMITTER-CREATED] userId={}, emitterHash={}, timeoutMs={}",
            userId, emitterHash, DEFAULT_TIMEOUT);

        // 완료 콜백 등록
        emitter.onCompletion(() -> {
            log.info("[SSE][EMITTER-COMPLETED] userId={}, emitterHash={}", userId, emitterHash);
            emitterMap.remove(userId);
        });

        // 타임아웃 콜백 등록
        emitter.onTimeout(() -> {
            log.warn("[SSE][EMITTER-TIMEOUT] userId={}, emitterHash={}", userId, emitterHash);
            emitterMap.remove(userId);
            log.warn("[SSE][EMITTER-TIMEOUT-COMPLETE] userId={}, emitterHash={}",
                userId, emitterHash);
            emitter.complete();
        });

        // 에러 콜백 등록
        emitter.onError((ex) -> {
            log.error("[SSE][EMITTER-ERROR] userId={}, emitterHash={}, msg={}",
                userId, emitterHash, ex.getMessage(), ex);
            emitterMap.remove(userId);
        });

        // Emitter 저장
        emitterMap.put(userId, emitter);
        log.info("[SSE][EMITTER-ADD-END] userId={}, emitterHash={}, activeConnections={}",
            userId, emitterHash, emitterMap.size());

        // 초기 keep-alive 이벤트 전송 (연결 즉시 수립 확인)
        try {
            SseEmitter.SseEventBuilder initEvent = SseEmitter.event()
                .name("keepalive")
                .data("connected");
            emitter.send(initEvent);

            log.debug("[SSE][INIT-EVENT-SENT] userId={}, emitterHash={}, event=keepalive",
                userId, emitterHash);
        } catch (Exception e) {
            log.warn("[SSE][INIT-EVENT-FAIL] userId={}, emitterHash={}, msg={}",
                userId, emitterHash, e.getMessage(), e);

            emitterMap.remove(userId);
        }

        return emitter;
    }

    @Override
    public void removeEmitter(String userId) {
        log.info("SSE Emitter 제거 - UserId: {}", userId);
        SseEmitter emitter = emitterMap.remove(userId);
        if (emitter != null) {
            try {
                log.warn("SSE Emitter 종료 처리 - UserId: {}", userId);
                emitter.complete();
            } catch (Exception e) {
                log.warn("SSE Emitter 종료 중 오류 - UserId: {}", userId, e);
            }
        }
    }

    @Override
    public void sendAlarmMessage(AlarmSentEvent event) {
        log.info("알림 메시지 전송 시작 - AlarmId: {}, Message: {}", event.getAlarmId(), event.getMessage());

        SseEmitter emitter = emitterMap.get(event.getTargetId());
        if (emitter == null) {
            log.warn("SSE Emitter를 찾을 수 없음 - targetId: {}", event.getTargetId());
            return;
        }

        try {
            // 데이터를 JSON으로 변환
            String jsonData = objectMapper.writeValueAsString(event);

            // SSE 메시지 전송
            SseEmitter.SseEventBuilder sseEvent = SseEmitter.event()
                .name("alarm")
                .data(jsonData);

            emitter.send(sseEvent);
            log.info("알림 메시지 전송 성공 - event=alarm, targetId: {}", event.getTargetId());

        } catch (IOException e) {
            log.error("알림 메시지 전송 실패 - targetId: {}", event.getTargetId(), e);
            emitterMap.remove(event.getTargetId());
            try {
                log.error("SSE Emitter 에러 처리 - targetId: {}",
                    event.getTargetId());
                emitter.completeWithError(e);
            } catch (Exception ex) {
                log.warn("SSE Emitter 에러 처리 중 오류 - targetId: {}", event.getTargetId(), e);
            }
        }
    }

    @Override
    public void sendKeepAlive(String userId) {
        SseEmitter emitter = emitterMap.get(userId);
        if (emitter == null) {
            log.debug("[SSE][KEEPALIVE-SKIP] emitter not found - userId={}", userId);
            return;
        }
        try {
            emitter.send(SseEmitter.event().name("keepalive").data("ping"));
            log.debug("[SSE][KEEPALIVE-SENT] userId={}", userId);
        } catch (Exception e) {
            log.warn("[SSE][KEEPALIVE-FAIL] userId={}, msg={}", userId, e.getMessage(), e);
            emitterMap.remove(userId);
            try {
                emitter.completeWithError(e);
            } catch (Exception ignore) {
            }
        }
    }

    @Override
    @Scheduled(fixedRate = 20000)
    public void broadcastKeepAlive() {
//        log.debug("[SSE][BROADCAST-KEEPALIVE] activeConnections={}", emitterMap.size());
        emitterMap.forEach((userId, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("keepalive").data("ping"));
            } catch (Exception e) {
                log.warn("[SSE][BROADCAST-KEEPALIVE-FAIL] userId={}, msg={}", userId,
                    e.getMessage(), e);
                emitterMap.remove(userId);
                try {
                    emitter.completeWithError(e);
                } catch (Exception ignore) {
                }
            }
        });
    }

    @Override
    public void sendUnreadCount(String userId, long unreadCount) {
        SseEmitter emitter = emitterMap.get(userId);
        if (emitter == null) {
            log.debug("[SSE][UNREADCOUNT-SKIP] emitter not found - userId={}", userId);
            return;
        }
        try {
            emitter.send(SseEmitter.event().name("unreadCount").data(unreadCount));
            log.info("[SSE][UNREADCOUNT-SENT] userId={}, count={}", userId, unreadCount);
        } catch (Exception e) {
            log.warn("[SSE][UNREADCOUNT-FAIL] userId={}, msg={}", userId, e.getMessage(), e);
            emitterMap.remove(userId);
            try {
                emitter.completeWithError(e);
            } catch (Exception ignore) {
            }
        }
    }
}
