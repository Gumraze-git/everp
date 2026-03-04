package org.ever._4ever_be_business.fcm.dao.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dao.PurchaseStatementDAO;
import org.ever._4ever_be_business.fcm.dto.internal.PurchaseStatementInfoDto;
import org.ever._4ever_be_business.fcm.dto.internal.PurchaseStatementListItemInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.voucher.entity.QPurchaseVoucher.purchaseVoucher;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PurchaseStatementDAOImpl implements PurchaseStatementDAO {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<PurchaseStatementInfoDto> findPurchaseStatementInfoById(String statementId) {
        log.debug("매입전표 기본 정보 조회 시작 - statementId: {}", statementId);

        // PurchaseVoucher 기본 정보 조회
        PurchaseStatementInfoProjection statementInfo = queryFactory
                .select(Projections.constructor(
                        PurchaseStatementInfoProjection.class,
                        purchaseVoucher.id.stringValue(),
                        purchaseVoucher.voucherCode,
                        purchaseVoucher.status,
                        purchaseVoucher.issueDate,
                        purchaseVoucher.dueDate,
                        purchaseVoucher.supplierCompanyId,
                        purchaseVoucher.productOrderId,
                        purchaseVoucher.totalAmount,
                        purchaseVoucher.memo
                ))
                .from(purchaseVoucher)
                .where(purchaseVoucher.id.eq(statementId))
                .fetchOne();

        if (statementInfo == null) {
            log.debug("매입전표를 찾을 수 없음 - statementId: {}", statementId);
            return Optional.empty();
        }

        // 날짜 포맷팅
        String issueDate = statementInfo.getIssueDate() != null
                ? statementInfo.getIssueDate().toString().substring(0, 10)
                : null;
        String dueDate = statementInfo.getDueDate() != null
                ? statementInfo.getDueDate().toString().substring(0, 10)
                : null;

        // DTO 조립
        PurchaseStatementInfoDto result = new PurchaseStatementInfoDto(
                statementInfo.getInvoiceId(),
                statementInfo.getInvoiceCode(),
                statementInfo.getStatusCode().name(),
                issueDate,
                dueDate,
                statementInfo.getSupplierCompanyId(),
                statementInfo.getProductOrderId(),
                statementInfo.getTotalAmount(),
                statementInfo.getNote()
        );

        log.debug("매입전표 기본 정보 조회 완료 - statementId: {}, invoiceCode: {}",
                statementId, statementInfo.getInvoiceCode());

        return Optional.of(result);
    }

    @Override
    public Page<PurchaseStatementListItemInfoDto> findPurchaseStatementList(
            String company,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        log.debug("매입전표 목록 조회 시작 - company: {}, status: {}, startDate: {}, endDate: {}",
                company, status, startDate, endDate);

        // 동적 쿼리 생성 (company 필터는 Service에서 SCM 호출 후 적용)
        // 여기서는 status와 날짜 필터만 적용
        JPAQuery<PurchaseStatementListProjection> query = queryFactory
                .select(Projections.constructor(
                        PurchaseStatementListProjection.class,
                        purchaseVoucher.id.stringValue(),
                        purchaseVoucher.voucherCode,
                        purchaseVoucher.supplierCompanyId,
                        purchaseVoucher.issueDate,
                        purchaseVoucher.dueDate,
                        purchaseVoucher.status,
                        purchaseVoucher.productOrderId
                ))
                .from(purchaseVoucher)
                .where(
                        statusEquals(status),
                        issueDateBetween(startDate, endDate)
                );

        // Total count
        long total = query.fetchCount();

        // 페이징된 데이터 조회
        List<PurchaseStatementListProjection> statements = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(purchaseVoucher.issueDate.desc(), purchaseVoucher.id.desc())
                .fetch();

        // DTO 변환
        List<PurchaseStatementListItemInfoDto> content = statements.stream()
                .map(stmt -> {
                    // 날짜 포맷팅
                    String issueDate = stmt.getIssueDate() != null
                            ? stmt.getIssueDate().toString().substring(0, 10)
                            : null;
                    String dueDate = stmt.getDueDate() != null
                            ? stmt.getDueDate().toString().substring(0, 10)
                            : null;

                    return new PurchaseStatementListItemInfoDto(
                            stmt.getInvoiceId(),
                            stmt.getInvoiceCode(),
                            stmt.getSupplierCompanyId(),
                            issueDate,
                            dueDate,
                            stmt.getStatus().name(),
                            stmt.getProductOrderId()
                    );
                })
                .toList();

        log.debug("매입전표 목록 조회 완료 - total: {}, size: {}", total, content.size());

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 전표 상태 필터
     */
    private BooleanExpression statusEquals(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus statusEnum =
                    org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus.valueOf(status);
            return purchaseVoucher.status.eq(statusEnum);
        } catch (IllegalArgumentException e) {
            log.warn("유효하지 않은 상태값 - status: {}", status);
            return null;
        }
    }

    /**
     * 발행일자 범위 필터
     */
    private BooleanExpression issueDateBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        if (startDateTime != null && endDateTime != null) {
            return purchaseVoucher.issueDate.between(startDateTime, endDateTime);
        } else if (startDateTime != null) {
            return purchaseVoucher.issueDate.goe(startDateTime);
        } else {
            return purchaseVoucher.issueDate.loe(endDateTime);
        }
    }

    /**
     * QueryDSL Projection용 내부 클래스
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class PurchaseStatementInfoProjection {
        private String invoiceId;
        private String invoiceCode;
        private org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus statusCode;
        private LocalDateTime issueDate;
        private LocalDateTime dueDate;
        private String supplierCompanyId;
        private String productOrderId;
        private BigDecimal totalAmount;
        private String note;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class PurchaseStatementListProjection {
        private String invoiceId;
        private String invoiceCode;
        private String supplierCompanyId;
        private LocalDateTime issueDate;
        private LocalDateTime dueDate;
        private org.ever._4ever_be_business.voucher.enums.PurchaseVoucherStatus status;
        private String productOrderId;
    }

    @Override
    public Page<PurchaseStatementListItemInfoDto> findPurchaseStatementListBySupplierCompanyId(
            String supplierCompanyId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {
        log.debug("Supplier Company ID로 매입전표 목록 조회 시작 - supplierCompanyId: {}, startDate: {}, endDate: {}",
                supplierCompanyId, startDate, endDate);

        // 동적 쿼리 생성 - supplierCompanyId와 날짜 필터 적용
        JPAQuery<PurchaseStatementListProjection> query = queryFactory
                .select(Projections.constructor(
                        PurchaseStatementListProjection.class,
                        purchaseVoucher.id.stringValue(),
                        purchaseVoucher.voucherCode,
                        purchaseVoucher.supplierCompanyId,
                        purchaseVoucher.issueDate,
                        purchaseVoucher.dueDate,
                        purchaseVoucher.status,
                        purchaseVoucher.productOrderId
                ))
                .from(purchaseVoucher)
                .where(
                        purchaseVoucher.supplierCompanyId.eq(supplierCompanyId),
                        issueDateBetween(startDate, endDate)
                );

        // Total count
        long total = query.fetchCount();

        // 페이징된 데이터 조회
        List<PurchaseStatementListProjection> statements = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(purchaseVoucher.issueDate.desc(), purchaseVoucher.id.desc())
                .fetch();

        // DTO 변환
        List<PurchaseStatementListItemInfoDto> content = statements.stream()
                .map(stmt -> {
                    // 날짜 포맷팅
                    String issueDate = stmt.getIssueDate() != null
                            ? stmt.getIssueDate().toString().substring(0, 10)
                            : null;
                    String dueDate = stmt.getDueDate() != null
                            ? stmt.getDueDate().toString().substring(0, 10)
                            : null;

                    return new PurchaseStatementListItemInfoDto(
                            stmt.getInvoiceId(),
                            stmt.getInvoiceCode(),
                            stmt.getSupplierCompanyId(),
                            issueDate,
                            dueDate,
                            stmt.getStatus().name(),
                            stmt.getProductOrderId()
                    );
                })
                .toList();

        log.debug("Supplier Company ID로 매입전표 목록 조회 완료 - supplierCompanyId: {}, total: {}, size: {}",
                supplierCompanyId, total, content.size());

        return new PageImpl<>(content, pageable, total);
    }
}
