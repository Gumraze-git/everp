package org.ever._4ever_be_business.fcm.dao;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FcmStatisticsDAO {
    /**
     * 총 매출 계산 (SalesVoucher)
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 총 매출
     */
    BigDecimal calculateTotalSales(LocalDate startDate, LocalDate endDate);

    /**
     * 총 매입 계산 (PurchaseVoucher)
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 총 매입
     */
    BigDecimal calculateTotalPurchases(LocalDate startDate, LocalDate endDate);

    /**
     * 순이익 계산 (총 매출 - 총 매입)
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 순이익
     */
    BigDecimal calculateNetProfit(LocalDate startDate, LocalDate endDate);

    /**
     * 미수금 계산 (SalesVoucher에서 UNPAID, PARTIALLY_PAID, PARTIALLY_UNPAID 상태)
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 미수금
     */
    BigDecimal calculateAccountsReceivable(LocalDate startDate, LocalDate endDate);
}
