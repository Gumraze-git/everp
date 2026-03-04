package org.ever._4ever_be_alarm.notification.domain.model.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum SourceTypeEnum {
    //    AUTH,
//    GATEWAY,
//    BUSINESS,
//    SCM,
//    PAYMENT,
//    ALARM,
//    UNKNOWN;
    PR, // Purchase Request, 구매부
    SD, // Sales Document, 영업부
    IM, // Inventory Management, 재고부
    FCM, // Financial Management, 재무부
    HRM, // Human Resource Management, 인사부
    PP, // Production Planning, 생산부
    CUS, // Customer, 고객사
    SUP, // Supplier, 공급사
    UNKNOWN; // 알 수 없음

    // String -> Enum 변환을 위한 맵 (대소문자 무시, 초기화 시 생성)
    private static final Map<String, SourceTypeEnum> stringToEnum =
        Arrays.stream(values())
            // Enum 이름을 대문자로 변환하여 키로 사용
            .collect(Collectors.toMap(en -> en.name().toUpperCase(), Function.identity()));

    /**
     * 문자열을 해당하는 Enum 상수로 변환합니다.
     * 대소문자를 구분하지 않습니다.
     *
     * @param name Enum 상수의 이름 문자열
     * @return 해당하는 Enum 상수 Optional (없으면 Optional.empty())
     */
    public static SourceTypeEnum fromString(String name) {
        if (name == null) {
            return UNKNOWN;
        }
        // 입력 문자열을 대문자로 변환하여 맵에서 찾음
        return stringToEnum.getOrDefault(name.toUpperCase(), UNKNOWN);
    }

    /**
     * Enum 상수를 문자열(Enum 이름)로 변환합니다.
     * Java의 기본 Enum.toString() 또는 Enum.name()과 동일한 기능을 제공합니다.
     * 명시적으로 필요할 경우 사용합니다.
     *
     * @return Enum 상수의 이름 문자열
     */
    public String toStringValue() {
        return name();
    }
}
