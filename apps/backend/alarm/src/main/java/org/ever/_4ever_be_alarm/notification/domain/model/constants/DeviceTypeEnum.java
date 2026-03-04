package org.ever._4ever_be_alarm.notification.domain.model.constants;

public enum DeviceTypeEnum {
    IOS,
    ANDROID,
    WEB,
    UNKNOWN; // 알 수 없음

    /**
     * [내부 메소드] String을 Enum으로 변환합니다. (대소문자 무시)
     *
     * @param value 변환할 문자열 (예: "ios", "Ios", "IOS")
     * @return 일치하는 DeviceTypeEnum 상수 (예: DeviceTypeEnum.IOS)
     * 일치하는 항목이 없으면 UNKNOWN을 반환합니다.
     */
    public static DeviceTypeEnum fromString(String value) {
        if (value == null) {
            return UNKNOWN;
        }

        try {
            return DeviceTypeEnum.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }

    /**
     * [내부 메소드] Enum을 String으로 변환합니다.
     * (기본 name()과 동일하지만 명시적인 메소드를 제공)
     *
     * @return Enum 상수의 이름 문자열 (예: "IOS", "ANDROID", "WEB")
     */
    public String getValue() {
        return this.name();
    }
}
