package org.ever._4ever_be_gw.scm.mm.service;

import org.ever._4ever_be_gw.common.dto.ValueKeyOptionDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MmHttpService {

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardPurchaseOrderList(String userId, int size);

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardCompanyPurchaseOrderList(String userId, int size);

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardPurchaseRequestList(String userId, int size);

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardPurchaseOrdersOverall(int size);

    ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardPurchaseRequestsOverall(int size);

    ResponseEntity<StatsResponseDto<StatsMetricsDto>> getMetrics();

    ResponseEntity<StatsResponseDto<StatsMetricsDto>> getSupplierOrderMetrics(String supplierUserId);

    ResponseEntity<List<ValueKeyOptionDto>> getPurchaseRequisitionStatusOptions();

    ResponseEntity<List<ValueKeyOptionDto>> getPurchaseOrderStatusOptions();

    ResponseEntity<List<ValueKeyOptionDto>> getSupplierStatusOptions();

    ResponseEntity<List<ValueKeyOptionDto>> getSupplierCategoryOptions();

    ResponseEntity<List<ValueKeyOptionDto>> getPurchaseRequisitionSearchTypeOptions();

    ResponseEntity<List<ValueKeyOptionDto>> getPurchaseOrderSearchTypeOptions();

    ResponseEntity<List<ValueKeyOptionDto>> getSupplierSearchTypeOptions();
}
