package org.ever._4ever_be_gw.business.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesAnalyticsResponseDto {
    private PeriodDto period;
    private BigDecimal totalSale;
    private Long totalOrders;
    private List<TrendPointDto> trend;
    private TrendScaleDto trendScale;
    private List<ProductShareDto> productShare;
    private List<TopCustomerDto> topCustomers;
}
