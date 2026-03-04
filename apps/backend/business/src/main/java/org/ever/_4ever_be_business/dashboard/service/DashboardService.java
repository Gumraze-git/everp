package org.ever._4ever_be_business.dashboard.service;

import org.ever._4ever_be_business.dashboard.dto.response.DashboardStatisticsResponseDto;

public interface DashboardService {
    /**
     * Get comprehensive dashboard statistics for all periods
     * Includes: total sales, purchases, profit, and employee count
     * with absolute delta values (not percentages) compared to previous periods
     *
     * @return Dashboard statistics for week/month/quarter/year
     */
    DashboardStatisticsResponseDto getDashboardStatistics();
}
