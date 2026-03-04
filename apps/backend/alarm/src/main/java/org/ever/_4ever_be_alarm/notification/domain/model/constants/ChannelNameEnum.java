package org.ever._4ever_be_alarm.notification.domain.model.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ChannelNameEnum {
    SSE, // 서버 발송 이벤트
    FCM_PUSH, // FCM 푸시
    WEB_PUSH, // 웹 푸시
    EMAIL, // 이메일
    SMS, // 문자 메시지
    SLACK, // 슬랙
    WEBHOOK, // 웹훅
    UNKNOWN; // 알 수 없음

    // String -> Enum 변환을 위한 맵 (대소문자 무시, 초기화 시 생성)
    private static final Map<String, ChannelNameEnum> stringToEnum =
        Arrays.stream(values())
            // Enum 이름을 대문자로 변환하여 키로 사용
            .collect(Collectors.toMap(en -> en.name().toUpperCase(), Function.identity()));

    /**
     * [내부 메소드] String을 Enum으로 변환합니다. (대소문자 무시)
     *
     * @param value 변환할 문자열 (예: "email", "Email", "EMAIL")
     * @return 일치하는 ChannelNameEnum 상수 (예: ChannelNameEnum.EMAIL)
     * 일치하는 항목이 없으면 null을 반환합니다.
     */
    public static ChannelNameEnum fromString(String value) {
        if (value == null) {
            return UNKNOWN;
        }

        // 입력 문자열을 대문자로 변환하여 맵에서 찾음
        return stringToEnum.getOrDefault(value.toUpperCase(), UNKNOWN);
    }

    /**
     * [내부 메소드] Enum을 String으로 변환합니다.
     * (기본 name()과 동일하지만 명시적인 메소드를 제공)
     *
     * @return Enum 상수의 이름 문자열 (예: "SSE", "PUSH_NOTIFICATION")
     */
    public String getValue() {
        return this.name();
    }
}
