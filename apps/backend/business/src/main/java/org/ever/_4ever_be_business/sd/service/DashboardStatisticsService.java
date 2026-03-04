package org.ever._4ever_be_business.sd.service;

import org.ever._4ever_be_business.sd.dto.response.DashboardStatisticsDto;

public interface DashboardStatisticsService {
    /**
     * 대시보드 통계 조회 (주/월/분기/년)
     *
     * @return 대시보드 통계
     */
    DashboardStatisticsDto getDashboardStatistics();
}
