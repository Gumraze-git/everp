package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.request.SupplierPurchaseInvoiceRequestDto;
import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 대시보드용(공급사) 매입 전표 조회 서비스
 */
public interface SupplierPurchaseInvoiceService {

    /**
     * 공급사 사용자 ID 기준 매입 전표 목록 조회 (대시보드)
     * @param request  userId, size 등 요청 파라미터
     * @param pageable 페이지 정보 (page, size)
     * @return GW 대시보드 스키마와 동일한 아이템 DTO 페이지
     */
    Page<SupplierPurchaseInvoiceListItemDto> getSupplierPurchaseList(
            SupplierPurchaseInvoiceRequestDto request,
            Pageable pageable
    );
}

