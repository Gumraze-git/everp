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
public class TrendPointDto {
    private int year;
    private int month;
    private int week;
    private BigDecimal sale;
    private Long orderCount;
}

