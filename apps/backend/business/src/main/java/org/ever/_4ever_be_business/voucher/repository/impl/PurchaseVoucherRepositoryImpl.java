package org.ever._4ever_be_business.voucher.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dto.request.APInvoiceSearchConditionDto;
import org.ever._4ever_be_business.fcm.dto.response.APInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.dto.response.ReferenceDto;
import org.ever._4ever_be_business.fcm.dto.response.SupplierDto;
import org.ever._4ever_be_business.voucher.repository.PurchaseVoucherRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.ever._4ever_be_business.voucher.entity.QPurchaseVoucher.purchaseVoucher;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PurchaseVoucherRepositoryImpl implements PurchaseVoucherRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Page<APInvoiceListItemDto> findAPInvoiceList(APInvoiceSearchConditionDto condition, Pageable pageable) {
        log.info("AP 전표 목록 조회 - condition: {}", condition);

        // 전체 개수 조회
        Long total = queryFactory
                .select(purchaseVoucher.count())
                .from(purchaseVoucher)
                .where(
                        statusEquals(condition.getStatus()),
                        supplierCompanyIdEquals(condition.getSupplierCompanyId()),
                        issueDateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .fetchOne();

        // 데이터 조회
        List<APInvoiceListItemDto> content = queryFactory
                .select(purchaseVoucher)
                .from(purchaseVoucher)
                .where(
                        statusEquals(condition.getStatus()),
                        supplierCompanyIdEquals(condition.getSupplierCompanyId()),
                        issueDateBetween(condition.getStartDate(), condition.getEndDate())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(purchaseVoucher.issueDate.desc())
                .fetch()
                .stream()
                .map(voucher -> {
                    // SupplierDto 생성 (supplierCompanyId만 설정, 회사명은 서비스 레이어에서 조회)
                    SupplierDto supplierDto = new SupplierDto(
                            voucher.getSupplierCompanyId(),
                            null, // companyCode는 SCM에서 조회 필요
                            null  // companyName은 SCM에서 조회 필요
                    );

                    // ReferenceDto 생성 (productOrder 정보)
                    ReferenceDto referenceDto = new ReferenceDto(
                            voucher.getProductOrderId(),
                            null // orderCode는 SCM에서 조회 필요
                    );

                    // APInvoiceListItemDto 생성
                    return new APInvoiceListItemDto(
                            voucher.getId(),
                            voucher.getVoucherCode(),
                            supplierDto,
                            voucher.getTotalAmount(),
                            voucher.getIssueDate().format(DATE_FORMATTER),
                            voucher.getDueDate().format(DATE_FORMATTER),
                            voucher.getStatus().name(),
                            null, // referenceNumber는 SCM에서 조회 필요
                            referenceDto
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
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
     * supplierCompanyId로 필터링
     */
    private BooleanExpression supplierCompanyIdEquals(String supplierCompanyId) {
        return supplierCompanyId != null && !supplierCompanyId.isBlank()
                ? purchaseVoucher.supplierCompanyId.eq(supplierCompanyId)
                : null;
    }

    /**
     * 발행일 기간으로 필터링
     */
    private BooleanExpression issueDateBetween(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            return purchaseVoucher.issueDate.between(startDateTime, endDateTime);
        } else if (startDate != null) {
            return purchaseVoucher.issueDate.goe(startDate.atStartOfDay());
        } else if (endDate != null) {
            return purchaseVoucher.issueDate.loe(endDate.atTime(23, 59, 59));
        }
        return null;
    }
}
