package org.ever._4ever_be_business.hr.service;

import org.ever._4ever_be_business.hr.dto.response.HRStatisticsResponseDto;

public interface StatisticsService {
    /**
     * HR 대시보드 통계 조회
     *
     * @return HRStatisticsResponseDto
     */
    HRStatisticsResponseDto getHRStatistics();
}
