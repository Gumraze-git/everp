package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDetailDto {
    private String customerId;
    private String customerNumber;
    private String customerName;
    private String ceoName;
    private String businessNumber;
    private String statusCode;
    private String customerPhone;
    private String customerEmail;
    private String baseAddress;
    private String detailAddress;
    private CustomerManagerDto manager;
    private Long totalOrders;         // NEW: 총 주문 수
    private BigDecimal totalTransactionAmount;  // NEW: 총 거래 금액
    private String note;
}
