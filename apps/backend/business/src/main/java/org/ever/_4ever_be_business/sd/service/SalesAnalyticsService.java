package org.ever._4ever_be_business.sd.service;

import org.ever._4ever_be_business.sd.dto.response.SalesAnalyticsDto;

import java.time.LocalDate;

public interface SalesAnalyticsService {
    /**
     * 매출 분석 통계 데이터 조회
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @return 매출 분석 통계 데이터
     */
    SalesAnalyticsDto getSalesAnalytics(LocalDate startDate, LocalDate endDate);
}
