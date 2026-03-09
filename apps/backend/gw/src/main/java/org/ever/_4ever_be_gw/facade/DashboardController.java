package org.ever._4ever_be_gw.facade;

import org.ever._4ever_be_gw.api.facade.DashboardApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.ever._4ever_be_gw.dashboard.service.DashboardHttpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor

public class DashboardController implements DashboardApi {
    private final DashboardHttpService dashboardHttpService;

    @GetMapping("/workflows")

    public ResponseEntity<DashboardWorkflowResponseDto> getWorkflows(
            @AuthenticationPrincipal EverUserPrincipal principal

    ) {
        log.info("[INFO][DASHBOARD] 대시보드 워크플로우 조회 요청");

        // 사용자 정보(userType, userRole)
        String userType = principal.getUserType();
        String userRole = principal.getUserRole();
        log.info("[INFO][DASHBOARD] 대시보드 사용자 정보 조회, 사용자 유형(userType): {}, 사용자 역할(userRole): {}", userType, userRole);

        return dashboardHttpService.getWorkflows(principal, userType, userRole);
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardStatisticsResponseDto> getDashboardStatistics() {
        return dashboardHttpService.getDashboardStatistics();
    }

}
