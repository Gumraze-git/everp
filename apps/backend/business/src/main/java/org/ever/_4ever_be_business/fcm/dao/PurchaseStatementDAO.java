package org.ever._4ever_be_business.fcm.dao;

import org.ever._4ever_be_business.fcm.dto.internal.PurchaseStatementInfoDto;
import org.ever._4ever_be_business.fcm.dto.internal.PurchaseStatementListItemInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface PurchaseStatementDAO {
    /**
     * 매입전표 기본 정보 조회
     *
     * @param statementId 전표 ID (PurchaseVoucher ID)
     * @return 매입전표 기본 정보 (supplierCompanyId, productOrderId 포함)
     */
    Optional<PurchaseStatementInfoDto> findPurchaseStatementInfoById(String statementId);

    /**
     * 매입전표 목록 조회 (페이징, 필터링)
     *
     * @param company 공급업체명 (optional)
     * @param status 전표 상태 (optional)
     * @param startDate 시작일 (optional)
     * @param endDate 종료일 (optional)
     * @param pageable 페이징 정보
     * @return 매입전표 목록
     */
    Page<PurchaseStatementListItemInfoDto> findPurchaseStatementList(
            String company,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    /**
     * 특정 Supplier Company ID로 매입전표 목록 조회 (페이징, 필터링)
     *
     * @param supplierCompanyId 공급업체 ID
     * @param startDate 시작일 (optional)
     * @param endDate 종료일 (optional)
     * @param pageable 페이징 정보
     * @return 매입전표 목록
     */
    Page<PurchaseStatementListItemInfoDto> findPurchaseStatementListBySupplierCompanyId(
            String supplierCompanyId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
