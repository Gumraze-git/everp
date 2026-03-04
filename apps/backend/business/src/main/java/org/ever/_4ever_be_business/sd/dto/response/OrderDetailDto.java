package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDto {
    private String salesOrderId;
    private String salesOrderNumber;
    private String orderDate;        // YYYY-MM-DD
    private String dueDate;          // YYYY-MM-DD
    private String statusCode;
    private BigDecimal totalAmount;
}
