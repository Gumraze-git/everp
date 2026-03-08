package org.ever._4ever_be_gw.scm.mm.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.common.dto.ValueKeyOptionDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseMapper;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.scm.mm.service.MmHttpService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

@Service("mmHttpService")
@RequiredArgsConstructor
@Slf4j
public class MmHttpServiceImpl implements MmHttpService {

    private static final ParameterizedTypeReference<List<DashboardWorkflowItemDto>> DASHBOARD_WORKFLOW_ITEMS_TYPE =
        new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<ValueKeyOptionDto>> VALUE_KEY_OPTIONS_TYPE =
        new ParameterizedTypeReference<>() {};

    private final WebClientProvider webClientProvider;

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardPurchaseOrderList(String userId, int size) {
        requireUserId(userId, "purchase userId");
        int pageSize = normalizeSize(size);
        return fetchEntity(
            "대시보드 구매 발주서 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/dashboard/purchase-orders/supplier")
                .queryParam("userId", userId)
                .queryParam("size", pageSize)
                .build(),
            DASHBOARD_WORKFLOW_ITEMS_TYPE
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardCompanyPurchaseOrderList(String userId, int size) {
        int pageSize = normalizeSize(size);
        return fetchEntity(
            "대시보드 기업 발주서 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/dashboard/purchase-orders/mm")
                .queryParam("size", pageSize)
                .build(),
            DASHBOARD_WORKFLOW_ITEMS_TYPE
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardPurchaseRequestList(String userId, int size) {
        requireUserId(userId, "purchase request userId");
        int pageSize = normalizeSize(size);
        return fetchEntity(
            "대시보드 구매 요청 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/dashboard/purchase-requests")
                .queryParam("userId", userId)
                .queryParam("size", pageSize)
                .build(),
            DASHBOARD_WORKFLOW_ITEMS_TYPE
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardPurchaseOrdersOverall(int size) {
        int pageSize = normalizeSize(size);
        return fetchEntity(
            "대시보드 전체 발주서 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/dashboard/purchase-orders")
                .queryParam("size", pageSize)
                .build(),
            DASHBOARD_WORKFLOW_ITEMS_TYPE
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardPurchaseRequestsOverall(int size) {
        int pageSize = normalizeSize(size);
        return fetchEntity(
            "대시보드 전체 구매 요청 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/dashboard/purchase-requests/company")
                .queryParam("size", pageSize)
                .build(),
            DASHBOARD_WORKFLOW_ITEMS_TYPE
        );
    }

    @Override
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getMetrics() {
        return toStatsResponse(
            fetchJson(
                "MM 통계 조회",
                uriBuilder -> uriBuilder.path("/scm-pp/mm/metrics").build()
            )
        );
    }

    @Override
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getSupplierOrderMetrics(String supplierUserId) {
        requireUserId(supplierUserId, "supplier userId");
        return toStatsResponse(
            fetchJson(
                "공급사 발주서 통계 조회",
                uriBuilder -> uriBuilder
                    .path("/scm-pp/mm/supplier-users/{userId}/metrics/order-counts")
                    .build(supplierUserId)
            )
        );
    }

    @Override
    public ResponseEntity<List<ValueKeyOptionDto>> getPurchaseRequisitionStatusOptions() {
        return fetchOptionList("구매요청서 상태 옵션 조회", "/scm-pp/mm/purchase-requisition-status-options");
    }

    @Override
    public ResponseEntity<List<ValueKeyOptionDto>> getPurchaseOrderStatusOptions() {
        return fetchOptionList("발주서 상태 옵션 조회", "/scm-pp/mm/purchase-order-status-options");
    }

    @Override
    public ResponseEntity<List<ValueKeyOptionDto>> getSupplierStatusOptions() {
        return fetchOptionList("공급업체 상태 옵션 조회", "/scm-pp/mm/supplier-status-options");
    }

    @Override
    public ResponseEntity<List<ValueKeyOptionDto>> getSupplierCategoryOptions() {
        return fetchOptionList("공급업체 카테고리 옵션 조회", "/scm-pp/mm/supplier-category-options");
    }

    @Override
    public ResponseEntity<List<ValueKeyOptionDto>> getPurchaseRequisitionSearchTypeOptions() {
        return fetchOptionList("구매 요청 검색 타입 옵션 조회", "/scm-pp/mm/purchase-requisition-search-type-options");
    }

    @Override
    public ResponseEntity<List<ValueKeyOptionDto>> getPurchaseOrderSearchTypeOptions() {
        return fetchOptionList("발주서 검색 타입 옵션 조회", "/scm-pp/mm/purchase-order-search-type-options");
    }

    @Override
    public ResponseEntity<List<ValueKeyOptionDto>> getSupplierSearchTypeOptions() {
        return fetchOptionList("공급업체 검색 타입 옵션 조회", "/scm-pp/mm/supplier-search-type-options");
    }

    private ResponseEntity<List<ValueKeyOptionDto>> fetchOptionList(String operation, String path) {
        return fetchEntity(
            operation,
            uriBuilder -> uriBuilder.path(path).build(),
            VALUE_KEY_OPTIONS_TYPE
        );
    }

    private ResponseEntity<StatsResponseDto<StatsMetricsDto>> toStatsResponse(ResponseEntity<JsonNode> response) {
        return ResponseEntity.status(response.getStatusCode()).body(StatsResponseMapper.fromJson(response.getBody()));
    }

    private ResponseEntity<JsonNode> fetchJson(String operation, Function<UriBuilder, URI> uriFunction) {
        try {
            ResponseEntity<JsonNode> response = scmClient()
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

    private <T> ResponseEntity<T> fetchEntity(
        String operation,
        Function<UriBuilder, URI> uriFunction,
        ParameterizedTypeReference<T> typeReference
    ) {
        try {
            ResponseEntity<T> response = scmClient()
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

    private WebClient scmClient() {
        return webClientProvider.getWebClient(ApiClientKey.SCM_PP);
    }

    private int normalizeSize(int size) {
        return size > 0 ? size : 5;
    }

    private void requireUserId(String userId, String fieldName) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, fieldName + " is required");
        }
    }
}
