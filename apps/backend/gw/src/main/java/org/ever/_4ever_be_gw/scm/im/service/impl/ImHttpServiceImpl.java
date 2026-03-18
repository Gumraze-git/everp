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
import org.ever._4ever_be_gw.config.restclient.RestClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.ever._4ever_be_gw.scm.im.dto.AddInventoryItemRequest;
import org.ever._4ever_be_gw.scm.im.dto.SalesOrderStatusChangeRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.StockTransferRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseCreateRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseUpdateRequestDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.ever._4ever_be_gw.scm.mm.dto.ItemInfoRequest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriBuilder;

@Service("imHttpService")
@RequiredArgsConstructor
@Slf4j
public class ImHttpServiceImpl implements ImHttpService {

    private static final ParameterizedTypeReference<List<DashboardWorkflowItemDto>> DASHBOARD_WORKFLOW_ITEMS_TYPE =
        new ParameterizedTypeReference<>() {};

    private final RestClientProvider restClientProvider;

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardInboundList(String userId, Integer size) {
        int pageSize = normalizeSize(size);
        return fetchEntity(
            ApiClientKey.SCM_PP,
            "대시보드 입고 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/iv/workflow-items/inbound")
                .queryParam("size", pageSize)
                .build(),
            DASHBOARD_WORKFLOW_ITEMS_TYPE
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardOutboundList(String userId, Integer size) {
        int pageSize = normalizeSize(size);
        return fetchEntity(
            ApiClientKey.SCM_PP,
            "대시보드 출고 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/iv/workflow-items/outbound")
                .queryParam("size", pageSize)
                .build(),
            DASHBOARD_WORKFLOW_ITEMS_TYPE
        );
    }

    @Override
    public ResponseEntity<Object> getInventoryItems(
        String type,
        String keyword,
        String statusCode,
        Integer page,
        Integer size
    ) {
        int pageNumber = normalizePage(page);
        int pageSize = normalizePageSize(size);
        String normalizedStatusCode = normalizeStatusCode(statusCode);
        return fetchObject(
            ApiClientKey.SCM_PP,
            "재고 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder
                    .path("/scm-pp/iv/inventory-items")
                    .queryParam("statusCode", normalizedStatusCode)
                    .queryParam("page", pageNumber)
                    .queryParam("size", pageSize);
                builder = addQueryParamIfPresent(builder, "type", type);
                builder = addQueryParamIfPresent(builder, "keyword", keyword);
                return builder.build();
            }
        );
    }

    @Override
    public ResponseEntity<Object> addInventoryItem(AddInventoryItemRequest request) {
        return postForObject(
            ApiClientKey.SCM_PP,
            "재고 추가",
            uriBuilder -> uriBuilder.path("/scm-pp/iv/items").build(),
            request
        );
    }

    @Override
    public ResponseEntity<Object> updateSafetyStock(String itemId, Integer safetyStock) {
        if (itemId == null || itemId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, "itemId is required");
        }
        if (safetyStock == null) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, "safetyStock is required");
        }
        return patchForObject(
            ApiClientKey.SCM_PP,
            "안전재고 수정",
            uriBuilder -> uriBuilder
                .path("/scm-pp/iv/items/{itemId}/safety-stock")
                .queryParam("safetyStock", safetyStock)
                .build(itemId)
        );
    }

    @Override
    public ResponseEntity<Object> getInventoryItemDetail(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, "itemId is required");
        }
        return fetchObject(
            ApiClientKey.SCM_PP,
            "재고 상세 조회",
            uriBuilder -> uriBuilder.path("/scm-pp/iv/items/{itemId}").build(itemId)
        );
    }

    @Override
    public ResponseEntity<Object> getShortageItems(String statusCode, Integer page, Integer size) {
        int pageNumber = normalizePage(page);
        int pageSize = normalizePageSize(size);
        return fetchObject(
            ApiClientKey.SCM_PP,
            "부족 재고 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder
                    .path("/scm-pp/iv/shortage")
                    .queryParam("page", pageNumber)
                    .queryParam("size", pageSize);
                builder = addQueryParamIfPresent(builder, "status", statusCode);
                return builder.build();
            }
        );
    }

    @Override
    public ResponseEntity<Object> getShortageItemsPreview() {
        return fetchObject(
            ApiClientKey.SCM_PP,
            "부족 재고 간단 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/iv/shortage/preview")
                .queryParam("page", 0)
                .queryParam("size", 5)
                .build()
        );
    }

    @Override
    public ResponseEntity<Object> getItemOptions() {
        return fetchObject(
            ApiClientKey.SCM_PP,
            "품목 옵션 조회",
            uriBuilder -> uriBuilder.path("/scm-pp/iv/items/toggle").build()
        );
    }

    @Override
    public ResponseEntity<Object> getStockTransfers() {
        return fetchObject(
            ApiClientKey.SCM_PP,
            "재고 이동 목록 조회",
            uriBuilder -> uriBuilder.path("/scm-pp/iv/stock-transfers").build()
        );
    }

    @Override
    public ResponseEntity<Object> createStockTransfer(StockTransferRequestDto request, String requesterId) {
        requireUserId(requesterId, "requesterId");
        return postForObject(
            ApiClientKey.SCM_PP,
            "재고 이동 생성",
            uriBuilder -> uriBuilder
                .path("/scm-pp/iv/stock-transfers")
                .queryParam("requesterId", requesterId)
                .build(),
            request
        );
    }

    @Override
    public ResponseEntity<Object> getWarehouses(Integer page, Integer size) {
        int pageNumber = normalizePage(page);
        int pageSize = size != null && size > 0 ? size : 20;
        return fetchObject(
            ApiClientKey.SCM_PP,
            "창고 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/iv/warehouses")
                .queryParam("page", pageNumber)
                .queryParam("size", pageSize)
                .build()
        );
    }

    @Override
    public ResponseEntity<Object> getWarehouseDetail(String warehouseId) {
        if (warehouseId == null || warehouseId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, "warehouseId is required");
        }
        return fetchObject(
            ApiClientKey.SCM_PP,
            "창고 상세 조회",
            uriBuilder -> uriBuilder.path("/scm-pp/iv/warehouses/{warehouseId}").build(warehouseId)
        );
    }

    @Override
    public ResponseEntity<Object> getWarehouseManagerOptions() {
        return fetchObject(
            ApiClientKey.BUSINESS,
            "창고 관리자 옵션 조회",
            uriBuilder -> uriBuilder.path("/hrm/departments/inventory/employees").build()
        );
    }

    @Override
    public ResponseEntity<Object> searchItems(ItemInfoRequest request) {
        return postForObject(
            ApiClientKey.SCM_PP,
            "재고성 구매요청 품목 조회",
            uriBuilder -> uriBuilder.path("/scm-pp/iv/items/info").build(),
            request
        );
    }

    @Override
    public ResponseEntity<Object> createWarehouse(WarehouseCreateRequestDto request) {
        return postForObject(
            ApiClientKey.SCM_PP,
            "창고 생성",
            uriBuilder -> uriBuilder.path("/scm-pp/iv/warehouses").build(),
            request
        );
    }

    @Override
    public ResponseEntity<Object> updateWarehouse(String warehouseId, WarehouseUpdateRequestDto request) {
        if (warehouseId == null || warehouseId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, "warehouseId is required");
        }
        return putForObject(
            ApiClientKey.SCM_PP,
            "창고 수정",
            uriBuilder -> uriBuilder.path("/scm-pp/iv/warehouses/{warehouseId}").build(warehouseId),
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
    public ResponseEntity<Object> getProductOptions() {
        return fetchObject(
            ApiClientKey.SCM_PP,
            "제품 옵션 조회",
            uriBuilder -> uriBuilder.path("/scm-pp/product-options").build()
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

    @Override
    public ResponseEntity<Object> getPurchaseOrders(
        String status,
        Integer page,
        Integer size,
        String startDate,
        String endDate
    ) {
        requireStatus(status, "purchase-order status");
        int pageNumber = normalizePage(page);
        int pageSize = normalizePageSize(size);

        return fetchObject(
            ApiClientKey.SCM_PP,
            "구매 발주 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder
                    .path("/scm-pp/purchase-orders")
                    .queryParam("status", status)
                    .queryParam("page", pageNumber)
                    .queryParam("size", pageSize);
                if (startDate != null && !startDate.isBlank()) {
                    builder.queryParam("startDate", startDate);
                }
                if (endDate != null && !endDate.isBlank()) {
                    builder.queryParam("endDate", endDate);
                }
                return builder.build();
            }
        );
    }

    @Override
    public ResponseEntity<Object> getSalesOrders(String status, Integer page, Integer size) {
        requireStatus(status, "sales-order status");
        int pageNumber = normalizePage(page);
        int pageSize = normalizePageSize(size);
        return fetchObject(
            ApiClientKey.SCM_PP,
            "판매 주문 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/sales-orders")
                .queryParam("status", status)
                .queryParam("page", pageNumber)
                .queryParam("size", pageSize)
                .build()
        );
    }

    @Override
    public ResponseEntity<Object> getSalesOrder(String salesOrderId) {
        if (salesOrderId == null || salesOrderId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, "salesOrderId is required");
        }
        return fetchObject(
            ApiClientKey.SCM_PP,
            "판매 주문 상세 조회",
            uriBuilder -> uriBuilder.path("/scm-pp/sales-orders/{salesOrderId}").build(salesOrderId)
        );
    }

    @Override
    public ResponseEntity<Void> createShipment(
        String salesOrderId,
        SalesOrderStatusChangeRequestDto requestDto,
        String requesterId
    ) {
        if (salesOrderId == null || salesOrderId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, "salesOrderId is required");
        }
        requireUserId(requesterId, "requesterId");
        try {
            ResponseEntity<Void> response = scmClient(ApiClientKey.SCM_PP)
                .post()
                .uri(uriBuilder -> uriBuilder
                    .path("/scm-pp/sales-orders/{salesOrderId}/shipments")
                    .queryParam("requesterId", requesterId)
                    .build(salesOrderId))
                .body(requestDto)
                .retrieve()
                .toBodilessEntity();
            if (response == null) {
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "출고 생성 응답이 비어 있습니다.");
            }
            return response;
        } catch (RestClientResponseException ex) {
            log.error("출고 생성 실패 - status: {}, body: {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("출고 생성 중 오류 발생", ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "출고 생성 중 오류가 발생했습니다.", ex);
        }
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
                .toEntity(JsonNode.class);
            return requireBody(operation, response);
        } catch (RestClientResponseException ex) {
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
                .toEntity(Object.class);
            return requireBody(operation, response);
        } catch (RestClientResponseException ex) {
            log.error("{} 실패 - status: {}, body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private ResponseEntity<Object> postForObject(
        ApiClientKey apiClientKey,
        String operation,
        Function<UriBuilder, URI> uriFunction,
        Object requestBody
    ) {
        try {
            ResponseEntity<Object> response = scmClient(apiClientKey)
                .post()
                .uri(uriFunction)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(Object.class);
            return requireBody(operation, response);
        } catch (RestClientResponseException ex) {
            log.error("{} 실패 - status: {}, body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private ResponseEntity<Object> patchForObject(
        ApiClientKey apiClientKey,
        String operation,
        Function<UriBuilder, URI> uriFunction
    ) {
        try {
            ResponseEntity<Object> response = scmClient(apiClientKey)
                .patch()
                .uri(uriFunction)
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(Object.class);
            return requireBody(operation, response);
        } catch (RestClientResponseException ex) {
            log.error("{} 실패 - status: {}, body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private ResponseEntity<Object> putForObject(
        ApiClientKey apiClientKey,
        String operation,
        Function<UriBuilder, URI> uriFunction,
        Object requestBody
    ) {
        try {
            ResponseEntity<Object> response = scmClient(apiClientKey)
                .put()
                .uri(uriFunction)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .toEntity(Object.class);
            return requireBody(operation, response);
        } catch (RestClientResponseException ex) {
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
                .toEntity(typeReference);
            return requireBody(operation, response);
        } catch (RestClientResponseException ex) {
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

    private RestClient scmClient(ApiClientKey apiClientKey) {
        return restClientProvider.getRestClient(apiClientKey);
    }

    private int normalizeSize(Integer size) {
        return size != null && size > 0 ? size : 5;
    }

    private int normalizePage(Integer page) {
        return page != null && page >= 0 ? page : 0;
    }

    private int normalizePageSize(Integer size) {
        return size != null && size > 0 ? size : 10;
    }

    private String normalizeStatusCode(String statusCode) {
        return statusCode != null && !statusCode.isBlank() ? statusCode : "ALL";
    }

    private void requireUserId(String userId, String fieldName) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, fieldName + " is required");
        }
    }

    private void requireStatus(String status, String fieldName) {
        if (status == null || status.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, fieldName + " is required");
        }
    }

    private UriBuilder addQueryParamIfPresent(UriBuilder builder, String name, Object value) {
        if (value == null) {
            return builder;
        }
        if (value instanceof String stringValue && stringValue.isBlank()) {
            return builder;
        }
        return builder.queryParam(name, value);
    }
}
