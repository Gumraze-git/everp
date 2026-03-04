package org.ever._4ever_be_business.sd.dao;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SalesStatisticsDAO {
    /**
     * 기간별 매출 금액 합계 조회
     */
    BigDecimal calculateTotalSalesAmount(LocalDate startDate, LocalDate endDate);

    /**
     * 기간별 신규 주문 수 조회
     */
    Long calculateNewOrdersCount(LocalDate startDate, LocalDate endDate);
}
