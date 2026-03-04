package org.ever._4ever_be_business.sd.dao;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DashboardStatisticsDAO {
    /**
     * 기간별 매출 금액 합계 조회
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @return 매출 금액 합계
     */
    BigDecimal calculateSalesAmount(LocalDate startDate, LocalDate endDate);

    /**
     * 기간별 신규 주문 수 조회
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @return 신규 주문 수
     */
    Long calculateNewOrdersCount(LocalDate startDate, LocalDate endDate);
}
