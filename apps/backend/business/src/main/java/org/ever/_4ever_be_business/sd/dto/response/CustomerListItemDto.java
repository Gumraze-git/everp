package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListItemDto {
    private String customerId;
    private String customerNumber;  // customerCode -> customerNumber
    private String customerName;    // companyName -> customerName
    private CustomerManagerDto manager;
    private String address;
    private BigDecimal totalTransactionAmount;  // transactionAmount -> totalTransactionAmount
    private Long orderCount;
    private String lastOrderDate;
    private String statusCode;  // status -> statusCode
}
