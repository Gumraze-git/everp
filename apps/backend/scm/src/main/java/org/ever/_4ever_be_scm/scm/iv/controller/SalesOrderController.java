package org.ever._4ever_be_scm.scm.iv.controller;

import org.ever._4ever_be_scm.api.scm.iv.SalesOrderApi;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.common.exception.BusinessException;
import org.ever._4ever_be_scm.common.exception.ErrorCode;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.SalesOrderDetailDto;
import org.ever._4ever_be_scm.scm.iv.dto.SalesOrderDto;
import org.ever._4ever_be_scm.scm.iv.dto.SalesOrderStatusChangeRequestDto;
import org.ever._4ever_be_scm.scm.iv.service.SalesOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * 판매 주문 관리 컨트롤러
 */

@RestController
@RequestMapping("/scm-pp")
@RequiredArgsConstructor
public class SalesOrderController implements SalesOrderApi {

    private final SalesOrderService salesOrderService;

    /**
     * 생산중 목록 조회 API
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 생산중 목록
     */
    @GetMapping("/sales-orders")

    public ResponseEntity<PagedResponseDto<SalesOrderDto>> getSalesOrders(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<SalesOrderDto> salesOrdersPage = switch (status) {
            case "IN_PRODUCTION" -> salesOrderService.getProductionSalesOrders(PageRequest.of(page, size));
            case "READY_FOR_SHIPMENT" -> salesOrderService.getReadyToShipSalesOrders(PageRequest.of(page, size));
            default -> throw new BusinessException(
                    ErrorCode.INVALID_STATUS,
                    "지원하지 않는 sales-order status 입니다: " + status
            );
        };
        PagedResponseDto<SalesOrderDto> response = PagedResponseDto.from(salesOrdersPage);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sales-orders/{salesOrderId}")

    public ResponseEntity<SalesOrderDetailDto> getSalesOrder(@PathVariable String salesOrderId) {
        return ResponseEntity.ok(salesOrderService.getSalesOrderDetail(salesOrderId));
    }

    /**
     * 출고 준비 완료를 배송중 상태로 변경
     */
    @PostMapping("/sales-orders/{salesOrderId}/shipments")

    public DeferredResult<ResponseEntity<?>> createShipment(
            @PathVariable String salesOrderId,
            @RequestBody SalesOrderStatusChangeRequestDto requestDto,
            @RequestParam String requesterId
    ) {
        return salesOrderService.createShipmentAsync(salesOrderId, requestDto, requesterId);
    }
}
