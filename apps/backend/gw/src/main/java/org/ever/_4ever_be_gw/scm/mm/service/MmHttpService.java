package org.ever._4ever_be_gw.scm.mm.service;

import org.ever._4ever_be_gw.common.dto.ValueKeyOptionDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.scm.mm.dto.PurchaseOrderRejectRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.PurchaseRequisitionCreateRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.PurchaseRequisitionRejectRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.StockPurchaseRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.SupplierUpdateRequestDto;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

public interface MmHttpService {

    ResponseEntity<Object> getSupplierList(
        String statusCode,
        String category,
        String type,
        String keyword,
        int page,
        int size
    );

    ResponseEntity<Object> getSupplierDetail(String supplierId);

    ResponseEntity<Object> updateSupplier(String supplierId, SupplierUpdateRequestDto requestDto);

    ResponseEntity<Object> createStockPurchaseRequest(StockPurchaseRequestDto requestDto, String requesterId);

    ResponseEntity<Object> getPurchaseRequisitionList(
        String statusCode,
        String type,
        String keyword,
        LocalDate startDate,
        LocalDate endDate,
        int page,
        int size
    );

    ResponseEntity<Object> getPurchaseRequisitionDetail(String purchaseRequisitionId);

    ResponseEntity<Object> createPurchaseRequisition(
        PurchaseRequisitionCreateRequestDto requestDto,
        String requesterId
    );

    ResponseEntity<Object> approvePurchaseRequisition(String purchaseRequisitionId, String requesterId);

    ResponseEntity<Object> rejectPurchaseRequisition(
        String purchaseRequisitionId,
        String requesterId,
        PurchaseRequisitionRejectRequestDto requestDto
    );

    ResponseEntity<Object> getPurchaseOrderList(
        String statusCode,
        String type,
        String keyword,
        LocalDate startDate,
        LocalDate endDate,
        int page,
        int size,
        String userId,
        String userType
    );

    ResponseEntity<Object> getPurchaseOrderDetail(String purchaseOrderId);

    ResponseEntity<Object> approvePurchaseOrder(String purchaseOrderId, String requesterId);

    ResponseEntity<Object> rejectPurchaseOrder(
        String purchaseOrderId,
        String requesterId,
        PurchaseOrderRejectRequestDto requestDto
    );

    ResponseEntity<Object> startDelivery(String purchaseOrderId);

    ResponseEntity<Object> completeDelivery(String purchaseOrderId);

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
