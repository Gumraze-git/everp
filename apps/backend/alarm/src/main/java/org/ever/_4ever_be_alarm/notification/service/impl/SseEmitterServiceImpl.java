package org.ever._4ever_be_alarm.notification.service.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.notification.service.SseEmitterService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Service
@Slf4j
public class SseEmitterServiceImpl implements SseEmitterService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    //    @Value("${sse.emitter.timeout:3600_000}") // 기본값 1시간
    private final Long emitterTimeout = 3600_000L;

    @Override
    public SseEmitter addEmitter(String userId) {
        log.info("[SSE] Emitter 추가 시작 - UserId: {}", userId);

        try {
            SseEmitter emitter = new SseEmitter(emitterTimeout);

            log.debug("[SSE] Emitter 생성 완료 - UserId: {}, Timeout: {}ms", userId, emitterTimeout);
            emitters.put(userId, emitter);

            emitter.onCompletion(() -> {
                emitters.remove(userId);
                log.info("[SSE] Emitter 완료 및 제거 - UserId: {}", userId);
            });

            emitter.onTimeout(() -> {
                emitters.remove(userId);
                log.warn("[SSE] Emitter 타임아웃 및 제거 - UserId: {}, Timeout: {}ms",
                    userId, emitterTimeout);
            });

            emitter.onError((e) -> {
                emitters.remove(userId);
                log.error("[SSE] Emitter 에러 발생 및 제거 - UserId: {}, Error: {}",
                    userId, e.getMessage(), e);
            });

            log.info("[SSE] Emitter 추가 완료 - UserId: {}", userId);
            return emitter;

        } catch (Exception e) {
            log.error("[SSE] Emitter 추가 실패 - UserId: {}, Error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("SSE Emitter 추가 실패", e);
        }
    }

    @Override
    public void removeEmitter(String userId) {
        log.info("[SSE] Emitter 제거 시작 - UserId: {}", userId);

        try {
            if (emitters.containsKey(userId)) {
                emitters.remove(userId);
                log.info("[SSE] Emitter 제거 완료 - UserId: {}", userId);
            } else {
                log.warn("[SSE] 제거할 Emitter가 존재하지 않음 - UserId: {}", userId);
            }
        } catch (Exception e) {
            log.error("[SSE] Emitter 제거 실패 - UserId: {}, Error: {}", userId, e.getMessage(), e);
        }
    }

    @Override
    public void sendEvent(String userId, String eventName, Object data) {
        log.debug("[SSE] 이벤트 전송 시작 - UserId: {}, EventName: {}", userId, eventName);

        if (!emitters.containsKey(userId)) {
            log.warn("[SSE] 해당 UserId의 Emitter가 존재하지 않음 - UserId: {}", userId);
            return;
        }

        SseEmitter emitter = emitters.get(userId);

        try {
            SseEventBuilder sseEventBuilder = SseEmitter.event()
                .name(eventName)
                .data(data);

            emitter.send(sseEventBuilder);
            log.info("[SSE] 이벤트 전송 완료 - UserId: {}, EventName: {}", userId, eventName);

        } catch (IOException e) {
            log.error("[SSE] 이벤트 전송 실패 - UserId: {}, EventName: {}, Error: {}",
                userId, eventName, e.getMessage(), e);

            emitter.completeWithError(e);
            emitters.remove(userId);
            log.info("[SSE] Emitter 제거 완료 - UserId: {}", userId);

        } catch (Exception e) {
            log.error("[SSE] 예상치 못한 오류 - UserId: {}, EventName: {}, Error: {}",
                userId, eventName, e.getMessage(), e);
            throw new RuntimeException("SSE 이벤트 전송 실패", e);
        }
    }
}
