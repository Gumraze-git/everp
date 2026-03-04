package org.ever._4ever_be_business.sd.service;

import org.ever._4ever_be_business.sd.dto.response.SupplierOrderWorkflowItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 공급사 사용자 기준 주문서 목록 조회 (대시보드용)
 */
public interface SdSupplierOrderService {
    Page<SupplierOrderWorkflowItemDto> getSupplierOrderList(String supplierUserId, Pageable pageable);
}
