package org.ever._4ever_be_business.order.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.repository.OrderItemRepository;
import org.ever._4ever_be_business.order.repository.OrderRepositoryCustom;
import org.ever._4ever_be_business.sd.dto.response.*;
import org.ever._4ever_be_business.sd.vo.OrderSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.ever._4ever_be_business.company.entity.QCustomerCompany.customerCompany;
import static org.ever._4ever_be_business.hr.entity.QCustomerUser.customerUser;
import static org.ever._4ever_be_business.order.entity.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final OrderItemRepository orderItemRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Page<SalesOrderListItemDto> findOrderList(OrderSearchConditionVo condition, Pageable pageable) {
        // 1. 동적 쿼리 조건 생성
        BooleanBuilder builder = new BooleanBuilder();

        // 날짜 범위 필터 (startDate ~ endDate)
        if (condition.getStartDate() != null && !condition.getStartDate().isEmpty()) {
            LocalDate startDate = LocalDate.parse(condition.getStartDate(), DATE_FORMATTER);
            builder.and(order.orderDate.goe(startDate.atStartOfDay()));
        }
        if (condition.getEndDate() != null && !condition.getEndDate().isEmpty()) {
            LocalDate endDate = LocalDate.parse(condition.getEndDate(), DATE_FORMATTER);
            builder.and(order.orderDate.loe(endDate.atTime(23, 59, 59)));
        }

        // 상태 필터
        if (condition.getStatus() != null && !condition.getStatus().equalsIgnoreCase("ALL")) {
            try {
                org.ever._4ever_be_business.order.entity.OrderStatus status =
                    org.ever._4ever_be_business.order.entity.OrderStatus.valueOf(condition.getStatus());
                builder.and(order.status.eq(status));
            } catch (IllegalArgumentException e) {
                // 잘못된 상태값은 무시
            }
        }

        // 고객사 사용자 ID 필터 (CUSTOMER 유저용)
        if (condition.getCustomerId() != null && !condition.getCustomerId().isEmpty()) {
            builder.and(order.customerUserId.eq(condition.getCustomerId()));
        }

        // 내부 직원 ID 필터 (EMPLOYEE 유저용)
        if (condition.getEmployeeId() != null && !condition.getEmployeeId().isEmpty()) {
            builder.and(order.quotation.quotationApproval.approvedBy.eq(condition.getEmployeeId()));
        }

        // 검색 조건 (type과 search 모두 있을 때만 검색)
        if (condition.getType() != null && !condition.getType().isEmpty() &&
                condition.getSearch() != null && !condition.getSearch().isEmpty()) {

            String search = "%" + condition.getSearch().trim() + "%";

            switch (condition.getType().toLowerCase()) {
                case "salesordernumber" -> builder.and(order.orderCode.like(search));
                case "customername" -> builder.and(customerCompany.companyName.like(search));
                case "managername" -> builder.and(customerUser.customerName.like(search));
            }
        }

        // 2. 데이터 조회
        JPAQuery<SalesOrderListItemDto> query = queryFactory
                .select(Projections.constructor(
                        SalesOrderListItemDto.class,
                        order.id,                              // salesOrderId
                        order.orderCode,                       // salesOrderNumber
                        customerCompany.companyName,           // customerName
                        Projections.constructor(
                                CustomerManagerDto.class,
                                customerUser.customerName,     // managerName
                                customerUser.phoneNumber,      // managerPhone
                                customerUser.email             // managerEmail
                        ),
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                order.orderDate
                        ),                                     // orderDate
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                order.dueDate
                        ),                                     // dueDate
                        order.totalPrice,                      // totalAmount
                        order.status.stringValue()             // statusCode
                ))
                .from(order)
                .leftJoin(customerUser).on(customerUser.id.eq(order.customerUserId))
                .leftJoin(customerUser.customerCompany, customerCompany)
                .where(builder)
                .orderBy(order.orderDate.desc());

        // 3. 페이징 적용
        List<SalesOrderListItemDto> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 4. 전체 개수 조회
        Long total = queryFactory
                .select(order.count())
                .from(order)
                .leftJoin(customerUser).on(customerUser.id.eq(order.customerUserId))
                .leftJoin(customerUser.customerCompany, customerCompany)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public SalesOrderDetailResponseDto findOrderDetailById(String salesOrderId) {
        // 1. Order 기본 정보 + Customer 정보 조회
        Order orderEntity = queryFactory
                .selectFrom(order)
                .where(order.id.eq(salesOrderId))
                .fetchOne();

        if (orderEntity == null) {
            return null;
        }

        // 2. Customer 및 Manager 정보 조회
        var customerInfo = queryFactory
                .select(Projections.constructor(
                        OrderCustomerDto.class,
                        customerCompany.id,                          // customerId
                        customerCompany.companyName,                 // customerName
                        customerCompany.baseAddress,                 // customerBaseAddress
                        customerCompany.detailAddress,               // customerDetailAddress
                        Projections.constructor(
                                CustomerManagerDto.class,
                                customerUser.customerName,           // managerName
                                customerUser.phoneNumber,            // managerPhone
                                customerUser.email                   // managerEmail
                        )
                ))
                .from(customerUser)
                .leftJoin(customerUser.customerCompany, customerCompany)
                .where(customerUser.id.eq(orderEntity.getCustomerUserId()))
                .fetchOne();

        // 3. Order 정보 DTO 생성
        OrderDetailDto orderDto = new OrderDetailDto(
                orderEntity.getId(),
                orderEntity.getOrderCode(),
                orderEntity.getOrderDate().format(DATE_FORMATTER),
                orderEntity.getDueDate().format(DATE_FORMATTER),
                orderEntity.getStatus() != null ? orderEntity.getStatus().name() : null,
                orderEntity.getTotalPrice()
        );

        // 4. OrderItems 조회 (Product 정보는 Service 계층에서 Adapter로 채움)
        // 여기서는 빈 리스트로 반환하고, Service에서 채울 예정
        List<OrderItemDto> items = new ArrayList<>();

        // 5. 응답 DTO 생성
        return new SalesOrderDetailResponseDto(
                orderDto,
                customerInfo,
                items,
                null  // note: Quotation 엔티티에 etc 필드가 없으므로 null 반환
        );
    }
}
