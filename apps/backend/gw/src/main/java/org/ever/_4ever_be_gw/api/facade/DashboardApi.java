package org.ever._4ever_be_gw.api.facade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.ever._4ever_be_gw.dashboard.service.DashboardHttpService;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "대시보드", description = "대시보드 워크플로우 API")
@ApiServerErrorResponse
public interface DashboardApi {

    @Operation(summary = "대시보드 워크플로우 조회", description = "사용자 역할 별로 2개의 탭과 n(기본 5개)개의 항목을 제공합니다.")
    public ResponseEntity<DashboardWorkflowResponseDto> getWorkflows(
            @AuthenticationPrincipal EverUserPrincipal principal

    );

    public ResponseEntity<DashboardStatisticsResponseDto> getDashboardStatistics();

}
