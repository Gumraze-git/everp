package org.ever._4ever_be_business.voucher.repository;

import org.ever._4ever_be_business.fcm.dto.request.ARInvoiceSearchConditionDto;
import org.ever._4ever_be_business.fcm.dto.response.ARInvoiceListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalesVoucherRepositoryCustom {

    /**
     * AR 전표 목록 조회
     *
     * @param condition 검색 조건 (company, startDate, endDate)
     * @param pageable 페이징 정보
     * @return Page<ARInvoiceListItemDto>
     */
    Page<ARInvoiceListItemDto> findARInvoiceList(ARInvoiceSearchConditionDto condition, Pageable pageable);
}
