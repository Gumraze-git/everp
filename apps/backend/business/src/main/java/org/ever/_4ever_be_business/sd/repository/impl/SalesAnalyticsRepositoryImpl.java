package org.ever._4ever_be_business.sd.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.sd.dto.response.TrendDto;
import org.ever._4ever_be_business.sd.repository.SalesAnalyticsRepositoryCustom;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.ever._4ever_be_business.company.entity.QCustomerCompany.customerCompany;
import static org.ever._4ever_be_business.order.entity.QOrder.order;
import static org.ever._4ever_be_business.order.entity.QQuotation.quotation;
import static org.ever._4ever_be_business.order.entity.QQuotationItem.quotationItem;
import static org.ever._4ever_be_business.voucher.entity.QSalesVoucher.salesVoucher;

@Repository
@RequiredArgsConstructor
public class SalesAnalyticsRepositoryImpl implements SalesAnalyticsRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TrendDto> findWeeklyTrend(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // YEAR, MONTH, WEEK 계산
        NumberExpression<Integer> year = Expressions.numberTemplate(Integer.class,
                "EXTRACT(YEAR FROM {0})", salesVoucher.issueDate);
        NumberExpression<Integer> month = Expressions.numberTemplate(Integer.class,
                "EXTRACT(MONTH FROM {0})", salesVoucher.issueDate);
        NumberExpression<Integer> week = Expressions.numberTemplate(Integer.class,
                "EXTRACT(WEEK FROM {0})", salesVoucher.issueDate);

        return queryFactory
                .select(Projections.constructor(
                        TrendDto.class,
                        year,
                        month,
                        week,
                        salesVoucher.totalAmount.sum().coalesce(BigDecimal.ZERO),
                        salesVoucher.count()
                ))
                .from(salesVoucher)
                .where(salesVoucher.issueDate.between(startDateTime, endDateTime))
                .groupBy(year, month, week)
                .orderBy(year.asc(), month.asc(), week.asc())
                .fetch();
    }

    @Override
    public Map<String, BigDecimal> findProductSales(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        // SalesVoucher -> Order -> Quotation -> QuotationItem 조인하여 productId별 매출 집계
        var results = queryFactory
                .select(
                        quotationItem.productId,
                        quotationItem.price.multiply(quotationItem.count).sum()
                )
                .from(salesVoucher)
                .innerJoin(order).on(salesVoucher.order.id.eq(order.id))
                .innerJoin(quotation).on(order.quotation.id.eq(quotation.id))
                .innerJoin(quotationItem).on(quotationItem.quotation.id.eq(quotation.id))
                .where(salesVoucher.issueDate.between(startDateTime, endDateTime))
                .groupBy(quotationItem.productId)
                .fetch();

        // Convert Long productId to String
        return results.stream()
                .collect(Collectors.toMap(
                        tuple -> String.valueOf(tuple.get(quotationItem.productId)),
                        tuple -> tuple.get(quotationItem.price.multiply(quotationItem.count).sum())
                ));
    }

    @Override
    public List<Object[]> findTopCustomers(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        var tuples = queryFactory
                .select(
                        customerCompany.companyCode,
                        customerCompany.companyName,
                        salesVoucher.count(),
                        salesVoucher.totalAmount.sum(),
                        customerCompany.isActive
                )
                .from(salesVoucher)
                .innerJoin(customerCompany).on(salesVoucher.customerCompany.id.eq(customerCompany.id))
                .where(salesVoucher.issueDate.between(startDateTime, endDateTime))
                .groupBy(customerCompany.id, customerCompany.companyCode, customerCompany.companyName, customerCompany.isActive)
                .orderBy(salesVoucher.totalAmount.sum().desc())
                .limit(limit)
                .fetch();

        return tuples.stream()
                .map(tuple -> new Object[]{
                        tuple.get(customerCompany.companyCode),
                        tuple.get(customerCompany.companyName),
                        tuple.get(salesVoucher.count()),
                        tuple.get(salesVoucher.totalAmount.sum()),
                        tuple.get(customerCompany.isActive)
                })
                .collect(Collectors.toList());
    }
}
