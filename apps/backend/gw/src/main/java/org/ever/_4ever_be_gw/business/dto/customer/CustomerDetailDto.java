package org.ever._4ever_be_gw.business.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailDto {
    private String customerId;              // 고객사 Id
    private String customerNumber;          // 고객사 번호
    private String customerName;            // 고객사 명
    private String ceoName;                 // 대표자 명
    private String businessNumber;          // 사업자번호
    private String statusCode;              // 상태 코드

    private String customerPhone;           // 고객사 전화번호
    private String customerEmail;           // 고객사 이메일
    private String baseAddress;             // 기본주소
    private String detailAddress;           // 상세주소

    private CustomerManagerDto manager;     // 담당자

    private Integer totalOrders;            // 총 주문 건수
    private Long totalTransactionAmount;    // 총 거래 금액
    private String note;                    // 비고
}

