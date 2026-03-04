package org.ever._4ever_be_gw.scm.im.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service("imHttpService")
@RequiredArgsConstructor
@Slf4j
public class ImHttpServiceImpl implements ImHttpService {

    private final WebClientProvider webClientProvider;

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardInboundList(String userId, Integer size) {
        log.debug("[DASHBOARD][IM] 입고 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("inventory userId is required", HttpStatus.BAD_REQUEST, null)
            );
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            WebClient scmClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

            ApiResponse<List<DashboardWorkflowItemDto>> body = scmClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/dashboard/inbound")
                            .queryParam("userId", userId)
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][IM] SCM 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "SCM 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][IM] SCM 서버 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SCM 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][IM] 입고 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "입고 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 입고 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][IM] 입고 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 입고 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardOutboundList(String userId, Integer size) {
        log.debug("[DASHBOARD][IM] 출고 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("inventory userId is required", HttpStatus.BAD_REQUEST, null)
            );
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            WebClient scmClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

            ApiResponse<List<DashboardWorkflowItemDto>> body = scmClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/dashboard/outbound")
                            .queryParam("userId", userId)
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][IM] SCM 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "SCM 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][IM] SCM 서버 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "SCM 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][IM] 출고 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "출고 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 출고 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][IM] 출고 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 출고 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    private <T> ResponseEntity<ApiResponse<T>> handleWebClientError(String operation, WebClientResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorBody = ex.getResponseBodyAsString();

        log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);

        return ResponseEntity.status(status).body(
                ApiResponse.fail(operation + " 중 오류가 발생했습니다.", status, null)
        );
    }
}
