package org.ever._4ever_be_business.sd.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.sd.repository.SalesStatisticsRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.ever._4ever_be_business.order.entity.QOrder.order;
import static org.ever._4ever_be_business.voucher.entity.QSalesVoucher.salesVoucher;

@Repository
@RequiredArgsConstructor
public class SalesStatisticsRepositoryImpl implements SalesStatisticsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public BigDecimal calculateTotalSalesAmount(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

        BigDecimal totalAmount = queryFactory
                .select(salesVoucher.totalAmount.sum())
                .from(salesVoucher)
                .where(
                        startDateTime != null ? salesVoucher.issueDate.goe(startDateTime) : null,
                        endDateTime != null ? salesVoucher.issueDate.lt(endDateTime) : null
                )
                .fetchOne();

        return totalAmount != null ? totalAmount : BigDecimal.ZERO;
    }

    @Override
    public Long calculateNewOrdersCount(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : null;

        Long count = queryFactory
                .select(order.count())
                .from(order)
                .where(
                        startDateTime != null ? order.orderDate.goe(startDateTime) : null,
                        endDateTime != null ? order.orderDate.lt(endDateTime) : null
                )
                .fetchOne();

        return count != null ? count : 0L;
    }
}
