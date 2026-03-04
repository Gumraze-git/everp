package org.ever._4ever_be_gw.business.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopCustomerDto {
    private String customerNumber;
    private String customerName;
    private Long orderCount;
    private BigDecimal sale;
    private Boolean active;
}

