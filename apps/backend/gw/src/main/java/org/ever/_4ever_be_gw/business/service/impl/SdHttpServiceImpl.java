package org.ever._4ever_be_gw.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.analytics.SalesAnalyticsResponseDto;
import org.ever._4ever_be_gw.business.service.SdHttpService;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SdHttpServiceImpl implements SdHttpService {

    private final WebClientProvider webClientProvider;

    // ==================== Statistics ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getDashboardStatistics() {
        log.debug("대시보드 통계 조회 요청");

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/sd/dashboard/statistics")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("대시보드 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 통계 조회", ex);
        } catch (Exception e) {
            log.error("대시보드 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<SalesAnalyticsResponseDto>> getSalesAnalytics(String startDate, String endDate) {
        log.debug("매출 분석 통계 조회 요청 - startDate: {}, endDate: {}", startDate, endDate);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<SalesAnalyticsResponseDto> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/sd/analytics/sales");
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<SalesAnalyticsResponseDto>>() {})
                    .block();

            log.info("매출 분석 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            String errorBody = ex.getResponseBodyAsString();
            log.error("매출 분석 통계 조회 실패 - Status: {}, Body: {}", ex.getStatusCode(), errorBody);
            return ResponseEntity.status(status).body(
                    ApiResponse.fail("매출 분석 통계 조회 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("매출 분석 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("매출 분석 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Customers ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getCustomerList(
            String status, String type, String search, Integer page, Integer size) {
        log.debug("고객사 목록 조회 요청 - status: {}, type: {}, search: {}, page: {}, size: {}",
                status, type, search, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/sd/customers");
                        if (status != null) builder.queryParam("status", status);
                        if (type != null) builder.queryParam("type", type);
                        if (search != null) builder.queryParam("search", search);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 목록 조회", ex);
        } catch (Exception e) {
            log.error("고객사 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createCustomer(Map<String, Object> requestBody) {
        log.debug("고객사 등록 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/customers")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 등록 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 등록", ex);
        } catch (Exception e) {
            log.error("고객사 등록 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getCustomerDetail(String customerId) {
        log.debug("고객사 상세 조회 요청 - customerId: {}", customerId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/sd/customers/{customerId}", customerId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 상세 조회 성공 - customerId: {}", customerId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 상세 조회", ex);
        } catch (Exception e) {
            log.error("고객사 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateCustomer(String customerId, Map<String, Object> requestBody) {
        log.debug("고객사 정보 수정 요청 - customerId: {}, body: {}", customerId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/sd/customers/{customerId}", customerId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 정보 수정 성공 - customerId: {}", customerId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 정보 수정", ex);
        } catch (Exception e) {
            log.error("고객사 정보 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 정보 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> deleteCustomer(String customerId) {
        log.debug("고객사 삭제 요청 - customerId: {}", customerId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.delete()
                    .uri("/sd/customers/{customerId}", customerId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 삭제 성공 - customerId: {}", customerId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 삭제", ex);
        } catch (Exception e) {
            log.error("고객사 삭제 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Orders ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getOrderList(
            String customerId, String employeeId, String startDate, String endDate, String search, String type, String status, Integer page, Integer size) {
        log.debug("주문 목록 조회 요청 - customerId: {}, employeeId: {}, startDate: {}, endDate: {}, search: {}, type: {}, status: {}, page: {}, size: {}",
                customerId, employeeId, startDate, endDate, search, type, status, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/sd/orders");
                        if (customerId != null) builder.queryParam("customerId", customerId);
                        if (employeeId != null) builder.queryParam("employeeId", employeeId);
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        if (search != null) builder.queryParam("search", search);
                        if (type != null) builder.queryParam("type", type);
                        if (status != null) builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("주문 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("주문 목록 조회", ex);
        } catch (Exception e) {
            log.error("주문 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("주문 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getOrderDetail(String salesOrderId) {
        log.debug("주문서 상세 조회 요청 - salesOrderId: {}", salesOrderId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/sd/orders/{salesOrderId}", salesOrderId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("주문서 상세 조회 성공 - salesOrderId: {}", salesOrderId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("주문서 상세 조회", ex);
        } catch (Exception e) {
            log.error("주문서 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("주문서 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    // ==================== Quotations ====================

    @Override
    public ResponseEntity<ApiResponse<Object>> getQuotationList(
            String customerId, String startDate, String endDate, String status, String type, String search, String sort, Integer page, Integer size) {
        log.debug("견적 목록 조회 요청 - customerId: {}, startDate: {}, endDate: {}, status: {}, type: {}, search: {}, sort: {}, page: {}, size: {}",
                customerId, startDate, endDate, status, type, search, sort, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/sd/quotations");
                        if (customerId != null) builder.queryParam("customerId", customerId);
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        if (status != null) builder.queryParam("status", status);
                        if (type != null) builder.queryParam("type", type);
                        if (search != null) builder.queryParam("search", search);
                        if (sort != null) builder.queryParam("sort", sort);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적 목록 조회", ex);
        } catch (Exception e) {
            log.error("견적 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getQuotationDetail(String quotationId) {
        log.debug("견적 상세 조회 요청 - quotationId: {}", quotationId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/sd/quotations/{quotationId}", quotationId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적 상세 조회 성공 - quotationId: {}", quotationId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적 상세 조회", ex);
        } catch (Exception e) {
            log.error("견적 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> createQuotation(Map<String, Object> requestBody) {
        log.debug("견적서 생성 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            // 임시로 하드코딩된 userId 사용 (Business DB 실제 목 데이터)
            Map<String, Object> enrichedBody = new LinkedHashMap<>(requestBody);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(enrichedBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적서 생성 성공");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적서 생성", ex);
        } catch (Exception e) {
            log.error("견적서 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적서 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> approveQuotation(String quotationId, Map<String, Object> requestBody) {
        log.debug("견적서 승인 및 주문 생성 요청 - quotationId: {}, body: {}", quotationId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            // TODO: JWT 구현 후 토큰에서 employeeId 추출
            // 임시로 하드코딩된 employeeId 사용 (Business DB 실제 목 데이터)
            Map<String, Object> enrichedBody = new LinkedHashMap<>();
            if (requestBody != null) {
                enrichedBody.putAll(requestBody);
            }
            if (!enrichedBody.containsKey("employeeId")) {
                enrichedBody.put("employeeId", "019a293e-163d-7f6f-9689-16381fba05a7"); // 임시: Business DB의 실제 employee ID (internel1, EMP-001)
                log.info("임시 employeeId 추가: {}", enrichedBody.get("employeeId"));
            }

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations/{quotationId}/approve-order", quotationId)
                    .bodyValue(enrichedBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적서 승인 및 주문 생성 성공 - quotationId: {}", quotationId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적서 승인 및 주문 생성", ex);
        } catch (Exception e) {
            log.error("견적서 승인 및 주문 생성 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적서 승인 및 주문 생성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> confirmQuotation(Map<String, Object> requestBody) {
        log.debug("견적서 검토 확정 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations/confirm")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적서 검토 확정 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적서 검토 확정", ex);
        } catch (Exception e) {
            log.error("견적서 검토 확정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적서 검토 확정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> rejectQuotation(String quotationId, Map<String, Object> requestBody) {
        log.debug("견적서 거부 요청 - quotationId: {}, body: {}", quotationId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations/{quotationId}/rejected", quotationId)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("견적서 거부 성공 - quotationId: {}", quotationId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("견적서 거부", ex);
        } catch (Exception e) {
            log.error("견적서 거부 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("견적서 거부 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> checkInventory(Map<String, Object> requestBody) {
        log.debug("재고 확인 요청 - body: {}", requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/sd/quotations/inventory/check")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("재고 확인 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("재고 확인", ex);
        } catch (Exception e) {
            log.error("재고 확인 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("재고 확인 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardSupplierOrderList(
            String userId,
            int size
    ) {
        log.debug("[DASHBOARD][SD] 공급사 주문서(SO) 목록 조회 - userId: {}, size: {}", userId, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            log.info("[INFO][SD] 공급사의 주문서 목록 조회 시작, userId: {}, size: {}", userId, size);
            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/sd/dashboard/quotation/supplier")
                            .queryParam("userId", userId)
                            .queryParam("size", size > 0 ? size : 5)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            // 서버 응답에 따른 에러
            if (body == null) {
                log.error("[ERROR][DASHBOARD][SD] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][SD] 비즈니스 서버 응답에 data 필드가 존재하지 않음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][SD] 공급사 발주서(Quotation) 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "공급사 주문서 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 공급사 주문서 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][SD] 공급사 주문서 목록 조회 중 에러 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 공급사 주문서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardCustomerQuotationList(String userId, int size) {
        log.debug("[DASHBOARD][SD] 고객사 견적서(QT) 목록 조회 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("customer userId is required", HttpStatus.BAD_REQUEST, null)
            );
        }

        final int pageSize = size > 0 ? size : 5;

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/sd/dashboard/quotation/customer")
                            .queryParam("userId", userId)
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][SD] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][SD] 비즈니스 서버 응답에 data 필드가 존재하지 않음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][SD] 고객사 견적서 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "고객사 견적서 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 고객사 견적서 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][SD] 고객사 견적서 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 고객사 견적서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardInternalQuotationList(int size) {
        log.debug("[DASHBOARD][SD] 내부 사용자 견적(QT) 목록 조회 - size: {}", size);

        final int pageSize = size > 0 ? size : 5;

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/sd/dashboard/quotation/mm")
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][SD] 내부 견적 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][SD] 내부 견적 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][SD] 내부 견적서 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "내부 견적서 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 내부 견적서 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][SD] 내부 견적서 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 내부 견적서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardInternalOrderList(int size) {
        log.debug("[DASHBOARD][SD] 내부 주문서(SO) 목록 조회 - size: {}", size);

        final int pageSize = size > 0 ? size : 5;

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/sd/dashboard/orders/mm")
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][SD] 내부 주문 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][SD] 내부 주문 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][SD] 내부 주문서 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "내부 주문서 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 내부 주문서 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][SD] 내부 주문서 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 내부 주문서 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    /**
     * WebClient 오류를 처리하고 로깅하는 공통 메서드
     */
    private <T> ResponseEntity<ApiResponse<T>> handleWebClientError(String operation, WebClientResponseException ex) {
        var sc = ex.getStatusCode();                // HttpStatusCode
        var http = HttpStatus.valueOf(sc.value());  // ApiResponse.fail 에 HttpStatus가 필요하면 변환

        String errorBody = ex.getResponseBodyAsString();
        log.error("[WEBCLIENT][ERROR] {} 실패 - status={}, body={}", operation, sc, errorBody, ex);

        return ResponseEntity.status(sc).body(
                ApiResponse.fail(operation + " 중 오류가 발생했습니다.", http, null)
        );
    }
}
