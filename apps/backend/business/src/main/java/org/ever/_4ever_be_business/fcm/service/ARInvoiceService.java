package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceDetailDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceListItemDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface ARInvoiceService {

    /**
     * AR 전표 목록 조회
     *
     * @param company 고객사명 (검색 필터, optional)
     * @param status 전표 상태 (검색 필터, optional)
     * @param startDate 시작일 (검색 필터, optional)
     * @param endDate 종료일 (검색 필터, optional)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return Page<ARInvoiceListItemDto>
     */
    Page<ARInvoiceListItemDto> getARInvoiceList(String company, String status, LocalDate startDate, LocalDate endDate, int page, int size);

    /**
     * CustomerUserId 기반 AR 전표 목록 조회
     *
     * @param customerUserId Customer의 userId
     * @param status 전표 상태 (검색 필터, optional)
     * @param startDate 시작일 (검색 필터, optional)
     * @param endDate 종료일 (검색 필터, optional)
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return Page<ARInvoiceListItemDto>
     */
    Page<ARInvoiceListItemDto> getARInvoiceListByCustomerUserId(String customerUserId, String status, LocalDate startDate, LocalDate endDate, int page, int size);

    /**
     * AR 전표 상세 정보 조회
     *
     * @param invoiceId 전표 ID
     * @return ARInvoiceDetailDto
     */
    ARInvoiceDetailDto getARInvoiceDetail(String invoiceId);

    /**
     * AR 전표 정보 업데이트
     *
     * @param invoiceId 전표 ID
     * @param status 새로운 상태
     * @param dueDate 새로운 지급 기한
     * @param memo 새로운 메모
     */
    void updateARInvoice(String invoiceId, String status, String dueDate, String memo);

    /**
     * AR 전표 미수 처리 완료 (상태를 PAID로 변경)
     *
     * @param invoiceId 전표 ID
     */
    void completeReceivable(String invoiceId);
}
