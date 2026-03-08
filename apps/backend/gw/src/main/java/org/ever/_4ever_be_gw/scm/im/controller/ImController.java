package org.ever._4ever_be_gw.scm.im.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.scm.im.dto.AddInventoryItemRequest;
import org.ever._4ever_be_gw.scm.im.dto.StockTransferRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseCreateRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseUpdateRequestDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.ever._4ever_be_gw.scm.mm.dto.ItemInfoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재고관리(IM)", description = "재고 관리 API")
@RestController
@RequestMapping("/scm-pp/iv")
@RequiredArgsConstructor
public class ImController {

    private final ImHttpService imHttpService;

    @GetMapping("/inventory-items")
    @io.swagger.v3.oas.annotations.Operation(summary = "재고 목록 조회")
    public ResponseEntity<Object> getInventoryItems(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusCode", required = false, defaultValue = "ALL") String statusCode,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        return imHttpService.getInventoryItems(type, keyword, statusCode, page, size);
    }

    @PostMapping("/items")
    @io.swagger.v3.oas.annotations.Operation(summary = "원자재 추가 (재고관리에서 사용) ")
    public ResponseEntity<Object> addInventoryItem(@RequestBody AddInventoryItemRequest request) {
        return imHttpService.addInventoryItem(request);
    }

    @PatchMapping("/items/{itemId}/safety-stock")
    @io.swagger.v3.oas.annotations.Operation(summary = "안전재고 수정")
    public ResponseEntity<Object> updateSafetyStock(
            @PathVariable String itemId,
            @RequestParam Integer safetyStock
    ) {
        return imHttpService.updateSafetyStock(itemId, safetyStock);
    }

    @GetMapping("/items/{itemId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "재고 상세 조회")
    public ResponseEntity<Object> getInventoryItemDetail(@PathVariable String itemId) {
        return imHttpService.getInventoryItemDetail(itemId);
    }

    @GetMapping("/shortage")
    @io.swagger.v3.oas.annotations.Operation(summary = "부족 재고 목록 조회")
    public ResponseEntity<Object> getShortageItems(
            @RequestParam(required = false) String statusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return imHttpService.getShortageItems(statusCode, page, size);
    }

    @GetMapping("/shortage-previews")
    @io.swagger.v3.oas.annotations.Operation(summary = "부족 재고 간단 조회")
    public ResponseEntity<Object> getShortageItemsPreview() {
        return imHttpService.getShortageItemsPreview();
    }

    @GetMapping("/item-options")
    @io.swagger.v3.oas.annotations.Operation(summary = "자재 추가 시 자재 토글 목록 조회")
    public ResponseEntity<Object> getItemToggleList() {
        return imHttpService.getItemOptions();
    }

    @GetMapping("/stock-transfers")
    @io.swagger.v3.oas.annotations.Operation(summary = "재고 이동 목록 조회 (상위 5개)")
    public ResponseEntity<Object> getStockTransfers() {
        return imHttpService.getStockTransfers();
    }

    @PostMapping("/stock-transfers")
    @PreAuthorize("hasAnyAuthority('IM_USER', 'IM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "창고간 재고 이동")
    public ResponseEntity<Object> createStockTransfer(
            @RequestBody StockTransferRequestDto request,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return imHttpService.createStockTransfer(request, principal.getUserId());
    }

    @GetMapping("/warehouses")
    @io.swagger.v3.oas.annotations.Operation(summary = "창고 목록 조회")
    public ResponseEntity<Object> getWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return imHttpService.getWarehouses(page, size);
    }

    @GetMapping("/warehouses/{warehouseId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "창고 상세 조회")
    public ResponseEntity<Object> getWarehouseDetail(@PathVariable String warehouseId) {
        return imHttpService.getWarehouseDetail(warehouseId);
    }

    @GetMapping("/warehouse-manager-options")
    public ResponseEntity<Object> getInventoryEmployees() {
        return imHttpService.getWarehouseManagerOptions();
    }

    @PostMapping("/warehouses")
    @PreAuthorize("hasAnyAuthority('IM_USER', 'IM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "창고 추가")
    public ResponseEntity<Object> createWarehouse(@RequestBody WarehouseCreateRequestDto request) {
        return imHttpService.createWarehouse(request);
    }

    @PostMapping("/items/search")
    @PreAuthorize("hasAnyAuthority('IM_USER', 'IM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(summary = "재고성 구매요청을 위한 item 정보 get")
    public ResponseEntity<Object> getItemInfoList(@RequestBody ItemInfoRequest request) {
        return imHttpService.searchItems(request);
    }

    @PutMapping("/warehouses/{warehouseId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "창고 정보 수정")
    public ResponseEntity<Object> updateWarehouse(
            @PathVariable String warehouseId,
            @RequestBody WarehouseUpdateRequestDto request
    ) {
        return imHttpService.updateWarehouse(warehouseId, request);
    }

    @GetMapping("/warehouse-options")
    @io.swagger.v3.oas.annotations.Operation(summary = "창고 드롭다운 목록 조회")
    public ResponseEntity<Object> getWarehouseDropdown(@RequestParam(required = false) String warehouseId) {
        return imHttpService.getWarehouseOptions(warehouseId);
    }

    @GetMapping("/shortage-metrics")
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getShortageStatistic() {
        return imHttpService.getShortageMetrics();
    }

    @GetMapping("/metrics")
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getImStatistic() {
        return imHttpService.getMetrics();
    }

    @GetMapping("/warehouse-metrics")
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getWarehouseStatistic() {
        return imHttpService.getWarehouseMetrics();
    }
}
