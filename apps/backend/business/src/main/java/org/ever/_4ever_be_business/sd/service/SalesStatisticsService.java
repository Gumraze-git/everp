package org.ever._4ever_be_business.sd.service;

import org.ever._4ever_be_business.sd.dto.response.DashboardStatisticsDto;
import org.ever._4ever_be_business.sd.dto.response.SalesStatisticsDto;
import org.ever._4ever_be_business.sd.vo.StatisticsSearchConditionVo;

public interface SalesStatisticsService {
    /**
     * 기간별 매출 통계 조회
     *
     * @param vo 검색 조건 (startDate, endDate)
     * @return SalesStatisticsDto
     */
    SalesStatisticsDto getSalesStatistics(StatisticsSearchConditionVo vo);

    /**
     * 주간/월간/분기/연간 매출 통계 조회
     *
     * @return DashboardStatisticsDto
     */
    DashboardStatisticsDto getPeriodStatistics();
}
