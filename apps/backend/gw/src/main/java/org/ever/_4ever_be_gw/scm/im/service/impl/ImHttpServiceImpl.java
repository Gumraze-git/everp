package org.ever._4ever_be_gw.scm.im.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseMapper;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.ever._4ever_be_gw.scm.mm.dto.ItemInfoRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

@Service("imHttpService")
@RequiredArgsConstructor
@Slf4j
public class ImHttpServiceImpl implements ImHttpService {

    private static final ParameterizedTypeReference<List<DashboardWorkflowItemDto>> DASHBOARD_WORKFLOW_ITEMS_TYPE =
        new ParameterizedTypeReference<>() {};

    private final WebClientProvider webClientProvider;

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardInboundList(String userId, Integer size) {
        requireUserId(userId, "inventory userId");
        int pageSize = normalizeSize(size);
        return fetchEntity(
            ApiClientKey.SCM_PP,
            "대시보드 입고 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/dashboard/inbound")
                .queryParam("userId", userId)
                .queryParam("size", pageSize)
                .build(),
            DASHBOARD_WORKFLOW_ITEMS_TYPE
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardOutboundList(String userId, Integer size) {
        requireUserId(userId, "inventory userId");
        int pageSize = normalizeSize(size);
        return fetchEntity(
            ApiClientKey.SCM_PP,
            "대시보드 출고 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/dashboard/outbound")
                .queryParam("userId", userId)
                .queryParam("size", pageSize)
                .build(),
            DASHBOARD_WORKFLOW_ITEMS_TYPE
        );
    }

    @Override
    public ResponseEntity<Object> getWarehouseManagerOptions() {
        return fetchObject(
            ApiClientKey.BUSINESS,
            "창고 관리자 옵션 조회",
            uriBuilder -> uriBuilder.path("/hrm/manager-options").build()
        );
    }

    @Override
    public ResponseEntity<Object> searchItems(ItemInfoRequest request) {
        return postForObject(
            "재고성 구매요청 품목 조회",
            uriBuilder -> uriBuilder.path("/scm-pp/iv/items/search").build(),
            request
        );
    }

    @Override
    public ResponseEntity<Object> getWarehouseOptions(String warehouseId) {
        return fetchObject(
            ApiClientKey.SCM_PP,
            "창고 옵션 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder.path("/scm-pp/iv/warehouse-options");
                if (warehouseId != null && !warehouseId.isBlank()) {
                    builder.queryParam("warehouseId", warehouseId);
                }
                return builder.build();
            }
        );
    }

    @Override
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getShortageMetrics() {
        return toStatsResponse(
            fetchJson(
                "재고 부족 통계 조회",
                uriBuilder -> uriBuilder.path("/scm-pp/iv/shortage-metrics").build()
            )
        );
    }

    @Override
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getMetrics() {
        return toStatsResponse(
            fetchJson(
                "재고 통계 조회",
                uriBuilder -> uriBuilder.path("/scm-pp/iv/metrics").build()
            )
        );
    }

    @Override
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getWarehouseMetrics() {
        return toStatsResponse(
            fetchJson(
                "창고 통계 조회",
                uriBuilder -> uriBuilder.path("/scm-pp/iv/warehouse-metrics").build()
            )
        );
    }

    private ResponseEntity<StatsResponseDto<StatsMetricsDto>> toStatsResponse(ResponseEntity<JsonNode> response) {
        return ResponseEntity.status(response.getStatusCode()).body(StatsResponseMapper.fromJson(response.getBody()));
    }

    private ResponseEntity<JsonNode> fetchJson(String operation, Function<UriBuilder, URI> uriFunction) {
        try {
            ResponseEntity<JsonNode> response = scmClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriFunction)
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
            return requireBody(operation, response);
        } catch (WebClientResponseException ex) {
            log.error("{} 실패 - status: {}, body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private ResponseEntity<Object> fetchObject(
        ApiClientKey apiClientKey,
        String operation,
        Function<UriBuilder, URI> uriFunction
    ) {
        try {
            ResponseEntity<Object> response = scmClient(apiClientKey)
                .get()
                .uri(uriFunction)
                .retrieve()
                .toEntity(Object.class)
                .block();
            return requireBody(operation, response);
        } catch (WebClientResponseException ex) {
            log.error("{} 실패 - status: {}, body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private ResponseEntity<Object> postForObject(
        String operation,
        Function<UriBuilder, URI> uriFunction,
        Object requestBody
    ) {
        try {
            ResponseEntity<Object> response = scmClient(ApiClientKey.SCM_PP)
                .post()
                .uri(uriFunction)
                .bodyValue(requestBody)
                .retrieve()
                .toEntity(Object.class)
                .block();
            return requireBody(operation, response);
        } catch (WebClientResponseException ex) {
            log.error("{} 실패 - status: {}, body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private <T> ResponseEntity<T> fetchEntity(
        ApiClientKey apiClientKey,
        String operation,
        Function<UriBuilder, URI> uriFunction,
        ParameterizedTypeReference<T> typeReference
    ) {
        try {
            ResponseEntity<T> response = scmClient(apiClientKey)
                .get()
                .uri(uriFunction)
                .retrieve()
                .toEntity(typeReference)
                .block();
            return requireBody(operation, response);
        } catch (WebClientResponseException ex) {
            log.error("{} 실패 - status: {}, body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private <T> ResponseEntity<T> requireBody(String operation, ResponseEntity<T> response) {
        if (response == null || response.getBody() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 응답이 비어 있습니다.");
        }
        return response;
    }

    private WebClient scmClient(ApiClientKey apiClientKey) {
        return webClientProvider.getWebClient(apiClientKey);
    }

    private int normalizeSize(Integer size) {
        return size != null && size > 0 ? size : 5;
    }

    private void requireUserId(String userId, String fieldName) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, fieldName + " is required");
        }
    }
}
