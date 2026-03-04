package org.ever._4ever_be_business.sd.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchConditionVo {
    private String customerId;  // 고객사 사용자 ID로 필터링 (CUSTOMER 유저용)
    private String employeeId;  // 내부 직원 ID로 필터링 (EMPLOYEE 유저용)
    private String startDate;  // YYYY-MM-DD
    private String endDate;    // YYYY-MM-DD
    private String search;     // 검색어
    private String type;       // salesOrderNumber, customerName, managerName
    private String status;     // ALL, MATERIAL_PREPARATION, IN_PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED
}
