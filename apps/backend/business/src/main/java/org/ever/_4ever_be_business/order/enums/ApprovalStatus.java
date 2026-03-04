package org.ever._4ever_be_business.order.enums;

public enum ApprovalStatus {
    PENDING,              // 대기
    REVIEW,               // 검토중
    APPROVAL,             // 승인 (APPROVED 대신 사용)
    REJECTED,             // 거부
    READY_FOR_SHIPMENT    // 출고 준비 (Order 생성 완료)
}
