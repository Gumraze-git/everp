package org.ever._4ever_be_business.order.entity;

public enum OrderStatus {
    PENDING,              // 대기
    CONFIRMED,            // 확정
    IN_PRODUCTION,        // 생산중
    READY_FOR_SHIPMENT,   // 출고 준비
    MATERIAL_PREPARATION, // 자재 준비중
    DELIVERING,           // 배송중
    DELIVERED,            // 배송 완료
    CANCELLED             // 취소
}
