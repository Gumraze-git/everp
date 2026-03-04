package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.dto.request.SupplierPurchaseInvoiceRequestDto;
import org.ever._4ever_be_business.fcm.dto.response.PurchaseInvoiceListDto;
import org.ever._4ever_be_business.fcm.dto.response.SupplierPurchaseInvoiceListItemDto;
import org.ever._4ever_be_business.fcm.service.PurchaseStatementService;
import org.ever._4ever_be_business.fcm.service.SupplierPurchaseInvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierPurchaseInvoiceServiceImpl implements SupplierPurchaseInvoiceService {

    private final PurchaseStatementService purchaseStatementService;

    @Override
    public Page<SupplierPurchaseInvoiceListItemDto> getSupplierPurchaseList(
            SupplierPurchaseInvoiceRequestDto request,
            Pageable pageable
    ) {
        String supplierUserId = request.getUserId();

        log.info("[BUSINESS][FCM] 대시보드 공급사 매입 전표 목록 조회 - userId: {}, page: {}, size: {}",
                supplierUserId, pageable.getPageNumber(), pageable.getPageSize());

        // startDate/endDate는 대시보드 1차 버전에서는 사용하지 않음(null)
        Page<PurchaseInvoiceListDto> page = purchaseStatementService.getPurchaseStatementListBySupplierUserId(
                supplierUserId,
                null,
                null,
                pageable
        );

        // GW 대시보드 스키마로 필드 매핑
        return page.map(this::toDashboardItem);
    }

    private SupplierPurchaseInvoiceListItemDto toDashboardItem(PurchaseInvoiceListDto src) {
        String title = src.getConnection() != null ? src.getConnection().getConnectionName() : "";
        return SupplierPurchaseInvoiceListItemDto.builder()
                .itemId(src.getInvoiceId())
                .itemNumber(src.getInvoiceCode())
                .itemTitle(title)
                .name(title) // 필요 시 담당자 등으로 교체 가능
                .statusCode(src.getStatus())
                .date(src.getIssueDate())
                .build();
    }
}

