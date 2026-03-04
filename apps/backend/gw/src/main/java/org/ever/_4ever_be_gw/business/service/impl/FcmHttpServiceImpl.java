package org.ever._4ever_be_gw.business.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.fcm.response.FcmStatisticsDto;
import org.ever._4ever_be_gw.business.service.FcmHttpService;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowItemDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
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
public class FcmHttpServiceImpl implements FcmHttpService {

    private final WebClientProvider webClientProvider;

    @Override
    public ResponseEntity<ApiResponse<FcmStatisticsDto>> getFcmStatistics(String periods) {
        log.debug("재무관리 통계 조회 요청 - periods: {}", periods);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<FcmStatisticsDto> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/fcm/statistics");
                        if (periods != null && !periods.isBlank()) {
                            builder.queryParam("periods", periods);
                        }
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<FcmStatisticsDto>>() {})
                    .block();

            log.info("재무관리 통계 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            String errorBody = ex.getResponseBodyAsString();
            log.error("재무관리 통계 조회 실패 - Status: {}, Body: {}", ex.getStatusCode(), errorBody);
            return ResponseEntity.status(status).body(
                    ApiResponse.fail("재무관리 통계 조회 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("재무관리 통계 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("재무관리 통계 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getApInvoices(
            String company,String status, String startDate, String endDate, Integer page, Integer size) {
        log.debug("매입 전표 목록 조회 요청 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/fcm/statement/ap");
                        if (company != null) builder.queryParam("company", company);
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("매입 전표 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("매입 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("매입 전표 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("매입 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getApInvoicesBySupplierUserId(
            String supplierUserId, String status, String startDate, String endDate, Integer page, Integer size) {
        log.debug("공급사 사용자 ID로 매입 전표 목록 조회 요청 - supplierUserId: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                supplierUserId, startDate, endDate, page, size);

        try {

            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/fcm/invoice/ap/supplier/{supplierUserId}");
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build(supplierUserId);
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("공급사 사용자 ID로 매입 전표 목록 조회 성공 - supplierUserId: {}", supplierUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("공급사 매입 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("공급사 매입 전표 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("공급사 매입 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardSupplierInvoiceList(
            String userId,
            Integer size
    ) {
        log.debug("[DASHBOARD][FCM] 공급사 매출 전표(AR) 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("supplier userId is required", HttpStatus.BAD_REQUEST, null)
            );
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/fcm/dashboard/invoice/ap/supplier")
                            .queryParam("userId", userId)
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][FCM] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][FCM] 비즈니스 서버 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][FCM] 공급사 매출 전표 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "공급사 매출 전표 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 공급사 매출 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][FCM] 공급사 매출 전표 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 공급사 매출 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardCustomerInvoiceList(
            String userId,
            Integer size
    ) {
        log.debug("[DASHBOARD][FCM] 고객사 매입 전표(AP) 목록 요청 - userId: {}, size: {}", userId, size);

        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.fail("customer userId is required", HttpStatus.BAD_REQUEST, null)
            );
        }

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/fcm/dashboard/invoice/ar/customer")
                            .queryParam("userId", userId)
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][FCM] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][FCM] 비즈니스 서버 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][FCM] 고객사 매입 전표 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "고객사 매입 전표 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 고객사 매입 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][FCM] 고객사 매입 전표 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 고객사 매입 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardCompanyArList(String userId, Integer size) {
        log.debug("[DASHBOARD][FCM] 기업 매출 전표(AR) 목록 요청 - userId: {}, size: {}", userId, size);

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/fcm/dashboard/invoice/ar")
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][FCM] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][FCM] 비즈니스 서버 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][FCM] 기업 매출 전표 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "기업 매출 전표 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 기업 매출 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][FCM] 기업 매출 전표 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 기업 매출 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<List<DashboardWorkflowItemDto>>> getDashboardCompanyApList(String userId, Integer size) {
        log.debug("[DASHBOARD][FCM] 기업 매입 전표(AP) 목록 요청 - size: {}", size);

        final int pageSize = (size != null && size > 0) ? size : 5;

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<List<DashboardWorkflowItemDto>> body = businessClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/fcm/dashboard/invoice/ap")
                            .queryParam("size", pageSize)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<DashboardWorkflowItemDto>>>() {})
                    .block();

            if (body == null) {
                log.error("[ERROR][DASHBOARD][FCM] 비즈니스 서버 응답이 null");
                throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "비즈니스 서버 응답이 비어 있습니다.");
            }

            List<DashboardWorkflowItemDto> data = body.getData();
            if (data == null) {
                log.error("[ERROR][DASHBOARD][FCM] 비즈니스 서버 응답에 data 필드가 없음");
                throw new BusinessException(ErrorCode.EXTERNAL_API_ERROR, "비즈니스 서버 응답 형식이 올바르지 않습니다.");
            }

            log.info("[INFO][DASHBOARD][FCM] 기업 매입 전표 목록 조회 성공");
            return ResponseEntity.ok(ApiResponse.success(data, "기업 매입 전표 목록 조회 성공", HttpStatus.OK));
        } catch (WebClientResponseException ex) {
            return handleWebClientError("대시보드 기업 매입 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("[ERROR][DASHBOARD][FCM] 기업 매입 전표 목록 조회 중 에러 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("대시보드 기업 매입 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getArInvoices(
            String company,String status, String startDate, String endDate, Integer page, Integer size) {
        log.debug("AR 전표 목록 조회 요청 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                company, startDate, endDate, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/fcm/invoice/ar");
                        if (company != null) builder.queryParam("company", company);
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build();
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("AR 전표 목록 조회 성공");
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("AR 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("AR 전표 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("AR 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getArInvoicesByCustomerUserId(
            String customerUserId,String status, String startDate, String endDate, Integer page, Integer size) {
        log.debug("고객사 사용자 ID로 AR 전표 목록 조회 요청 - customerUserId: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                customerUserId, startDate, endDate, page, size);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri(uriBuilder -> {
                        var builder = uriBuilder.path("/fcm/invoice/ar/customer/{customerUserId}");
                        if (startDate != null) builder.queryParam("startDate", startDate);
                        if (endDate != null) builder.queryParam("endDate", endDate);
                        builder.queryParam("status", status);
                        builder.queryParam("page", page != null ? page : 0);
                        builder.queryParam("size", size != null ? size : 10);
                        return builder.build(customerUserId);
                    })
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("고객사 사용자 ID로 AR 전표 목록 조회 성공 - customerUserId: {}", customerUserId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("고객사 AR 전표 목록 조회", ex);
        } catch (Exception e) {
            log.error("고객사 AR 전표 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("고객사 AR 전표 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getApInvoiceDetail(String invoiceId) {
        log.debug("AP 전표 상세 조회 요청 - invoiceId: {}", invoiceId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/fcm/invoice/ap/{invoiceId}", invoiceId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("AP 전표 상세 조회 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("AP 전표 상세 조회", ex);
        } catch (Exception e) {
            log.error("AP 전표 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("AP 전표 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> getArInvoiceDetail(String invoiceId) {
        log.debug("AR 전표 상세 조회 요청 - invoiceId: {}", invoiceId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.get()
                    .uri("/fcm/invoice/ar/{invoiceId}", invoiceId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("AR 전표 상세 조회 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("AR 전표 상세 조회", ex);
        } catch (Exception e) {
            log.error("AR 전표 상세 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("AR 전표 상세 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> patchApInvoice(String invoiceId, Map<String, Object> requestBody) {
        log.debug("AP 전표 수정 요청 - invoiceId: {}, body: {}", invoiceId, requestBody);

        // AP 전표 수정 엔드포인트가 Business 서비스에 없으므로 mock 응답 반환
        log.warn("AP 전표 수정 엔드포인트가 Business 서비스에 구현되지 않았습니다.");
        return ResponseEntity.ok(
                ApiResponse.success(null, "매입 전표 수정이 완료되었습니다.", HttpStatus.OK)
        );
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> patchArInvoice(String invoiceId, Map<String, Object> requestBody) {
        log.debug("AR 전표 수정 요청 - invoiceId: {}, body: {}", invoiceId, requestBody);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            // LocalDate를 String으로 변환
            Map<String, Object> convertedBody = new java.util.HashMap<>(requestBody);
            if (convertedBody.containsKey("dueDate") && convertedBody.get("dueDate") instanceof java.time.LocalDate) {
                convertedBody.put("dueDate", convertedBody.get("dueDate").toString());
            }

            ApiResponse<Object> response = businessClient.patch()
                    .uri("/fcm/invoice/ar/{invoiceId}", invoiceId)
                    .bodyValue(convertedBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("AR 전표 수정 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("AR 전표 수정", ex);
        } catch (Exception e) {
            log.error("AR 전표 수정 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("AR 전표 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> completeReceivable(String invoiceId) {
        log.debug("미수 처리 완료 요청 - invoiceId: {}", invoiceId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/fcm/invoice/ar/{invoiceId}/receivable/complete", invoiceId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("미수 처리 완료 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("미수 처리 완료", ex);
        } catch (Exception e) {
            log.error("미수 처리 완료 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("미수 처리 완료 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> requestApReceivable(String invoiceId) {
        log.debug("매입 전표 미수 처리 요청 - invoiceId: {}", invoiceId);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("voucherId", invoiceId, "statusCode", "PENDING");

            ApiResponse<Object> response = businessClient.post()
                    .uri("/fcm/invoice/ap/receivable/request")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("매입 전표 미수 처리 요청 성공 - invoiceId: {}", invoiceId);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("매입 전표 미수 처리 요청", ex);
        } catch (Exception e) {
            log.error("매입 전표 미수 처리 요청 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("매입 전표 미수 처리 요청 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateArInvoicesResponsePending(java.util.List<String> invoiceIds) {
        log.debug("매출 전표 상태 일괄 변경 요청 - invoiceIds: {}", invoiceIds);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("invoiceIds", invoiceIds);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/fcm/invoice/ar/customer/response-pending")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("매출 전표 상태 일괄 변경 성공 - invoiceIds: {}", invoiceIds);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("매출 전표 상태 일괄 변경", ex);
        } catch (Exception e) {
            log.error("매출 전표 상태 일괄 변경 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("매출 전표 상태 일괄 변경 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<ApiResponse<Object>> updateApInvoicesResponsePending(java.util.List<String> invoiceIds) {
        log.debug("매입 전표 상태 일괄 변경 요청 - invoiceIds: {}", invoiceIds);

        try {
            WebClient businessClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

            Map<String, Object> requestBody = Map.of("invoiceIds", invoiceIds);

            ApiResponse<Object> response = businessClient.post()
                    .uri("/fcm/invoice/ap/supplier/response-pending")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<Object>>() {})
                    .block();

            log.info("매입 전표 상태 일괄 변경 성공 - invoiceIds: {}", invoiceIds);
            return ResponseEntity.ok(response);

        } catch (WebClientResponseException ex) {
            return handleWebClientError("매입 전표 상태 일괄 변경", ex);
        } catch (Exception e) {
            log.error("매입 전표 상태 일괄 변경 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.fail("매입 전표 상태 일괄 변경 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    /**
     * WebClient 오류를 처리하고 로깅하는 공통 메서드
     */
    private <T> ResponseEntity<ApiResponse<T>> handleWebClientError(String operation, WebClientResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorBody = ex.getResponseBodyAsString();

        log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);

        return ResponseEntity.status(status).body(
                ApiResponse.fail(operation + " 중 오류가 발생했습니다.", status, null)
        );
    }
}
