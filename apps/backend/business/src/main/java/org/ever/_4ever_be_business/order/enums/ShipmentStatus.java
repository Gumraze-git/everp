package org.ever._4ever_be_business.order.enums;

public enum ShipmentStatus {
    PENDING,              // 출고 대기(피킹/패킹 전)
    PACKED,               // 포장 완료(출고 준비 끝)
    LABEL_CREATED,        // 운송장 생성(택배사 전송 전)
    AWAITING_CARRIER,     // 집하 대기(택배사 수거 기다림)

    // 집하/이동
    PICKED_UP,            // 집하 완료(택배사가 수거)
    IN_TRANSIT,           // 운송 중(허브/터미널 간 이동)
    AT_SORT_FACILITY,     // 분류 터미널 도착
    DEPARTED_FACILITY,    // 분류 터미널 출발
    OUT_FOR_DELIVERY,     // 배송 출발(기사 배정/차량 탑재)

    // 배송 완료/예외
    DELIVERED,            // 배송 완료
    DELIVERY_ATTEMPTED,   // 배송 시도했으나 실패(부재 등)
    DELIVERY_DELAYED,     // 지연(기상/물류 이슈)
    HOLD,                 // 보류(수취인 요청/회사 요청)
    ADDRESS_ISSUE,        // 주소 문제(불명확/오기)
    DAMAGED,              // 파손 이슈
    LOST,                 // 분실 이슈

    // 반품/회송
    RETURN_REQUESTED,     // 반품 요청 접수
    RETURN_IN_TRANSIT,    // 반품 물류 이동 중
    RETURNED,             // 반품 도착(판매처 수령)
    RTO_IN_TRANSIT,       // 수취 불가로 발신지 회송 중(Return To Origin)
    RTO_COMPLETED,        // 회송 완료(원발송지 복귀)

    // 취소
    CANCELLED,            // 배송 취소(출고 전/중단)
}
