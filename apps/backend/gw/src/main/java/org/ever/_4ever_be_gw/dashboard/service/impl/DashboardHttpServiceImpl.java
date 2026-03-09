package org.ever._4ever_be_gw.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.restclient.RestClientProvider;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.ever._4ever_be_gw.dashboard.service.DashboardHttpService;
import org.ever._4ever_be_gw.dashboard.service.DashboardService;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardHttpServiceImpl implements DashboardHttpService {

    private final RestClientProvider restClientProvider;
    private final DashboardService dashboardService;

    @Override
    public ResponseEntity<DashboardStatisticsResponseDto> getDashboardStatistics() {
        try {
            RestClient businessClient = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

            ResponseEntity<DashboardStatisticsResponseDto> response = businessClient.get()
                    .uri("/dashboard/metrics")
                    .retrieve()
                    .toEntity(DashboardStatisticsResponseDto.class);

            if (response == null || response.getBody() == null) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "종합 대시보드 통계 응답이 비어 있습니다.");
            }
            log.info("종합 대시보드 통계 조회 성공");
            return response;

        } catch (RestClientResponseException ex) {
            log.error("종합 대시보드 통계 조회 실패 - Status: {}, Body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception e) {
            log.error("종합 대시보드 통계 조회 중 예기치 않은 오류 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "종합 대시보드 통계 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public ResponseEntity<DashboardWorkflowResponseDto> getWorkflows(
            EverUserPrincipal principal,
            String userType,
            String userRole
    ) {
        log.info("[INFO][DASHBOARD] 로컬 워크플로우 서비스 호출 - userType: {}, userRole: {}", userType, userRole);

        DashboardWorkflowResponseDto workflowResponse = dashboardService.getDashboardWorkflow(principal, null);

        return ResponseEntity.ok(workflowResponse);
    }
}
