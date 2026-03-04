package org.ever._4ever_be_business.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Dashboard statistics for a specific period (week/month/quarter/year)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardPeriodStatisticsDto {
    /**
     * Total sales amount (accumulated)
     */
    @JsonProperty("total_sales")
    private DashboardValueDto totalSales;

    /**
     * Total purchase amount (accumulated)
     */
    @JsonProperty("total_purchases")
    private DashboardValueDto totalPurchases;

    /**
     * Net profit (total sales - total purchases)
     */
    @JsonProperty("net_profit")
    private DashboardValueDto netProfit;

    /**
     * Total employee count (accumulated)
     */
    @JsonProperty("total_employees")
    private DashboardValueDto totalEmployees;
}
