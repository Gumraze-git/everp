package org.ever._4ever_be_gw.scm.im.service;

import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.scm.im.dto.AddInventoryItemRequest;
import org.ever._4ever_be_gw.scm.im.dto.SalesOrderStatusChangeRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.StockTransferRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseCreateRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseUpdateRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.ItemInfoRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ImHttpService {

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardInboundList(String userId, Integer size);

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardOutboundList(String userId, Integer size);

    ResponseEntity<Object> getInventoryItems(String type, String keyword, String statusCode, Integer page, Integer size);

    ResponseEntity<Object> addInventoryItem(AddInventoryItemRequest request);

    ResponseEntity<Object> updateSafetyStock(String itemId, Integer safetyStock);

    ResponseEntity<Object> getInventoryItemDetail(String itemId);

    ResponseEntity<Object> getShortageItems(String statusCode, Integer page, Integer size);

    ResponseEntity<Object> getShortageItemsPreview();

    ResponseEntity<Object> getItemOptions();

    ResponseEntity<Object> getStockTransfers();

    ResponseEntity<Object> createStockTransfer(StockTransferRequestDto request, String requesterId);

    ResponseEntity<Object> getWarehouses(Integer page, Integer size);

    ResponseEntity<Object> getWarehouseDetail(String warehouseId);

    ResponseEntity<Object> getWarehouseManagerOptions();

    ResponseEntity<Object> searchItems(ItemInfoRequest request);

    ResponseEntity<Object> createWarehouse(WarehouseCreateRequestDto request);

    ResponseEntity<Object> updateWarehouse(String warehouseId, WarehouseUpdateRequestDto request);

    ResponseEntity<Object> getWarehouseOptions(String warehouseId);

    ResponseEntity<Object> getProductOptions();

    ResponseEntity<StatsResponseDto<StatsMetricsDto>> getShortageMetrics();

    ResponseEntity<StatsResponseDto<StatsMetricsDto>> getMetrics();

    ResponseEntity<StatsResponseDto<StatsMetricsDto>> getWarehouseMetrics();

    ResponseEntity<Object> getPurchaseOrders(String status, Integer page, Integer size, String startDate, String endDate);

    ResponseEntity<Object> getSalesOrders(String status, Integer page, Integer size);

    ResponseEntity<Object> getSalesOrder(String salesOrderId);

    ResponseEntity<Void> createShipment(String salesOrderId, SalesOrderStatusChangeRequestDto requestDto, String requesterId);
}
