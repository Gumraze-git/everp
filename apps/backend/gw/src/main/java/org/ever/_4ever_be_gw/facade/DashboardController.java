package org.ever._4ever_be_gw.facade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.ever._4ever_be_gw.dashboard.service.DashboardHttpService;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.user.dto.UserInfoResponse;
import org.ever._4ever_be_gw.user.service.UserInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "대시보드", description = "대시보드 워크플로우 API")
public class DashboardController {
    private final DashboardHttpService dashboardHttpService;
    private final UserInfoService userInfoService;

    @GetMapping("/workflows")
    @Operation(
            summary = "대시보드 워크플로우 조회",
            description = "사용자 역할 별로 2개의 탭과 n(기본 5개)개의 항목을 제공합니다."
    )
    public ResponseEntity<ApiResponse<Object>> getWorkflows(
            @AuthenticationPrincipal EverUserPrincipal principal

    ) {
        log.info("[INFO][DASHBOARD] 대시보드 워크플로우 조회 요청");

        // 사용자 정보(userType, userRole)
        String userType = principal.getUserType();
        String userRole = principal.getUserRole();
        log.info("[INFO][DASHBOARD] 대시보드 사용자 정보 조회, 사용자 유형(userType): {}, 사용자 역할(userRole): {}", userType, userRole);

        return dashboardHttpService.getWorkflows(principal, userType, userRole);
    }

    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<DashboardStatisticsResponseDto>> getDashboardStatistics() {
        return dashboardHttpService.getDashboardStatistics();
    }

}
