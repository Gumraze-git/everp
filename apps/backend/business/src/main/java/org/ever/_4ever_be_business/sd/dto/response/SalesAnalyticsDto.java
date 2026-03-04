package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsDto {
    private PeriodInfoDto period;
    private BigDecimal totalSale;
    private Long totalOrders;
    private List<TrendDto> trend;
    private TrendScaleDto trendScale;
    private List<ProductShareDto> productShare;
    private List<TopCustomerDto> topCustomers;
}
