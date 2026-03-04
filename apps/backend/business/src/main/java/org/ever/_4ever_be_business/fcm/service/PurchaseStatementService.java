package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.response.PurchaseStatementDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseInvoiceListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PurchaseStatementService {
    /**
     * 매입전표 상세 정보 조회
     *
     * @param statementId 전표 ID
     * @return 매입전표 상세 정보
     */
    PurchaseStatementDetailDto getPurchaseStatementDetail(String statementId);

    /**
     * 매입전표 목록 조회
     *
     * @param company 공급업체명 (optional)
     * @param status 전표 상태 (optional)
     * @param startDate 시작일 (optional)
     * @param endDate 종료일 (optional)
     * @param pageable 페이징 정보
     * @return 매입전표 목록
     */
    Page<PurchaseInvoiceListDto> getPurchaseStatementList(
            String company,
            String status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    /**
     * Supplier User ID로 매입전표 목록 조회
     * SCM 서비스를 통해 supplierCompanyId를 조회한 후 해당 공급업체의 매입전표를 반환
     *
     * @param supplierUserId 공급사 사용자 ID
     * @param startDate 시작일 (optional)
     * @param endDate 종료일 (optional)
     * @param pageable 페이징 정보
     * @return 매입전표 목록
     */
    Page<PurchaseInvoiceListDto> getPurchaseStatementListBySupplierUserId(
            String supplierUserId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}
