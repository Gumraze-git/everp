package org.ever._4ever_be_business.fcm.dao.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dao.SalesStatementDAO;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementConnectionDto;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementItemDto;
import org.ever._4ever_be_business.fcm.dto.response.SalesStatementListItemDto;
import org.ever._4ever_be_business.order.enums.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.voucher.entity.QSalesVoucher.salesVoucher;
import static org.ever._4ever_be_business.order.entity.QOrder.order;
import static org.ever._4ever_be_business.order.entity.QQuotation.quotation;
import static org.ever._4ever_be_business.order.entity.QQuotationItem.quotationItem;
import static org.ever._4ever_be_business.company.entity.QCustomerCompany.customerCompany;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SalesStatementDAOImpl implements SalesStatementDAO {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<SalesStatementDetailDto> findSalesStatementDetailById(String statementId) {
        log.debug("매출전표 상세 정보 조회 시작 - statementId: {}", statementId);

        // 1. SalesVoucher → Order → Quotation → CustomerCompany 조인하여 기본 정보 조회
        var statementInfo = queryFactory
                .select(Projections.constructor(
                        SalesStatementInfoProjection.class,
                        salesVoucher.id,
                        salesVoucher.voucherCode,
                        salesVoucher.status,
                        salesVoucher.issueDate,
                        salesVoucher.dueDate,
                        customerCompany.companyName,
                        order.orderCode,
                        salesVoucher.totalAmount,
                        salesVoucher.memo,
                        quotation.id
                ))
                .from(salesVoucher)
                .innerJoin(salesVoucher.order, order)
                .innerJoin(order.quotation, quotation)
                .innerJoin(salesVoucher.customerCompany, customerCompany)
                .where(salesVoucher.id.eq(statementId))
                .fetchOne();

        if (statementInfo == null) {
            log.debug("매출전표를 찾을 수 없음 - statementId: {}", statementId);
            return Optional.empty();
        }

        // 2. QuotationItem 조회 (productId, quantity, unit, price)
        List<QuotationItemProjection> quotationItems = queryFactory
                .select(Projections.constructor(
                        QuotationItemProjection.class,
                        quotationItem.productId,
                        quotationItem.count,
                        quotationItem.unit,
                        quotationItem.price
                ))
                .from(quotationItem)
                .where(quotationItem.quotation.id.eq(statementInfo.getQuotationId()))
                .fetch();

        // 3. Items DTO 생성 (productName은 Service에서 SCM 호출하여 채움)
        List<SalesStatementItemDto> items = quotationItems.stream()
                .map(item -> new SalesStatementItemDto(
                        item.getProductId(),
                        null, // itemName - Service에서 SCM 호출하여 채울 예정
                        item.getQuantity().intValue(),
                        item.getUnit().name(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                ))
                .toList();

        // 4. 날짜 포맷팅
        String issueDate = statementInfo.getIssueDate() != null
                ? statementInfo.getIssueDate().toString().substring(0, 10)
                : null;
        String dueDate = statementInfo.getDueDate() != null
                ? statementInfo.getDueDate().toString().substring(0, 10)
                : null;

        // 5. DTO 조립
        SalesStatementDetailDto result = new SalesStatementDetailDto(
                statementInfo.getInvoiceId(),
                statementInfo.getInvoiceCode(),
                "AR", // invoiceType - 매출전표는 AR (Accounts Receivable)
                statementInfo.getStatusCode().name(),
                issueDate,
                dueDate,
                statementInfo.getCustomerName(),
                statementInfo.getReferenceCode(),
                items,
                statementInfo.getTotalAmount(),
                statementInfo.getNote()
        );

        log.debug("매출전표 상세 정보 조회 완료 - statementId: {}, invoiceCode: {}",
                statementId, statementInfo.getInvoiceCode());

        return Optional.of(result);
    }

    @Override
    public Page<SalesStatementListItemDto> findSalesStatementList(String company, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        log.debug("매출전표 목록 조회 시작 - company: {}, startDate: {}, endDate: {}", company, startDate, endDate);

        // 동적 쿼리 조건 생성
        var query = queryFactory
                .select(Projections.constructor(
                        SalesStatementListProjection.class,
                        salesVoucher.id,
                        salesVoucher.voucherCode,
                        customerCompany.id,
                        customerCompany.companyCode,
                        customerCompany.companyName,
                        salesVoucher.totalAmount,
                        salesVoucher.issueDate,
                        salesVoucher.dueDate,
                        salesVoucher.status,
                        order.orderCode
                ))
                .from(salesVoucher)
                .innerJoin(salesVoucher.customerCompany, customerCompany)
                .innerJoin(salesVoucher.order, order)
                .where(
                        companyNameContains(company),
                        issueDateBetween(startDate, endDate)
                );

        // Total count
        long total = query.fetchCount();

        // 페이징된 데이터 조회
        List<SalesStatementListProjection> statements = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(salesVoucher.issueDate.desc(), salesVoucher.id.desc())
                .fetch();

        // DTO 변환
        List<SalesStatementListItemDto> content = statements.stream()
                .map(stmt -> {
                    // 날짜 포맷팅
                    String issueDate = stmt.getIssueDate() != null
                            ? stmt.getIssueDate().toString().substring(0, 10)
                            : null;
                    String dueDate = stmt.getDueDate() != null
                            ? stmt.getDueDate().toString().substring(0, 10)
                            : null;

                    // Connection DTO 생성
                    SalesStatementConnectionDto connection = new SalesStatementConnectionDto(
                            stmt.getConnectionId(),
                            stmt.getConnectionCode(),
                            stmt.getConnectionName()
                    );

                    return new SalesStatementListItemDto(
                            stmt.getInvoiceId(),
                            stmt.getInvoiceCode(),
                            connection,
                            stmt.getTotalAmount(),
                            issueDate,
                            dueDate,
                            stmt.getStatus().name(),
                            stmt.getReferenceCode()
                    );
                })
                .toList();

        log.debug("매출전표 목록 조회 완료 - total: {}, size: {}", total, content.size());

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 거래처명 필터 (LIKE 검색)
     */
    private BooleanExpression companyNameContains(String company) {
        if (company == null || company.isBlank()) {
            return null;
        }
        return customerCompany.companyName.containsIgnoreCase(company);
    }

    /**
     * 발행일 기간 필터
     */
    private BooleanExpression issueDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        if (startDateTime != null && endDateTime != null) {
            return salesVoucher.issueDate.between(startDateTime, endDateTime);
        } else if (startDateTime != null) {
            return salesVoucher.issueDate.goe(startDateTime);
        } else {
            return salesVoucher.issueDate.loe(endDateTime);
        }
    }

    /**
     * QueryDSL Projection용 내부 클래스
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class SalesStatementInfoProjection {
        private String invoiceId;
        private String invoiceCode;
        private org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus statusCode;
        private LocalDateTime issueDate;
        private LocalDateTime dueDate;
        private String customerName;
        private String referenceCode;
        private BigDecimal totalAmount;
        private String note;
        private String quotationId;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class QuotationItemProjection {
        private String productId;
        private Long quantity;
        private Unit unit;
        private BigDecimal unitPrice;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class SalesStatementListProjection {
        private String invoiceId;
        private String invoiceCode;
        private String connectionId;
        private String connectionCode;
        private String connectionName;
        private BigDecimal totalAmount;
        private LocalDateTime issueDate;
        private LocalDateTime dueDate;
        private org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus status;
        private String referenceCode;
    }
}
