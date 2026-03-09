package org.ever._4ever_be_scm.scm.mm.controller;

import org.ever._4ever_be_scm.api.scm.mm.PurchaseOrderApi;
import java.time.LocalDate;
import org.ever._4ever_be_scm.common.exception.ErrorCode;
import org.ever._4ever_be_scm.common.exception.handler.ProblemDetailFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.PurchaseOrderDetailResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.PurchaseOrderListResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.PurchaseOrderRejectRequestDto;
import org.ever._4ever_be_scm.scm.mm.service.PurchaseOrderService;
import org.ever._4ever_be_scm.scm.mm.vo.PurchaseOrderSearchVo;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;



@RestController
@RequestMapping("/scm-pp/mm/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController implements PurchaseOrderApi {

    private final PurchaseOrderService purchaseOrderService;

    @GetMapping
    public ResponseEntity<PagedResponseDto<PurchaseOrderListResponseDto>> getPurchaseOrderList(
            @RequestParam(defaultValue = "ALL") String statusCode,

            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PurchaseOrderSearchVo searchVo = PurchaseOrderSearchVo.builder()
                .statusCode(statusCode)
                .type(type)
                .keyword(keyword)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();

        Page<PurchaseOrderListResponseDto> purchaseOrders = purchaseOrderService.getPurchaseOrderList(searchVo);
        PagedResponseDto<PurchaseOrderListResponseDto> response = PagedResponseDto.from(purchaseOrders);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/supplier/{userId}")
    public ResponseEntity<PagedResponseDto<PurchaseOrderListResponseDto>> getPurchaseOrderListBySupplier(
            @PathVariable String userId,
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PurchaseOrderSearchVo searchVo = PurchaseOrderSearchVo.builder()
                .statusCode(statusCode)
                .keyword(keyword)
                .startDate(startDate)
                .endDate(endDate)
                .page(page)
                .size(size)
                .build();

        Page<PurchaseOrderListResponseDto> purchaseOrders = purchaseOrderService.getPurchaseOrderListBySupplier(userId, searchVo);
        PagedResponseDto<PurchaseOrderListResponseDto> response = PagedResponseDto.from(purchaseOrders);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{purchaseOrderId}")
    public ResponseEntity<PurchaseOrderDetailResponseDto> getPurchaseOrderDetail(
            @PathVariable String purchaseOrderId) {

        PurchaseOrderDetailResponseDto detail = purchaseOrderService.getPurchaseOrderDetail(purchaseOrderId);

        return ResponseEntity.ok(detail);
    }

    /**
     * 발주서 승인 (비동기 - 분산 트랜잭션)
     */
    @PostMapping("/{purchaseOrderId}/approve")
    public DeferredResult<ResponseEntity<?>> approvePurchaseOrder(
            @PathVariable String purchaseOrderId,
            @RequestParam String requesterId) {

        return purchaseOrderService.approvePurchaseOrderAsync(purchaseOrderId, requesterId);
    }

    /**
     * 발주서 반려
     */
    @PostMapping("/{purchaseOrderId}/reject")
    public ResponseEntity<Void> rejectPurchaseOrder(
            @PathVariable String purchaseOrderId,
            @RequestParam String requesterId,
            @RequestBody PurchaseOrderRejectRequestDto requestDto) {
        purchaseOrderService.rejectPurchaseOrder(purchaseOrderId, requesterId ,requestDto.getReason());
        return ResponseEntity.noContent().build();
    }

    /**
     * 배송 시작
     */
    @PostMapping("/{purchaseOrderId}/start-delivery")
    public ResponseEntity<Void> startDelivery(
            @PathVariable String purchaseOrderId) {
        purchaseOrderService.startDelivery(purchaseOrderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 입고 완료
     */
    @PostMapping("/{purchaseOrderId}/complete-delivery")
    public ResponseEntity<Void> completeDelivery(
            @PathVariable String purchaseOrderId) {
        purchaseOrderService.completeDelivery(purchaseOrderId);
        return ResponseEntity.noContent().build();
    }
}
