package org.ever._4ever_be_business.voucher.enums;

public enum PurchaseVoucherStatus {
    CREATED,           // 바우처가 새로 생성된 상태 (초기 상태)
    APPROVED,          // 승인 완료된 상태 (지출 혹은 구매가 승인됨)
    REJECTED,          // 승인 요청이 거절된 상태 (결재 반려 등)
    CANCELLED,         // 거래나 바우처가 취소된 상태 (승인 후라도 취소 가능)

    PENDING,           // 대기 중 상태 (지급 기한 전, 미지급 상태)
    RESPONSE_PENDING,
    PAID,              // 전액 지급 완료된 상태 (완납)
    UNPAID,            // 아직 아무 금액도 지급되지 않은 상태 (미지급)
    OVERDUE,           // 지급 기한 초과, 미지급 상태 (미납)
    PARTIALLY_PAID,    // 일부 금액만 지급된 상태 (부분 지급)
    PARTIALLY_UNPAID,  // 일부 금액이 아직 미지급 상태 (PARTIALLY_PAID와 의미 동일하나 관점상 미지급 중심 표현)

    PARTIALLY_REFUNDED,// 일부 금액만 환불된 상태 (부분 환불)
    REFUNDED           // 전액 환불 완료된 상태
}
