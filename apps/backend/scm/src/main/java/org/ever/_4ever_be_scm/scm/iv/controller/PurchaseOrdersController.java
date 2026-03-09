package org.ever._4ever_be_scm.scm.iv.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.common.exception.BusinessException;
import org.ever._4ever_be_scm.common.exception.ErrorCode;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.PurchaseOrderDto;
import org.ever._4ever_be_scm.scm.iv.service.PurchaseOrdersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * 구매 발주 관리 컨트롤러
 */
@Tag(name = "재고관리", description = "재고 관리 API")
@RestController
@RequestMapping("/scm-pp")
@RequiredArgsConstructor
public class PurchaseOrdersController {
    
    private final PurchaseOrdersService purchaseOrderService;
    
    /**
     * 입고 준비 목록 조회 API
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 입고 준비 목록
     */
    @GetMapping("/purchase-orders")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "구매 발주 목록 조회"
    )
    public ResponseEntity<PagedResponseDto<PurchaseOrderDto>> getPurchaseOrders(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Page<PurchaseOrderDto> purchaseOrdersPage = switch (status) {
            case "DELIVERING" -> purchaseOrderService.getReceivingPurchaseOrders(PageRequest.of(page, size));
            case "DELIVERED" -> purchaseOrderService.getReceivedPurchaseOrders(
                    PageRequest.of(page, size), startDate, endDate);
            default -> throw new BusinessException(
                    ErrorCode.INVALID_STATUS,
                    "지원하지 않는 purchase-order status 입니다: " + status
            );
        };
        PagedResponseDto<PurchaseOrderDto> response = PagedResponseDto.from(purchaseOrdersPage);

        return ResponseEntity.ok(response);
    }
}
