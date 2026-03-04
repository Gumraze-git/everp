package org.ever._4ever_be_business.company.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.company.repository.CustomerCompanyRepositoryCustom;
import org.ever._4ever_be_business.sd.dto.response.CustomerContactDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerDetailDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListItemDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerManagerDto;
import org.ever._4ever_be_business.sd.vo.CustomerSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.company.entity.QCustomerCompany.customerCompany;
import static org.ever._4ever_be_business.hr.entity.QCustomerUser.customerUser;
import static org.ever._4ever_be_business.order.entity.QOrder.order;
import static org.ever._4ever_be_business.voucher.entity.QSalesVoucher.salesVoucher;

@Repository
@RequiredArgsConstructor
public class CustomerCompanyRepositoryImpl implements CustomerCompanyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<CustomerDetailDto> findCustomerDetailById(String customerId) {
        // 총 거래 금액 서브쿼리 (고객사별 총 매출액)
        var totalTransactionAmountSubQuery = JPAExpressions
                .select(salesVoucher.totalAmount.sum().coalesce(BigDecimal.ZERO))
                .from(salesVoucher)
                .where(salesVoucher.customerCompany.id.eq(customerCompany.id));

        // 총 주문 수 서브쿼리
        var totalOrdersSubQuery = JPAExpressions
                .select(order.count())
                .from(order)
                .where(order.customerUserId.eq(customerUser.id));

        // CustomerCompany와 CustomerUser 조인하여 조회
        var result = queryFactory
                .select(Projections.constructor(
                        CustomerDetailDto.class,
                        customerCompany.id,                    // customerId
                        customerCompany.companyCode,           // customerNumber
                        customerCompany.companyName,           // customerName
                        customerCompany.ceoName,               // ceoName
                        customerCompany.businessNumber,        // businessNumber
                        Expressions.stringTemplate(
                                "CASE WHEN {0} = true THEN 'ACTIVE' ELSE 'INACTIVE' END",
                                customerCompany.isActive
                        ),                                     // statusCode
                        customerCompany.officePhone,           // customerPhone
                        customerCompany.officeEmail,           // customerEmail
                        customerCompany.baseAddress,           // baseAddress
                        customerCompany.detailAddress,         // detailAddress
                        Projections.constructor(
                                CustomerManagerDto.class,
                                customerUser.customerName,     // managerName
                                customerUser.phoneNumber,      // managerPhone
                                customerUser.email             // managerEmail
                        ),
                        totalOrdersSubQuery,                   // totalOrders
                        totalTransactionAmountSubQuery,        // totalTransactionAmount
                        customerCompany.etc                    // note
                ))
                .from(customerCompany)
                .leftJoin(customerUser).on(customerUser.customerCompany.id.eq(customerCompany.id))
                .where(customerCompany.id.eq(customerId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<CustomerListItemDto> findCustomerList(CustomerSearchConditionVo condition, Pageable pageable) {
        // 1. 동적 쿼리 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // 상태 필터 (ALL, ACTIVE, DEACTIVE) - isActive 필드 사용
        if (condition.getStatus() != null && !condition.getStatus().equalsIgnoreCase("ALL")) {
            if (condition.getStatus().equalsIgnoreCase("ACTIVE")) {
                builder.and(customerCompany.isActive.isTrue());
            } else if (condition.getStatus().equalsIgnoreCase("DEACTIVE")) {
                builder.and(customerCompany.isActive.isFalse());
            }
        }

        // 검색 조건 (type과 search 모두 있을 때만 검색)
        if (condition.getType() != null && !condition.getType().isEmpty() &&
            condition.getSearch() != null && !condition.getSearch().isEmpty()) {

            String search = "%" + condition.getSearch().trim() + "%";

            switch (condition.getType().toLowerCase()) {
                case "customernumber" -> builder.and(customerCompany.companyCode.like(search));
                case "customername" -> builder.and(customerCompany.companyName.like(search));
                case "managername" -> builder.and(customerUser.customerName.like(search));
            }
        }

        // 2. 거래 금액 서브쿼리 (고객사별 총 매출액)
        var transactionAmountSubQuery = JPAExpressions
                .select(salesVoucher.totalAmount.sum().coalesce(BigDecimal.ZERO))
                .from(salesVoucher)
                .where(salesVoucher.customerCompany.id.eq(customerCompany.id));

        // 3. 주문 수 서브쿼리
        var orderCountSubQuery = JPAExpressions
                .select(order.count())
                .from(order)
                .where(order.customerUserId.eq(customerUser.id));

        // 4. 마지막 주문일 서브쿼리
        var lastOrderDateSubQuery = JPAExpressions
                .select(order.orderDate.max())
                .from(order)
                .where(order.customerUserId.eq(customerUser.id));

        // 5. 데이터 조회
        JPAQuery<CustomerListItemDto> query = queryFactory
                .select(Projections.constructor(
                        CustomerListItemDto.class,
                        customerCompany.id,                    // customerId
                        customerCompany.companyCode,           // customerNumber
                        customerCompany.companyName,           // customerName
                        Projections.constructor(
                                CustomerManagerDto.class,
                                customerUser.customerName,     // managerName
                                customerUser.phoneNumber,      // managerPhone
                                customerUser.email             // managerEmail
                        ),
                        Expressions.stringTemplate(
                                "CONCAT({0}, ' ', COALESCE({1}, ''))",
                                customerCompany.baseAddress,
                                customerCompany.detailAddress
                        ),                                     // address
                        transactionAmountSubQuery,             // totalTransactionAmount
                        orderCountSubQuery,                    // orderCount
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                lastOrderDateSubQuery
                        ),                                     // lastOrderDate
                        Expressions.stringTemplate(
                                "CASE WHEN {0} = true THEN 'ACTIVE' ELSE 'INACTIVE' END",
                                customerCompany.isActive
                        )                                      // statusCode
                ))
                .from(customerCompany)
                .leftJoin(customerUser).on(customerUser.customerCompany.id.eq(customerCompany.id))
                .where(builder)
                .orderBy(customerCompany.id.desc());

        // 6. 페이징 적용
        List<CustomerListItemDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 7. 전체 개수 조회
        Long total = queryFactory
                .select(customerCompany.count())
                .from(customerCompany)
                .leftJoin(customerUser).on(customerUser.customerCompany.id.eq(customerCompany.id))
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }
}
