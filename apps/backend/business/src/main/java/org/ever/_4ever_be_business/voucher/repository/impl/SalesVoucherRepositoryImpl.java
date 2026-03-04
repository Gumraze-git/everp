package org.ever._4ever_be_business.voucher.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dto.request.ARInvoiceSearchConditionDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.dto.response.ReferenceDto;
import org.ever._4ever_be_business.fcm.dto.response.SupplyDto;
import org.ever._4ever_be_business.voucher.repository.SalesVoucherRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.ever._4ever_be_business.voucher.entity.QSalesVoucher.salesVoucher;
import static org.ever._4ever_be_business.company.entity.QCustomerCompany.customerCompany;
import static org.ever._4ever_be_business.order.entity.QOrder.order;
import static org.ever._4ever_be_business.hr.entity.QCustomerUser.customerUser;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SalesVoucherRepositoryImpl implements SalesVoucherRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Page<ARInvoiceListItemDto> findARInvoiceList(ARInvoiceSearchConditionDto condition, Pageable pageable) {
        log.info("AR 전표 목록 조회 - condition: {}", condition);

        // 전체 개수 조회
        Long total = queryFactory
                .select(salesVoucher.count())
                .from(salesVoucher)
                .join(salesVoucher.customerCompany, customerCompany)
                .leftJoin(customerUser).on(customerUser.customerCompany.id.eq(customerCompany.id))
                .join(salesVoucher.order, order)
                .where(
                        companyNameContains(condition.getCompany()),
                        statusEquals(condition.getStatus()),
                        customerUserIdEquals(condition.getCustomerUserId()),
                        issueDateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .fetchOne();

        // 데이터 조회
        List<ARInvoiceListItemDto> content = queryFactory
                .select(salesVoucher)
                .from(salesVoucher)
                .join(salesVoucher.customerCompany, customerCompany).fetchJoin()
                .leftJoin(customerUser).on(customerUser.customerCompany.id.eq(customerCompany.id))
                .join(salesVoucher.order, order).fetchJoin()
                .where(
                        companyNameContains(condition.getCompany()),
                        statusEquals(condition.getStatus()),
                        customerUserIdEquals(condition.getCustomerUserId()),
                        issueDateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(salesVoucher.issueDate.desc())
                .fetch()
                .stream()
                .map(voucher -> {
                    // SupplyDto 생성 (customerCompany 정보를 supply 필드에 매핑)
                    SupplyDto supplyDto = new SupplyDto(
                            voucher.getCustomerCompany().getId(),
                            voucher.getCustomerCompany().getCompanyCode(),
                            voucher.getCustomerCompany().getCompanyName()
                    );

                    // ReferenceDto 생성 (order 정보를 reference 필드에 매핑)
                    ReferenceDto referenceDto = new ReferenceDto(
                            voucher.getOrder().getId(),
                            voucher.getOrder().getOrderCode()
                    );

                    // ARInvoiceListItemDto 생성
                    return new ARInvoiceListItemDto(
                            voucher.getId(),
                            voucher.getVoucherCode(),
                            supplyDto,
                            voucher.getTotalAmount(),
                            voucher.getIssueDate().format(DATE_FORMATTER),
                            voucher.getDueDate().format(DATE_FORMATTER),
                            voucher.getStatus().name(),
                            voucher.getOrder().getOrderCode(),
                            referenceDto
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    /**
     * 고객사명으로 필터링 (LIKE 검색)
     */
    private BooleanExpression companyNameContains(String companyName) {
        return companyName != null && !companyName.isBlank()
                ? customerCompany.companyName.containsIgnoreCase(companyName)
                : null;
    }

    /**
     * customerUserId로 필터링
     */
    private BooleanExpression customerUserIdEquals(String customerUserId) {
        return customerUserId != null && !customerUserId.isBlank()
                ? customerUser.userId.eq(customerUserId)
                : null;
    }

    /**
     * 전표 상태 필터
     */
    private BooleanExpression statusEquals(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus statusEnum =
                    org.ever._4ever_be_business.voucher.enums.SalesVoucherStatus.valueOf(status);
            return salesVoucher.status.eq(statusEnum);
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 상태값 - status: {}", status);
            return null;
        }
    }

    /**
     * 발행일 기간으로 필터링
     */
    private BooleanExpression issueDateBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            return salesVoucher.issueDate.between(startDateTime, endDateTime);
        } else if (startDate != null) {
            return salesVoucher.issueDate.goe(startDate.atStartOfDay());
        } else if (endDate != null) {
            return salesVoucher.issueDate.loe(endDate.atTime(23, 59, 59));
        }
        return null;
    }
}
