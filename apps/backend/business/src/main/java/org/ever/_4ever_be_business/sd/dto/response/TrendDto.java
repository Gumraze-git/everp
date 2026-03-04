package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrendDto {
    private int year;
    private int month;
    private int week;
    private BigDecimal sale;
    private Long orderCount;
}
