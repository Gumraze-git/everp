package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDto {
    private String customerNumber;
    private String customerName;
    private Long orderCount;
    private BigDecimal sale;
    private Boolean active;
}
