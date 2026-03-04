package org.ever._4ever_be_gw.scm.im.service;

import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ImHttpService {

    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardInboundList(String userId, Integer size);

    ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardOutboundList(String userId, Integer size);
}
