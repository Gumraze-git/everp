package org.ever._4ever_be_gw.business.service.impl;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.service.FcmHttpService;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.handler.ProblemDetailFactory;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class FcmHttpServiceImpl implements FcmHttpService {

    private final WebClientProvider webClientProvider;

    @Override
    public ResponseEntity<?> getFcmStatistics(String periods) {
        log.debug("재무관리 통계 조회 요청 - periods: {}", periods);
        return get(
            "재무관리 통계 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder.path("/fcm/statistics");
                if (periods != null && !periods.isBlank()) {
                    builder.queryParam("periods", periods);
                }
                return builder.build();
            }
        );
    }

    @Override
    public ResponseEntity<?> getApInvoices(
        String company,
        String status,
        String startDate,
        String endDate,
        Integer page,
        Integer size
    ) {
        log.debug("매입 전표 목록 조회 요청 - company: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
            company, status, startDate, endDate, page, size);
        return get(
            "매입 전표 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder.path("/fcm/statement/ap");
                if (company != null) {
                    builder.queryParam("company", company);
                }
                if (status != null) {
                    builder.queryParam("status", status);
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
            }
        );
    }

    @Override
    public ResponseEntity<?> getApInvoicesBySupplierUserId(
        String supplierUserId,
        String status,
        String startDate,
        String endDate,
        Integer page,
        Integer size
    ) {
        log.debug("공급사 사용자 ID로 매입 전표 목록 조회 요청 - supplierUserId: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
            supplierUserId, status, startDate, endDate, page, size);
        return get(
            "공급사 매입 전표 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder.path("/fcm/invoice/ap/supplier/{supplierUserId}");
                if (status != null) {
                    builder.queryParam("status", status);
                }
                if (startDate != null) {
                    builder.queryParam("startDate", startDate);
                }
                if (endDate != null) {
                    builder.queryParam("endDate", endDate);
                }
                builder.queryParam("page", page != null ? page : 0);
                builder.queryParam("size", size != null ? size : 10);
                return builder.build(supplierUserId);
            }
        );
    }

    @Override
    public ResponseEntity<?> getArInvoices(
        String company,
        String status,
        String startDate,
        String endDate,
        Integer page,
        Integer size
    ) {
        log.debug("매출 전표 목록 조회 요청 - company: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
            company, status, startDate, endDate, page, size);
        return get(
            "매출 전표 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder.path("/fcm/invoice/ar");
                if (company != null) {
                    builder.queryParam("company", company);
                }
                if (status != null) {
                    builder.queryParam("status", status);
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
            }
        );
    }

    @Override
    public ResponseEntity<?> getArInvoicesByCustomerUserId(
        String customerUserId,
        String status,
        String startDate,
        String endDate,
        Integer page,
        Integer size
    ) {
        log.debug("고객사 사용자 ID로 매출 전표 목록 조회 요청 - customerUserId: {}, status: {}, startDate: {}, endDate: {}, page: {}, size: {}",
            customerUserId, status, startDate, endDate, page, size);
        return get(
            "고객사 매출 전표 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder.path("/fcm/invoice/ar/customer/{customerUserId}");
                if (status != null) {
                    builder.queryParam("status", status);
                }
                if (startDate != null) {
                    builder.queryParam("startDate", startDate);
                }
                if (endDate != null) {
                    builder.queryParam("endDate", endDate);
                }
                builder.queryParam("page", page != null ? page : 0);
                builder.queryParam("size", size != null ? size : 10);
                return builder.build(customerUserId);
            }
        );
    }

    @Override
    public ResponseEntity<?> getApInvoiceDetail(String invoiceId) {
        log.debug("매입 전표 상세 조회 요청 - invoiceId: {}", invoiceId);
        return get("매입 전표 상세 조회", "/fcm/invoice/ap/{invoiceId}", invoiceId);
    }

    @Override
    public ResponseEntity<?> getArInvoiceDetail(String invoiceId) {
        log.debug("매출 전표 상세 조회 요청 - invoiceId: {}", invoiceId);
        return get("매출 전표 상세 조회", "/fcm/invoice/ar/{invoiceId}", invoiceId);
    }

    @Override
    public ResponseEntity<?> patchApInvoice(String invoiceId, Map<String, Object> requestBody) {
        log.debug("매입 전표 수정 요청 - invoiceId: {}, body: {}", invoiceId, requestBody);
        log.warn("매입 전표 수정 엔드포인트가 Business 서비스에 구현되지 않아 204 응답으로 처리합니다.");
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<?> patchArInvoice(String invoiceId, Map<String, Object> requestBody) {
        log.debug("매출 전표 수정 요청 - invoiceId: {}, body: {}", invoiceId, requestBody);

        Map<String, Object> convertedBody = new HashMap<>(requestBody);
        if (convertedBody.containsKey("dueDate") && convertedBody.get("dueDate") instanceof LocalDate dueDate) {
            convertedBody.put("dueDate", dueDate.toString());
        }

        return patch("매출 전표 수정", "/fcm/invoice/ar/{invoiceId}", convertedBody, invoiceId);
    }

    @Override
    public ResponseEntity<?> completeReceivable(String invoiceId) {
        log.debug("미수 처리 완료 요청 - invoiceId: {}", invoiceId);
        return post("미수 처리 완료", "/fcm/invoice/ar/{invoiceId}/receivable/complete", null, invoiceId);
    }

    @Override
    public ResponseEntity<?> completePayable(String invoiceId) {
        log.debug("미지급 처리 완료 요청 - invoiceId: {}", invoiceId);
        return post("미지급 처리 완료", "/fcm/invoice/ap/{invoiceId}/payable/complete", null, invoiceId);
    }

    @Override
    public ResponseEntity<?> requestApReceivable(String invoiceId) {
        log.debug("매입 전표 미수 처리 요청 - invoiceId: {}", invoiceId);
        Map<String, Object> requestBody = Map.of("voucherId", invoiceId, "statusCode", "PENDING");
        return post("매입 전표 미수 처리 요청", "/fcm/invoice/ap/receivable/request", requestBody);
    }

    @Override
    public ResponseEntity<?> updateArInvoicesResponsePending(List<String> invoiceIds) {
        log.debug("매출 전표 상태 일괄 변경 요청 - invoiceIds: {}", invoiceIds);
        return post(
            "매출 전표 상태 일괄 변경",
            "/fcm/invoice/ar/customer/response-pending",
            Map.of("invoiceIds", invoiceIds)
        );
    }

    @Override
    public ResponseEntity<?> updateApInvoicesResponsePending(List<String> invoiceIds) {
        log.debug("매입 전표 상태 일괄 변경 요청 - invoiceIds: {}", invoiceIds);
        return post(
            "매입 전표 상태 일괄 변경",
            "/fcm/invoice/ap/supplier/response-pending",
            Map.of("invoiceIds", invoiceIds)
        );
    }

    @Override
    public ResponseEntity<?> getSupplierTotalSales(String supplierUserId) {
        log.debug("공급사 총 매출 통계 조회 요청 - supplierUserId: {}", supplierUserId);
        return get("공급사 총 매출 통계 조회", "/fcm/statistics/supplier/{supplierUserId}/total-sales", supplierUserId);
    }

    @Override
    public ResponseEntity<?> getCustomerTotalPurchases(String customerUserId) {
        log.debug("고객사 총 매입 통계 조회 요청 - customerUserId: {}", customerUserId);
        return get("고객사 총 매입 통계 조회", "/fcm/statistics/customer/{customerUserId}/total-purchases", customerUserId);
    }

    @Override
    public ResponseEntity<?> getDashboardSupplierInvoiceList(String userId, Integer size) {
        log.debug("[DASHBOARD][FCM] 공급사 매출 전표 목록 조회 요청 - userId: {}, size: {}", userId, size);
        if (userId == null || userId.isBlank()) {
            return badRequest("supplier userId is required");
        }
        return get(
            "대시보드 공급사 매출 전표 목록 조회",
            uriBuilder -> uriBuilder
                .path("/fcm/dashboard/invoice/ap/supplier")
                .queryParam("userId", userId)
                .queryParam("size", normalizeSize(size))
                .build()
        );
    }

    @Override
    public ResponseEntity<?> getDashboardCustomerInvoiceList(String userId, Integer size) {
        log.debug("[DASHBOARD][FCM] 고객사 매입 전표 목록 조회 요청 - userId: {}, size: {}", userId, size);
        if (userId == null || userId.isBlank()) {
            return badRequest("customer userId is required");
        }
        return get(
            "대시보드 고객사 매입 전표 목록 조회",
            uriBuilder -> uriBuilder
                .path("/fcm/dashboard/invoice/ar/customer")
                .queryParam("userId", userId)
                .queryParam("size", normalizeSize(size))
                .build()
        );
    }

    @Override
    public ResponseEntity<?> getDashboardCompanyArList(String userId, Integer size) {
        log.debug("[DASHBOARD][FCM] 기업 매출 전표 목록 조회 요청 - size: {}", size);
        return get(
            "대시보드 기업 매출 전표 목록 조회",
            uriBuilder -> uriBuilder
                .path("/fcm/dashboard/invoice/ar")
                .queryParam("size", normalizeSize(size))
                .build()
        );
    }

    @Override
    public ResponseEntity<?> getDashboardCompanyApList(String userId, Integer size) {
        log.debug("[DASHBOARD][FCM] 기업 매입 전표 목록 조회 요청 - size: {}", size);
        return get(
            "대시보드 기업 매입 전표 목록 조회",
            uriBuilder -> uriBuilder
                .path("/fcm/dashboard/invoice/ap")
                .queryParam("size", normalizeSize(size))
                .build()
        );
    }

    private ResponseEntity<?> get(String operation, String path, Object... uriVariables) {
        return execute(operation, () -> businessClient().get().uri(path, uriVariables).retrieve().toEntity(Object.class).block());
    }

    private ResponseEntity<?> get(String operation, Function<UriBuilder, URI> uriFunction) {
        return execute(operation, () -> businessClient().get().uri(uriFunction).retrieve().toEntity(Object.class).block());
    }

    private ResponseEntity<?> post(String operation, String path, Object body, Object... uriVariables) {
        return execute(operation, () -> {
            WebClient.RequestBodySpec request = businessClient().post().uri(path, uriVariables);
            if (body != null) {
                return request.bodyValue(body).retrieve().toEntity(Object.class).block();
            }
            return request.retrieve().toEntity(Object.class).block();
        });
    }

    private ResponseEntity<?> patch(String operation, String path, Object body, Object... uriVariables) {
        return execute(operation, () -> businessClient().patch()
            .uri(path, uriVariables)
            .bodyValue(body)
            .retrieve()
            .toEntity(Object.class)
            .block());
    }

    private ResponseEntity<?> execute(String operation, RequestExecutor executor) {
        try {
            ResponseEntity<Object> response = executor.execute();
            return response != null ? response : ResponseEntity.noContent().build();
        } catch (WebClientResponseException ex) {
            return handleWebClientError(operation, ex);
        } catch (Exception e) {
            log.error("{} 중 예기치 않은 오류 발생", operation, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ProblemDetailFactory.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    operation + " 중 오류가 발생했습니다.",
                    operation + " 중 오류가 발생했습니다.",
                    null,
                    null,
                    ErrorCode.INTERNAL_SERVER_ERROR.getCode()
                )
            );
        }
    }

    private ResponseEntity<?> handleWebClientError(String operation, WebClientResponseException ex) {
        log.error("{} 실패 - Status: {}, Body: {}", operation, ex.getStatusCode(), ex.getResponseBodyAsString());
        return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode().value()))
            .body(ProblemDetailFactory.fromWebClientResponseException(ex, "business"));
    }

    private ResponseEntity<?> badRequest(String detail) {
        return ResponseEntity.badRequest().body(
            ProblemDetailFactory.badRequest(
                "요청 값이 올바르지 않습니다.",
                detail,
                null,
                null,
                ErrorCode.INVALID_INPUT_VALUE.getCode()
            )
        );
    }

    private int normalizeSize(Integer size) {
        return size != null && size > 0 ? size : 5;
    }

    private WebClient businessClient() {
        return webClientProvider.getWebClient(ApiClientKey.BUSINESS);
    }

    @FunctionalInterface
    private interface RequestExecutor {
        ResponseEntity<Object> execute();
    }
}
