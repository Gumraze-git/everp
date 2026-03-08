package org.ever._4ever_be_gw.scm.pp.service.impl;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.scm.pp.PpHttpService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

import java.util.List;
import java.util.function.Function;

@Service("ppHttpService")
@RequiredArgsConstructor
@Slf4j
public class PpHttpServiceImpl implements PpHttpService {

    private final WebClientProvider webClientProvider;

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardQuotationsToProduction(
            String userId,
            Integer size
    ) {
        log.debug("[DASHBOARD][PP] 생산 전환 견적 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, "production userId is required");
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            ResponseEntity<List<DashboardWorkflowItemDto>> response = ppClient()
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/scm-pp/pp/workflow-items/quotations")
                    .queryParam("userId", userId)
                    .queryParam("size", pageSize)
                    .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<DashboardWorkflowItemDto>>() {})
                .block();
            ResponseEntity<List<DashboardWorkflowItemDto>> result = requireBody("생산 전환 견적 목록 조회", response);
            log.info("[INFO][DASHBOARD][PP] 생산 전환 견적 목록 조회 성공");
            return result;
        } catch (WebClientResponseException ex) {
            log.error("대시보드 생산 전환 견적 목록 조회 실패 - Status: {}, Body: {}",
                ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][PP] 생산 전환 견적 목록 조회 중 에러 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "대시보드 생산 전환 견적 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardProductionInProgress(String userId, Integer size) {
        log.debug("[DASHBOARD][PP] 생산 진행(MES) 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, "production userId is required");
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            ResponseEntity<List<DashboardWorkflowItemDto>> response = ppClient()
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path("/scm-pp/pp/workflow-items/mes")
                    .queryParam("userId", userId)
                    .queryParam("size", pageSize)
                    .build())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<DashboardWorkflowItemDto>>() {})
                .block();
            ResponseEntity<List<DashboardWorkflowItemDto>> result = requireBody("생산 진행 목록 조회", response);
            log.info("[INFO][DASHBOARD][PP] 생산 진행 목록 조회 성공");
            return result;
        } catch (WebClientResponseException ex) {
            log.error("대시보드 생산 진행 목록 조회 실패 - Status: {}, Body: {}",
                ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][PP] 생산 진행 목록 조회 중 에러 발생", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "대시보드 생산 진행 목록 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public ResponseEntity<Object> get(String operation, String path, Object... uriVariables) {
        return executeObject(operation, client -> client.get().uri(path, uriVariables).accept(MediaType.APPLICATION_JSON));
    }

    @Override
    public ResponseEntity<Object> get(String operation, Function<UriBuilder, URI> uriFunction) {
        return executeObject(operation, client -> client.get().uri(uriFunction).accept(MediaType.APPLICATION_JSON));
    }

    @Override
    public ResponseEntity<Object> postWithoutBody(String operation, String path, Object... uriVariables) {
        return executeObject(operation, client -> client.post().uri(path, uriVariables).contentType(MediaType.APPLICATION_JSON));
    }

    @Override
    public ResponseEntity<Object> postWithoutBody(String operation, Function<UriBuilder, URI> uriFunction) {
        return executeObject(operation, client -> client.post().uri(uriFunction).contentType(MediaType.APPLICATION_JSON));
    }

    @Override
    public ResponseEntity<Object> post(String operation, String path, Object body, Object... uriVariables) {
        return executeObject(
            operation,
            client -> client.post()
                .uri(path, uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
        );
    }

    @Override
    public ResponseEntity<Object> post(String operation, Function<UriBuilder, URI> uriFunction, Object body) {
        return executeObject(
            operation,
            client -> client.post()
                .uri(uriFunction)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
        );
    }

    @Override
    public ResponseEntity<Object> patch(String operation, String path, Object body, Object... uriVariables) {
        return executeObject(
            operation,
            client -> client.patch()
                .uri(path, uriVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
        );
    }

    private ResponseEntity<Object> executeObject(
        String operation,
        Function<WebClient, WebClient.RequestHeadersSpec<?>> requestFunction
    ) {
        try {
            ResponseEntity<Object> response = requestFunction.apply(ppClient())
                .retrieve()
                .toEntity(Object.class)
                .block();
            return requireBody(operation, response);
        } catch (WebClientResponseException ex) {
            log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private WebClient ppClient() {
        return webClientProvider.getWebClient(ApiClientKey.SCM_PP);
    }

    private <T> ResponseEntity<T> requireBody(String operation, ResponseEntity<T> response) {
        if (response == null || response.getBody() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 응답이 비어 있습니다.");
        }
        return response;
    }
}
