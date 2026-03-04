package org.ever._4ever_be_business.voucher.repository;

import org.ever._4ever_be_business.fcm.dto.request.APInvoiceSearchConditionDto;
import org.ever._4ever_be_business.fcm.dto.response.APInvoiceListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseVoucherRepositoryCustom {

    /**
     * AP 전표 목록 조회
     *
     * @param condition 검색 조건 (company, supplierCompanyId, startDate, endDate)
     * @param pageable 페이징 정보
     * @return Page<APInvoiceListItemDto>
     */
    Page<APInvoiceListItemDto> findAPInvoiceList(APInvoiceSearchConditionDto condition, Pageable pageable);
}
