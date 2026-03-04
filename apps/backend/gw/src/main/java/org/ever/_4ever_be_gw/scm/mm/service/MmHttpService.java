package org.ever._4ever_be_gw.scm.mm.service;

import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface MmHttpService {

    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardPurchaseOrderList(String userId, int size);

    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardCompanyPurchaseOrderList(String userId, int size);

    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardPurchaseRequestList(String userId, int size);

    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardPurchaseOrdersOverall(int size);

    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardPurchaseRequestsOverall(int size);
}
