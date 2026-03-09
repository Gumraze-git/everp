package org.ever._4ever_be_gw.business.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.analytics.SalesAnalyticsResponseDto;
import org.ever._4ever_be_gw.business.service.SdHttpService;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.restclient.RestClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SdHttpServiceImpl implements SdHttpService {

    private final RestClientProvider restClientProvider;

    @Override
    public ResponseEntity<Object> getDashboardStatistics() {
        log.debug("대시보드 통계 조회 요청");
        return toEntity(
            "대시보드 통계 조회",
            businessClient().get().uri("/sd/metrics"),
            Object.class
        );
    }

    @Override
    public ResponseEntity<SalesAnalyticsResponseDto> getSalesAnalytics(String startDate, String endDate) {
        log.debug("매출 분석 통계 조회 요청 - startDate: {}, endDate: {}", startDate, endDate);
        return toEntity(
            "매출 분석 통계 조회",
            businessClient().get().uri(uriBuilder -> {
                var builder = uriBuilder.path("/sd/analytics/sales");
                if (startDate != null) {
                    builder.queryParam("startDate", startDate);
                }
                if (endDate != null) {
                    builder.queryParam("endDate", endDate);
                }
                return builder.build();
            }),
            SalesAnalyticsResponseDto.class
        );
    }

    @Override
    public ResponseEntity<Object> getCustomerList(
        String status,
        String type,
        String search,
        Integer page,
        Integer size
    ) {
        log.debug(
            "고객사 목록 조회 요청 - status: {}, type: {}, search: {}, page: {}, size: {}",
            status,
            type,
            search,
            page,
            size
        );
        return toEntity(
            "고객사 목록 조회",
            businessClient().get().uri(uriBuilder -> {
                var builder = uriBuilder.path("/sd/customers");
                if (status != null) {
                    builder.queryParam("status", status);
                }
                if (type != null) {
                    builder.queryParam("type", type);
                }
                if (search != null) {
                    builder.queryParam("search", search);
                }
                builder.queryParam("page", page != null ? page : 0);
                builder.queryParam("size", size != null ? size : 10);
                return builder.build();
            }),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> createCustomer(Map<String, Object> requestBody) {
        log.debug("고객사 등록 요청 - body: {}", requestBody);
        return toEntity(
            "고객사 등록",
            businessClient().post().uri("/sd/customers").body(requestBody),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> getCustomerDetail(String customerId) {
        log.debug("고객사 상세 조회 요청 - customerId: {}", customerId);
        return toEntity(
            "고객사 상세 조회",
            businessClient().get().uri("/sd/customers/{customerId}", customerId),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> updateCustomer(String customerId, Map<String, Object> requestBody) {
        log.debug("고객사 정보 수정 요청 - customerId: {}, body: {}", customerId, requestBody);
        return toEntity(
            "고객사 정보 수정",
            businessClient().patch().uri("/sd/customers/{customerId}", customerId).body(requestBody),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> deleteCustomer(String customerId) {
        log.debug("고객사 삭제 요청 - customerId: {}", customerId);
        return toEntity(
            "고객사 삭제",
            businessClient().delete().uri("/sd/customers/{customerId}", customerId),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> getOrderList(
        String customerId,
        String employeeId,
        String startDate,
        String endDate,
        String search,
        String type,
        String status,
        Integer page,
        Integer size
    ) {
        log.debug(
            "주문 목록 조회 요청 - customerId: {}, employeeId: {}, startDate: {}, endDate: {}, search: {}, type: {}, status: {}, page: {}, size: {}",
            customerId,
            employeeId,
            startDate,
            endDate,
            search,
            type,
            status,
            page,
            size
        );
        return toEntity(
            "주문 목록 조회",
            businessClient().get().uri(uriBuilder -> {
                var builder = uriBuilder.path("/sd/orders");
                if (customerId != null) {
                    builder.queryParam("customerId", customerId);
                }
                if (employeeId != null) {
                    builder.queryParam("employeeId", employeeId);
                }
                if (startDate != null) {
                    builder.queryParam("startDate", startDate);
                }
                if (endDate != null) {
                    builder.queryParam("endDate", endDate);
                }
                if (search != null) {
                    builder.queryParam("search", search);
                }
                if (type != null) {
                    builder.queryParam("type", type);
                }
                if (status != null) {
                    builder.queryParam("status", status);
                }
                builder.queryParam("page", page != null ? page : 0);
                builder.queryParam("size", size != null ? size : 10);
                return builder.build();
            }),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> getOrderDetail(String salesOrderId) {
        log.debug("주문서 상세 조회 요청 - salesOrderId: {}", salesOrderId);
        return toEntity(
            "주문서 상세 조회",
            businessClient().get().uri("/sd/orders/{salesOrderId}", salesOrderId),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> getQuotationList(
        String customerId,
        String startDate,
        String endDate,
        String status,
        String type,
        String search,
        String sort,
        Integer page,
        Integer size
    ) {
        log.debug(
            "견적 목록 조회 요청 - customerId: {}, startDate: {}, endDate: {}, status: {}, type: {}, search: {}, sort: {}, page: {}, size: {}",
            customerId,
            startDate,
            endDate,
            status,
            type,
            search,
            sort,
            page,
            size
        );
        return toEntity(
            "견적 목록 조회",
            businessClient().get().uri(uriBuilder -> {
                var builder = uriBuilder.path("/sd/quotations");
                if (customerId != null) {
                    builder.queryParam("customerId", customerId);
                }
                if (startDate != null) {
                    builder.queryParam("startDate", startDate);
                }
                if (endDate != null) {
                    builder.queryParam("endDate", endDate);
                }
                if (status != null) {
                    builder.queryParam("status", status);
                }
                if (type != null) {
                    builder.queryParam("type", type);
                }
                if (search != null) {
                    builder.queryParam("search", search);
                }
                if (sort != null) {
                    builder.queryParam("sort", sort);
                }
                builder.queryParam("page", page != null ? page : 0);
                builder.queryParam("size", size != null ? size : 10);
                return builder.build();
            }),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> getQuotationDetail(String quotationId) {
        log.debug("견적 상세 조회 요청 - quotationId: {}", quotationId);
        return toEntity(
            "견적 상세 조회",
            businessClient().get().uri("/sd/quotations/{quotationId}", quotationId),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> createQuotation(Map<String, Object> requestBody) {
        log.debug("견적서 생성 요청 - body: {}", requestBody);
        Map<String, Object> enrichedBody = new LinkedHashMap<>(requestBody);
        return toEntity(
            "견적서 생성",
            businessClient().post()
                .uri("/sd/quotations")
                .contentType(MediaType.APPLICATION_JSON)
                .body(enrichedBody),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> approveQuotation(String quotationId, Map<String, Object> requestBody) {
        log.debug("견적서 승인 및 주문 생성 요청 - quotationId: {}, body: {}", quotationId, requestBody);

        Map<String, Object> enrichedBody = new LinkedHashMap<>();
        if (requestBody != null) {
            enrichedBody.putAll(requestBody);
        }
        if (!enrichedBody.containsKey("employeeId")) {
            enrichedBody.put("employeeId", "019a293e-163d-7f6f-9689-16381fba05a7");
            log.info("임시 employeeId 추가: {}", enrichedBody.get("employeeId"));
        }

        return toEntity(
            "견적서 승인 및 주문 생성",
            businessClient().post()
                .uri("/sd/quotations/{quotationId}/orders", quotationId)
                .body(enrichedBody),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> confirmQuotation(String quotationId) {
        log.debug("견적서 검토 확정 요청 - quotationId: {}", quotationId);
        return toEntity(
            "견적서 검토 확정",
            businessClient().post().uri("/sd/quotations/{quotationId}/reviews", quotationId),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> rejectQuotation(String quotationId, Map<String, Object> requestBody) {
        log.debug("견적서 거부 요청 - quotationId: {}, body: {}", quotationId, requestBody);
        return toEntity(
            "견적서 거부",
            businessClient().post().uri("/sd/quotations/{quotationId}/rejections", quotationId).body(requestBody),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> checkInventory(Map<String, Object> requestBody) {
        log.debug("재고 확인 요청 - body: {}", requestBody);
        return toEntity(
            "재고 확인",
            businessClient().post().uri("/sd/inventory-checks").body(requestBody),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> getQuotationOptions() {
        log.debug("견적 옵션 조회 요청");
        return toEntity(
            "견적 옵션 조회",
            businessClient().get().uri("/sd/quotation-options"),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> getQuotationCountByCustomerUserId(String customerUserId) {
        log.debug("고객사 견적 건수 조회 요청 - customerUserId: {}", customerUserId);
        return toEntity(
            "고객사 견적 건수 조회",
            businessClient().get().uri("/sd/customer-users/{customerUserId}/metrics/quotation-counts", customerUserId),
            Object.class
        );
    }

    @Override
    public ResponseEntity<Object> getScmQuotationList(
        String statusCode,
        String availableStatus,
        String startDate,
        String endDate,
        Integer page,
        Integer size
    ) {
        log.debug(
            "SCM 견적 목록 조회 요청 - statusCode: {}, availableStatus: {}, startDate: {}, endDate: {}, page: {}, size: {}",
            statusCode,
            availableStatus,
            startDate,
            endDate,
            page,
            size
        );
        return toEntity(
            "SCM 견적 목록 조회",
            businessClient().get().uri(uriBuilder -> {
                var builder = uriBuilder.path("/sd/scm/quotations");
                if (statusCode != null) {
                    builder.queryParam("statusCode", statusCode);
                }
                if (availableStatus != null) {
                    builder.queryParam("availableStatus", availableStatus);
                }
                if (startDate != null) {
                    builder.queryParam("startDate", startDate);
                }
                if (endDate != null) {
                    builder.queryParam("endDate", endDate);
                }
                builder.queryParam("page", page != null ? page : 0);
                builder.queryParam("size", size != null ? size : 10);
                return builder.build();
            }),
            Object.class
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardSupplierOrderList(String userId, int size) {
        log.debug("[DASHBOARD][SD] 공급사 주문서(SO) 목록 조회 - userId: {}, size: {}", userId, size);
        requireUserId(userId, "supplier userId");
        return toEntity(
            "대시보드 공급사 주문서 목록 조회",
            businessClient().get().uri(uriBuilder -> uriBuilder
                .path("/sd/supplier-users/{userId}/workflow-items/quotations")
                .queryParam("size", size > 0 ? size : 5)
                .build(userId)),
            new ParameterizedTypeReference<List<DashboardWorkflowItemDto>>() {}
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardCustomerQuotationList(String userId, int size) {
        log.debug("[DASHBOARD][SD] 고객사 견적서(QT) 목록 조회 - userId: {}, size: {}", userId, size);
        requireUserId(userId, "customer userId");
        return toEntity(
            "대시보드 고객사 견적서 목록 조회",
            businessClient().get().uri(uriBuilder -> uriBuilder
                .path("/sd/customer-users/{userId}/workflow-items/quotations")
                .queryParam("size", size > 0 ? size : 5)
                .build(userId)),
            new ParameterizedTypeReference<List<DashboardWorkflowItemDto>>() {}
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardInternalQuotationList(int size) {
        log.debug("[DASHBOARD][SD] 내부 사용자 견적(QT) 목록 조회 - size: {}", size);
        return toEntity(
            "대시보드 내부 견적서 목록 조회",
            businessClient().get().uri(uriBuilder -> uriBuilder
                .path("/sd/workflow-items/quotations")
                .queryParam("size", size > 0 ? size : 5)
                .build()),
            new ParameterizedTypeReference<List<DashboardWorkflowItemDto>>() {}
        );
    }

    @Override
    public ResponseEntity<List<DashboardWorkflowItemDto>> getDashboardInternalOrderList(int size) {
        log.debug("[DASHBOARD][SD] 내부 주문서(SO) 목록 조회 - size: {}", size);
        return toEntity(
            "대시보드 내부 주문서 목록 조회",
            businessClient().get().uri(uriBuilder -> uriBuilder
                .path("/sd/workflow-items/orders")
                .queryParam("size", size > 0 ? size : 5)
                .build()),
            new ParameterizedTypeReference<List<DashboardWorkflowItemDto>>() {}
        );
    }

    private RestClient businessClient() {
        return restClientProvider.getRestClient(ApiClientKey.BUSINESS);
    }

    private void requireUserId(String userId, String label) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.MISSING_INPUT_VALUE, label + " is required");
        }
    }

    private <T> ResponseEntity<T> toEntity(
        String operation,
        RequestHeadersSpec<?> requestSpec,
        Class<T> responseType
    ) {
        try {
            return requireEntity(operation, requestSpec.retrieve().toEntity(responseType));
        } catch (RestClientResponseException ex) {
            log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 예기치 않은 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private <T> ResponseEntity<T> toEntity(
        String operation,
        RequestHeadersSpec<?> requestSpec,
        ParameterizedTypeReference<T> responseType
    ) {
        try {
            return requireEntity(operation, requestSpec.retrieve().toEntity(responseType));
        } catch (RestClientResponseException ex) {
            log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("{} 중 예기치 않은 오류 발생", operation, ex);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 중 오류가 발생했습니다.", ex);
        }
    }

    private <T> ResponseEntity<T> requireEntity(String operation, ResponseEntity<T> response) {
        if (response == null) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, operation + " 응답이 비어 있습니다.");
        }
        return response;
    }
}
