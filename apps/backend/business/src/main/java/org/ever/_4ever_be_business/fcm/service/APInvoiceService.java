package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.response.APInvoiceDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.APInvoiceListItemDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface APInvoiceService {

    /**
     * AP 전표 목록 조회
     *
     * @param company 공급사명 (검색 필터, optional)
     * @param startDate 시작일 (검색 필터, optional)
     * @param endDate 종료일 (검색 필터, optional)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return Page<APInvoiceListItemDto>
     */
    Page<APInvoiceListItemDto> getAPInvoiceList(String company, LocalDate startDate, LocalDate endDate, int page, int size);

    /**
     * SupplierCompanyId 기반 AP 전표 목록 조회
     *
     * @param supplierCompanyId Supplier의 companyId (from SCM)
     * @param status 전표 상태 (검색 필터, optional)
     * @param startDate 시작일 (검색 필터, optional)
     * @param endDate 종료일 (검색 필터, optional)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return Page<APInvoiceListItemDto>
     */
    Page<APInvoiceListItemDto> getAPInvoiceListBySupplierCompanyId(String supplierCompanyId, String status, LocalDate startDate, LocalDate endDate, int page, int size);

    /**
     * AP 전표 상세 정보 조회
     *
     * @param invoiceId 전표 ID
     * @return APInvoiceDetailDto
     */
    APInvoiceDetailDto getAPInvoiceDetail(String invoiceId);

    /**
     * AP 전표 미지급 처리 완료 (PAID 상태로 변경)
     *
     * @param invoiceId 전표 ID
     */
    void completePayable(String invoiceId);
}
