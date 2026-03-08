package org.ever._4ever_be_gw.scm.im.service;

import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.scm.im.dto.SalesOrderStatusChangeRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.ItemInfoRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ImHttpService {

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardInboundList(String userId, Integer size);

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardOutboundList(String userId, Integer size);

    ResponseEntity<Object> getWarehouseManagerOptions();

    ResponseEntity<Object> searchItems(ItemInfoRequest request);

    ResponseEntity<Object> getWarehouseOptions(String warehouseId);

    ResponseEntity<StatsResponseDto<StatsMetricsDto>> getShortageMetrics();

    ResponseEntity<StatsResponseDto<StatsMetricsDto>> getMetrics();

    ResponseEntity<StatsResponseDto<StatsMetricsDto>> getWarehouseMetrics();

    ResponseEntity<Object> getPurchaseOrders(String status, Integer page, Integer size, String startDate, String endDate);

    ResponseEntity<Object> getSalesOrders(String status, Integer page, Integer size);

    ResponseEntity<Object> getSalesOrder(String salesOrderId);

    ResponseEntity<Void> createShipment(String salesOrderId, SalesOrderStatusChangeRequestDto requestDto, String requesterId);
}
