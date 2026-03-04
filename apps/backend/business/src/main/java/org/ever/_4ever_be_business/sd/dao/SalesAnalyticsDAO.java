package org.ever._4ever_be_business.sd.dao;

import org.ever._4ever_be_business.sd.dto.response.TrendDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SalesAnalyticsDAO {
    /**
     * 주차별 매출 및 주문 수 트렌드 조회
     */
    List<TrendDto> findWeeklyTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 제품별 매출 집계
     */
    Map<String, BigDecimal> findProductSales(LocalDate startDate, LocalDate endDate);

    /**
     * 고객사별 매출 및 주문 수 TOP N 조회
     */
    List<Object[]> findTopCustomers(LocalDate startDate, LocalDate endDate, int limit);
}
