package org.ever._4ever_be_gw.scm.mm.controller;

import org.ever._4ever_be_gw.api.scm.mm.MmApi;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.common.dto.ValueKeyOptionDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.scm.mm.dto.*;
import org.ever._4ever_be_gw.scm.mm.service.MmHttpService;
import org.ever._4ever_be_gw.scm.mm.service.MmService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;


@RestController
@RequestMapping("/scm-pp/mm")
@RequiredArgsConstructor
public class MmController implements MmApi {

    private final MmService mmService;
    private final MmHttpService mmHttpService;

    // 공급업체 목록 조회
    @GetMapping("/suppliers")

    public ResponseEntity<Object> getSupplierList(

            @RequestParam(defaultValue = "ALL") String statusCode,

            @RequestParam(defaultValue = "ALL") String category,

            @RequestParam(required = false) String type,

            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return mmHttpService.getSupplierList(statusCode, category, type, keyword, page, size);
    }

    // 공급업체 상세 조회
    @GetMapping("/suppliers/{supplierId}")

    public ResponseEntity<Object> getSupplierDetail(@PathVariable String supplierId) {
        return mmHttpService.getSupplierDetail(supplierId);
    }

    // 공급업체 등록 (SAGA)
    @PostMapping("/suppliers")
    @PreAuthorize("hasAnyAuthority('MM_USER', 'MM_ADMIN', 'ALL_ADMIN')")

    public Mono<ResponseEntity<CreateAuthUserResultDto>> createSupplier(
        @RequestBody SupplierCreateRequestDto requestDto
    ) {
        return mmService.createSupplier(requestDto)
            .map(ResponseEntity::ok);
    }

    @PatchMapping("/suppliers/{supplierId}")

    public ResponseEntity<Object> updateSupplier(
            @PathVariable String supplierId,
            @RequestBody SupplierUpdateRequestDto requestDto
    ) {
        return mmHttpService.updateSupplier(supplierId, requestDto);
    }


    // 재고성 구매요청 생성
    @PostMapping("/stock-purchase-requisitions")
    @PreAuthorize("hasAnyAuthority('MM_USER', 'MM_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> createStockPurchaseRequest(
            @RequestBody StockPurchaseRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal requester
    ) {

        String requesterId = requester.getUserId();
        return mmHttpService.createStockPurchaseRequest(requestDto, requesterId);
    }

    // 구매요청서 목록 조회
    @GetMapping("/purchase-requisitions")

    public ResponseEntity<Object> getPurchaseRequisitionList(

            @RequestParam(defaultValue = "ALL") String statusCode,

            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return mmHttpService.getPurchaseRequisitionList(
            statusCode,
            type,
            keyword,
            startDate,
            endDate,
            page,
            size
        );
    }

    // 구매요청서 상세 조회
    @GetMapping("/purchase-requisitions/{purchaseRequisitionId}")

    public ResponseEntity<Object> getPurchaseRequisitionDetail(@PathVariable String purchaseRequisitionId) {
        return mmHttpService.getPurchaseRequisitionDetail(purchaseRequisitionId);
    }

    // 구매요청서 생성
    @PostMapping("/purchase-requisitions")

    public ResponseEntity<Object> createPurchaseRequisition(
            @RequestBody PurchaseRequisitionCreateRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal principal
            ) {

        return mmHttpService.createPurchaseRequisition(requestDto, principal.getUserId());
    }

    // 구매요청서 승인
    @PostMapping("/purchase-requisitions/{purchaseRequisitionId}/approvals")
    @PreAuthorize("hasAnyAuthority('MM_USER', 'MM_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> approvePurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return mmHttpService.approvePurchaseRequisition(purchaseRequisitionId, principal.getUserId());
    }


    // 구매요청서 반려
    @PostMapping("/purchase-requisitions/{purchaseRequisitionId}/rejections")
    @PreAuthorize("hasAnyAuthority('MM_USER', 'MM_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> rejectPurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @AuthenticationPrincipal EverUserPrincipal principal,
            @RequestBody PurchaseRequisitionRejectRequestDto requestDto
    ) {
        return mmHttpService.rejectPurchaseRequisition(
            purchaseRequisitionId,
            principal.getUserId(),
            requestDto
        );
    }


    // 발주서 목록 조회
    @GetMapping("/purchase-orders")

    public ResponseEntity<Object> getPurchaseOrderList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return mmHttpService.getPurchaseOrderList(
            statusCode,
            type,
            keyword,
            startDate,
            endDate,
            page,
            size,
            principal.getUserId(),
            principal.getUserType()
        );
    }


    // 발주서 상세 조회
    @GetMapping("/purchase-orders/{purchaseOrderId}")

    public ResponseEntity<Object> getPurchaseOrderDetail(@PathVariable String purchaseOrderId) {
        return mmHttpService.getPurchaseOrderDetail(purchaseOrderId);
    }

    // 발주서 승인
    @PostMapping("/purchase-orders/{purchaseOrderId}/approvals")

    public ResponseEntity<Object> approvePurchaseOrder(
            @PathVariable String purchaseOrderId,
            @AuthenticationPrincipal EverUserPrincipal principal
            ) {
        return mmHttpService.approvePurchaseOrder(purchaseOrderId, principal.getUserId());
    }

    // 발주서 반려
    @PostMapping("/purchase-orders/{purchaseOrderId}/rejections")

    public ResponseEntity<Object> rejectPurchaseOrder(
            @PathVariable String purchaseOrderId,
            @RequestBody PurchaseOrderRejectRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return mmHttpService.rejectPurchaseOrder(
            purchaseOrderId,
            principal.getUserId(),
            requestDto
        );
    }


    // MM 통계 조회
    @GetMapping("/metrics")

    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getMMStatistics() {
        return mmHttpService.getMetrics();
    }

    @GetMapping("/supplier-users/me/metrics/order-counts")

    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getSupplierStatistics(
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return mmHttpService.getSupplierOrderMetrics(principal.getUserId());
    }

    // 배송 시작
    @PostMapping("/purchase-orders/{purchaseOrderId}/delivery-starts")

    public ResponseEntity<Object> startDelivery(
            @PathVariable String purchaseOrderId
    ) {
        return mmHttpService.startDelivery(purchaseOrderId);
    }

    // 입고 시작
    @PostMapping("/purchase-orders/{purchaseOrderId}/delivery-completions")

    public ResponseEntity<Object> completeDelivery(
            @PathVariable String purchaseOrderId
    ) {
        return mmHttpService.completeDelivery(purchaseOrderId);
    }



    // 구매요청서 상태 토글
    @GetMapping("/purchase-requisition-status-options")

    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseRequisitionStatusOptions() {
        return mmHttpService.getPurchaseRequisitionStatusOptions();
    }

    // 발주서 상태 옵션
    @GetMapping("/purchase-order-status-options")

    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseOrderStatusOptions() {
        return mmHttpService.getPurchaseOrderStatusOptions();
    }

    // 공급업체 상태 옵션
    @GetMapping("/supplier-status-options")

    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getSupplierStatusOptions() {
        return mmHttpService.getSupplierStatusOptions();
    }

    // 공급업체 카테고리 옵션
    @GetMapping("/supplier-category-options")

    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getSupplierCategoryOptions() {
        return mmHttpService.getSupplierCategoryOptions();
    }

    // 구매 요청 검색 타입 옵션
    @GetMapping("/purchase-requisition-search-type-options")

    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseRequisitionSearchTypeOptions() {
        return mmHttpService.getPurchaseRequisitionSearchTypeOptions();
    }

    // 발주서 검색 타입 옵션
    @GetMapping("/purchase-order-search-type-options")

    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseOrderSearchTypeOptions() {
        return mmHttpService.getPurchaseOrderSearchTypeOptions();
    }

    // 공급업체 검색 타입 옵션
    @GetMapping("/supplier-search-type-options")

    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getSupplierSearchTypeOptions() {
        return mmHttpService.getSupplierSearchTypeOptions();
    }

}
