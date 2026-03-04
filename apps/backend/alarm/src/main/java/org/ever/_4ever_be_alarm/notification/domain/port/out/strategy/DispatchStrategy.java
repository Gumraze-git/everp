package org.ever._4ever_be_alarm.notification.domain.port.out.strategy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DispatchStrategy {
    SSE("sse"),
    APP_PUSH("app_push"),
    WEB_PUSH("web_push"),
    EMAIL("email"),
    UNKNOWN("unknown");     // 기본값 또는 처리 불가

    private final String beanName;
    
    public static DispatchStrategy fromString(String text) {
        for (DispatchStrategy b : DispatchStrategy.values()) {
            if (b.name().equalsIgnoreCase(text)) {
                return b;
            }
        }
        return UNKNOWN;
    }
}