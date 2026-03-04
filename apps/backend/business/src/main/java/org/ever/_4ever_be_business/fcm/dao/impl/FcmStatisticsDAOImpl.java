package org.ever._4ever_be_business.fcm.dao.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dao.FcmStatisticsDAO;
import org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus;
import org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.ever._4ever_be_business.voucher.entity.QSalesVoucher.salesVoucher;
import static org.ever._4ever_be_business.voucher.entity.QPurchaseVoucher.purchaseVoucher;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FcmStatisticsDAOImpl implements FcmStatisticsDAO {

    private final JPAQueryFactory queryFactory;

    @Override
    public BigDecimal calculateTotalSales(LocalDate startDate, LocalDate endDate) {
        log.debug("총 매출 계산 - startDate: {}, endDate: {}", startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        BigDecimal result = queryFactory
                .select(salesVoucher.totalAmount.sum())
                .from(salesVoucher)
                .where(salesVoucher.issueDate.between(startDateTime, endDateTime))
                .fetchOne();

        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateTotalPurchases(LocalDate startDate, LocalDate endDate) {
        log.debug("총 매입 계산 - startDate: {}, endDate: {}", startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        BigDecimal result = queryFactory
                .select(purchaseVoucher.totalAmount.sum())
                .from(purchaseVoucher)
                .where(purchaseVoucher.issueDate.between(startDateTime, endDateTime))
                .fetchOne();

        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal calculateNetProfit(LocalDate startDate, LocalDate endDate) {
        log.debug("순이익 계산 - startDate: {}, endDate: {}", startDate, endDate);

        BigDecimal totalSales = calculateTotalSales(startDate, endDate);
        BigDecimal totalPurchases = calculateTotalPurchases(startDate, endDate);

        return totalSales.subtract(totalPurchases);
    }

    @Override
    public BigDecimal calculateAccountsReceivable(LocalDate startDate, LocalDate endDate) {
        log.debug("미수금 계산 - startDate: {}, endDate: {}", startDate, endDate);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // UNPAID, PARTIALLY_PAID, PARTIALLY_UNPAID 상태의 SalesVoucher 합계
        BigDecimal result = queryFactory
                .select(salesVoucher.totalAmount.sum())
                .from(salesVoucher)
                .where(
                        salesVoucher.issueDate.between(startDateTime, endDateTime)
                                .and(salesVoucher.status.in(
                                        SalesVoucherStatus.UNPAID,
                                        SalesVoucherStatus.PENDING,
                                        SalesVoucherStatus.RESPONSE_PENDING
                                ))
                )
                .fetchOne();

        return result != null ? result : BigDecimal.ZERO;
    }
}
