package org.ever._4ever_be_gw.business.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListItemDto {
    private String customerId;                 // UUID
    private String customerNumber;             // 고객 번호 ex) CUS-______ -> customerId의 앞 6자리
    private String customerName;               // 고객사명
    private CustomerManagerDto manager;        // 담당자 정보
    private String address;                    // 주소 요약
    private Long totalTransactionAmount;       // 총 거래 금액
    private Integer orderCount;                // 총 주문 건수
    private String lastOrderDate;              // 마지막 거래 일시
    private String statusCode;                 // ACTIVE / INACTIVE
}

