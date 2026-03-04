package org.ever._4ever_be_business.sd.service;

import org.ever._4ever_be_business.sd.dto.request.SupplierQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.response.SupplierQuotationWorkflowItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 대시보드용(공급사) 발주서 목록 서비스
 */
public interface DashboardSupplierQuotationService {

    Page<SupplierQuotationWorkflowItemDto> getSupplierQuotationList(
            SupplierQuotationRequestDto request,
            Pageable pageable
    );
}

