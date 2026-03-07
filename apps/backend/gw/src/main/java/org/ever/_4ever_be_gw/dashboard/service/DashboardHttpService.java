package org.ever._4ever_be_gw.dashboard.service;

import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;
import org.springframework.http.ResponseEntity;

public interface DashboardHttpService {
    // 대시보드 통계 요청
    ResponseEntity<DashboardStatisticsResponseDto> getDashboardStatistics();

    // 대시보드 워크플로우 요청
    ResponseEntity<DashboardWorkflowResponseDto> getWorkflows(EverUserPrincipal principal, String userType, String userRole);


}
