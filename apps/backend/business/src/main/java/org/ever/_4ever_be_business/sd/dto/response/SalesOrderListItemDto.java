package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderListItemDto {
    private String salesOrderId;
    private String salesOrderNumber;
    private String customerName;
    private CustomerManagerDto manager;
    private String orderDate;      // YYYY-MM-DD format
    private String dueDate;         // YYYY-MM-DD format
    private BigDecimal totalAmount;
    private String statusCode;
}
