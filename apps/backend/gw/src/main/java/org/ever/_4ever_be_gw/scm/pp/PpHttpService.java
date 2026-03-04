package org.ever._4ever_be_gw.scm.pp;

import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PpHttpService {

    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardQuotationsToProduction(String userId, Integer size);

    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardProductionInProgress(String userId, Integer size);
}
