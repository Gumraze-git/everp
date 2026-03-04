package org.ever._4ever_be_business.sd.repository;

import org.ever._4ever_be_business.sd.dto.response.TrendDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface SalesAnalyticsRepositoryCustom {
    /**
     * 주차별 매출 및 주문 수 트렌드 조회
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @return 주차별 트렌드 데이터
     */
    List<TrendDto> findWeeklyTrend(LocalDate startDate, LocalDate endDate);

    /**
     * 제품별 매출 집계 (Product ID별)
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @return Product ID -> 매출액 매핑
     */
    Map<String, BigDecimal> findProductSales(LocalDate startDate, LocalDate endDate);

    /**
     * 고객사별 매출 및 주문 수 TOP N 조회
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @param limit     조회할 개수
     * @return 고객사 코드, 고객사 이름, 주문 수, 매출액
     */
    List<Object[]> findTopCustomers(LocalDate startDate, LocalDate endDate, int limit);
}
