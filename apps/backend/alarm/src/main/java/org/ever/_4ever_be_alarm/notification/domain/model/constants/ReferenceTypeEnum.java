package org.ever._4ever_be_alarm.notification.domain.model.constants;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 참조 문서 유형을 나타내는 열거형
 * 각 유형은 다양한 비즈니스 문서 형식을 나타냅니다.
 * 예: 견적서, 청구서, 구매 요청서 등
 * PR (구매부)
 * _PURCHASE_REQUEST(구매 요청서)
 * _PURCHASE_ORDER(발주서)
 * _ETC (기타) - 화면 이동 없음
 *
 * SD (영업부)
 * _QUOTATION(견적서)
 * _SALES_ORDER(주문서)
 * _ETC(기타) - 화면 이동 없음
 *
 * IM (재고부)
 * _ETC(재고 부족, 입고, 출고, 기타) - 화면 이동 없음
 *
 * FCM (재무부)
 * _SALES_INVOICE(매출 청구서, 만기일)
 * _PURCHASE_INVOICE(매입 청구서, 만기일)
 * _ETC(기타) - 화면 이동 없음
 *
 * HRM (인사부)
 * _ETC(휴가 신청서, 휴가 승인서, 급여 명세서, 교육, 기타) - 화면 이동 없음
 *
 * PP (생산부)
 * _ESTIMATE(견적서)
 * _INSUFFICIENT_STOCK(가용 재고 부족)
 * _ETC(기타) - 화면 이동 없음
 */
public enum ReferenceTypeEnum {
    // PR (구매부)
    PURCHASE_REQUISITION,
    PURCHASE_ORDER,
    PR_ETC,

    // SD (영업부)
    QUOTATION,
    SALES_ORDER,
    SD_ETC,

    // IM (재고부)
    IM_ETC,

    // FCM (재무부)
    SALES_INVOICE,
    PURCHASE_INVOICE,
    FCM_ETC,

    // HRM (인사부)
    HRM_ETC,

    // PP (생산부)
    ESTIMATE,
    INSUFFICIENT_STOCK,
    PP_ETC,

    UNKNOWN;

    // String -> Enum 변환을 위한 맵 (대소문자 무시, 초기화 시 생성)
    private static final Map<String, ReferenceTypeEnum> stringToEnum =
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
    public static ReferenceTypeEnum fromString(String name) {
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
