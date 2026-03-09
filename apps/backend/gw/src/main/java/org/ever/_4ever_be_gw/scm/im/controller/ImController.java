package org.ever._4ever_be_gw.scm.im.controller;

import org.ever._4ever_be_gw.api.scm.im.ImApi;
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


@RestController
@RequestMapping("/scm-pp/iv")
@RequiredArgsConstructor
public class ImController implements ImApi {

    private final ImHttpService imHttpService;

    @GetMapping("/inventory-items")

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

    public ResponseEntity<Object> addInventoryItem(@RequestBody AddInventoryItemRequest request) {
        return imHttpService.addInventoryItem(request);
    }

    @PatchMapping("/items/{itemId}/safety-stock")

    public ResponseEntity<Object> updateSafetyStock(
            @PathVariable String itemId,
            @RequestParam Integer safetyStock
    ) {
        return imHttpService.updateSafetyStock(itemId, safetyStock);
    }

    @GetMapping("/items/{itemId}")

    public ResponseEntity<Object> getInventoryItemDetail(@PathVariable String itemId) {
        return imHttpService.getInventoryItemDetail(itemId);
    }

    @GetMapping("/shortage")

    public ResponseEntity<Object> getShortageItems(
            @RequestParam(required = false) String statusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return imHttpService.getShortageItems(statusCode, page, size);
    }

    @GetMapping("/shortage-previews")

    public ResponseEntity<Object> getShortageItemsPreview() {
        return imHttpService.getShortageItemsPreview();
    }

    @GetMapping("/item-options")

    public ResponseEntity<Object> getItemToggleList() {
        return imHttpService.getItemOptions();
    }

    @GetMapping("/stock-transfers")

    public ResponseEntity<Object> getStockTransfers() {
        return imHttpService.getStockTransfers();
    }

    @PostMapping("/stock-transfers")
    @PreAuthorize("hasAnyAuthority('IM_USER', 'IM_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> createStockTransfer(
            @RequestBody StockTransferRequestDto request,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return imHttpService.createStockTransfer(request, principal.getUserId());
    }

    @GetMapping("/warehouses")

    public ResponseEntity<Object> getWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return imHttpService.getWarehouses(page, size);
    }

    @GetMapping("/warehouses/{warehouseId}")

    public ResponseEntity<Object> getWarehouseDetail(@PathVariable String warehouseId) {
        return imHttpService.getWarehouseDetail(warehouseId);
    }

    @GetMapping("/warehouse-manager-options")
    public ResponseEntity<Object> getInventoryEmployees() {
        return imHttpService.getWarehouseManagerOptions();
    }

    @PostMapping("/warehouses")
    @PreAuthorize("hasAnyAuthority('IM_USER', 'IM_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> createWarehouse(@RequestBody WarehouseCreateRequestDto request) {
        return imHttpService.createWarehouse(request);
    }

    @PostMapping("/items/search")
    @PreAuthorize("hasAnyAuthority('IM_USER', 'IM_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> getItemInfoList(@RequestBody ItemInfoRequest request) {
        return imHttpService.searchItems(request);
    }

    @PutMapping("/warehouses/{warehouseId}")

    public ResponseEntity<Object> updateWarehouse(
            @PathVariable String warehouseId,
            @RequestBody WarehouseUpdateRequestDto request
    ) {
        return imHttpService.updateWarehouse(warehouseId, request);
    }

    @GetMapping("/warehouse-options")

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
