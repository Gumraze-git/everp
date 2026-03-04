package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.InvoiceUpdateRequestDto;
import org.ever._4ever_be_gw.business.dto.fcm.response.FcmStatisticsDto;
import org.ever._4ever_be_gw.business.service.FcmHttpService;
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.scm.mm.dto.ItemInfoRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/business/fcm")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "재무관리(FCM)", description = "재무 관리 API")
public class FcmController {

	private final FcmHttpService fcmHttpService;
    private final WebClientProvider webClientProvider;

	// ==================== 재무 관리 통계 ====================

	@GetMapping("/statictics")
	@Operation(
		summary = "FCM 통계 조회",
		description = "기간별 재무 관리 통계를 조회합니다."
	)
	public ResponseEntity<ApiResponse<FcmStatisticsDto>> getStatistics(
		@Parameter(description = "조회 기간 목록(콤마 구분)")
		@RequestParam(name = "periods", required = false) String periods
	) {
		log.info("FCM 통계 조회 API 호출 - periods: {}", periods);
		return fcmHttpService.getFcmStatistics(periods);
	}

	// ==================== 전표 목록 조회 (AP: 매입, AR: 매출) ====================

	@GetMapping("/invoice/ap")
	@Operation(
		summary = "매입 전표 목록 조회",
		description = "매입(AP) 전표 목록을 조회합니다. JWT 토큰이 있고 userType이 SUPPLIER인 경우 해당 공급사의 매입(AP) 전표를 조회합니다."
	)
	public ResponseEntity<ApiResponse<Object>> getApInvoices(
		@AuthenticationPrincipal EverUserPrincipal principal,
		@Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
		@Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
		@Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
        @RequestParam(required = false) String status,
		@Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
		@Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
	) {
        // JWT 토큰이 있고 userType이 CUSTOMER인 경우, 해당 고객사의 매출(AR) 전표 조회
        if (principal != null && "CUSTOMER".equals(principal.getUserType())) {
            String customerUserId = principal.getUserId();
            log.info("CUSTOMER 사용자의 매출 전표 목록 조회 API 호출 - customerUserId: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                    customerUserId, startDate, endDate, page, size);
            return fcmHttpService.getArInvoicesByCustomerUserId(customerUserId, status, startDate, endDate, page, size);
        }

		// JWT 토큰이 없거나 EMPLOYEE인 경우, 매입(AP) 전표 조회
		log.info("매입 전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
				company, startDate, endDate, page, size);
		return fcmHttpService.getApInvoices(company, status, startDate, endDate, page, size);
	}

    @GetMapping("/invoice/ar")
	@Operation(
		summary = "매출 전표 목록 조회",
		description = "매출(AR) 전표 목록을 조회합니다. JWT 토큰이 있고 userType이 CUSTOMER인 경우 해당 고객사의 매출(AR) 전표를 조회합니다."
	)
	public ResponseEntity<ApiResponse<Object>> getArInvoices(
		@AuthenticationPrincipal EverUserPrincipal principal,
		@Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
		@Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
		@Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
		@RequestParam(required = false) String status,
        @Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
		@Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
	) {

        // JWT 토큰이 있고 userType이 SUPPLIER인 경우, 해당 공급사의 매입(AP) 전표 조회
        if (principal != null && "SUPPLIER".equals(principal.getUserType())) {
            String supplierUserId = principal.getUserId();
            log.info("SUPPLIER 사용자의 매입 전표 목록 조회 API 호출 - supplierUserId: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                    supplierUserId, startDate, endDate, page, size);
            return fcmHttpService.getApInvoicesBySupplierUserId(supplierUserId, status, startDate, endDate, page, size);
        }


        // JWT 토큰이 없거나 EMPLOYEE인 경우, 매출(AR) 전표 조회
		log.info("매출 전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
				company, startDate, endDate, page, size);
		return fcmHttpService.getArInvoices(company, status, startDate, endDate, page, size);
	}

	// ==================== 전표 상세 조회 ====================

    @GetMapping("/invoice/ap/{invoiceId}")
	@Operation(
		summary = "매입 전표 상세 조회",
		description = "매입(AP) 전표 상세 정보를 조회합니다."
	)
	public ResponseEntity<ApiResponse<Object>> getApInvoiceDetail(
		@Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId
	) {
		log.info("매입 전표 상세 조회 API 호출 - invoiceId: {}", invoiceId);
		return fcmHttpService.getApInvoiceDetail(invoiceId);
	}

    @GetMapping("/invoice/ar/{invoiceId}")
	@Operation(
		summary = "매출 전표 상세 조회",
		description = "매출(AR) 전표 상세 정보를 조회합니다."
	)
	public ResponseEntity<ApiResponse<Object>> getArInvoiceDetail(
		@Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId
	) {
		log.info("매출 전표 상세 조회 API 호출 - invoiceId: {}", invoiceId);
		return fcmHttpService.getArInvoiceDetail(invoiceId);
	}

	// ==================== 전표 수정 ====================

    @PatchMapping("/invoice/ap/{invoiceId}")
    @Operation(summary = "매입 전표 수정", description = "매입(AP) 전표를 수정합니다.")
    public ResponseEntity<ApiResponse<Object>> patchApInvoice(
        @Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    ) {
        log.info("매입 전표 수정 API 호출 - invoiceId: {}, request: {}", invoiceId, request);

        Map<String, Object> requestBody = new HashMap<>();
        if (request.getStatus() != null) requestBody.put("status", request.getStatus());
        if (request.getDueDate() != null) requestBody.put("dueDate", request.getDueDate());
        if (request.getMemo() != null) requestBody.put("memo", request.getMemo());

        return fcmHttpService.patchApInvoice(invoiceId, requestBody);
    }

    @PatchMapping("/invoice/ar/{invoiceId}")
    @Operation(summary = "매출 전표 수정", description = "매출(AR) 전표를 수정합니다.")
    public ResponseEntity<ApiResponse<Object>> patchArInvoice(
        @Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    ) {
        log.info("매출 전표 수정 API 호출 - invoiceId: {}, request: {}", invoiceId, request);

        Map<String, Object> requestBody = new HashMap<>();
        if (request.getStatus() != null) requestBody.put("status", request.getStatus());
        if (request.getDueDate() != null) requestBody.put("dueDate", request.getDueDate());
        if (request.getMemo() != null) requestBody.put("memo", request.getMemo());

        return fcmHttpService.patchArInvoice(invoiceId, requestBody);
    }

    // ==================== 미수 처리 ====================

    @PostMapping("/invoice/ar/{invoiceId}/receivable/complete")
    @Operation(
        summary = "매출 전표 미수 처리 완료",
        description = "미납/확인요청 상태의 매출(AR) 전표에 대해 미수 처리를 완료합니다."
    )
    public ResponseEntity<ApiResponse<Object>> completeReceivable(
        @Parameter(description = "매출 전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab") @PathVariable("invoiceId") String invoiceId
    ) {
        log.info("미수 처리 완료 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.completeReceivable(invoiceId);
    }

    @PostMapping("/invoice/ap/{invoiceId}/payable/complete")
    @Operation(
            summary = "매출 전표 미수 처리 완료",
            description = "미납/확인요청 상태의 매출(AR) 전표에 대해 미수 처리를 완료합니다."
    )
    public ResponseEntity<Object> completeAR(
            @Parameter(description = "매출 전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
            @PathVariable("invoiceId") String invoiceId
    ) {
        WebClient financialWebClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        try {
            ResponseEntity<Object> result = financialWebClient.post()
                    .uri("/fcm/invoice/ap/{invoiceId}/payable/complete", invoiceId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .exchangeToMono(response -> response.bodyToMono(String.class)
                            .map(body -> ResponseEntity.status(response.statusCode())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body((Object) body)))
                    .block();

            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }


    // ==================== 매입 전표 미수 처리 요청 ====================

    @PostMapping("/invoice/ap/receivable/request")
    @Operation(
        summary = "매입 전표 미수 처리 요청",
        description = "매입(AP) 전표에 대해 공급사에 미수 처리 요청을 발송합니다."
    )
    public ResponseEntity<ApiResponse<Object>> requestApReceivable(
        @Parameter(description = "매입 전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab", required = true) @RequestParam("invoiceId") String invoiceId
    ) {
        log.info("매입 전표 미수 처리 요청 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.requestApReceivable(invoiceId);
    }

    // ==================== 매출 전표 상태 일괄 변경 ====================

    @PostMapping("/invoice/ar/customer/response-pending")
    @Operation(
        summary = "매출 전표 상태 일괄 변경",
        description = "매출(AR) 전표들의 상태를 RESPONSE_PENDING으로 일괄 변경합니다."
    )
    public ResponseEntity<ApiResponse<Object>> updateArInvoicesResponsePending(
        @Valid @RequestBody org.ever._4ever_be_gw.business.dto.InvoiceIdsRequestDto request
    ) {
        log.info("매출 전표 상태 일괄 변경 API 호출 - invoiceIds: {}", request.getInvoiceIds());
        return fcmHttpService.updateArInvoicesResponsePending(request.getInvoiceIds());
    }

    // ==================== 매입 전표 상태 일괄 변경 ====================

    @PostMapping("/invoice/ap/supplier/response-pending")
    @Operation(
        summary = "매입 전표 상태 일괄 변경",
        description = "매입(AP) 전표들의 상태를 RESPONSE_PENDING으로 일괄 변경합니다."
    )
    public ResponseEntity<ApiResponse<Object>> updateApInvoicesResponsePending(
        @Valid @RequestBody org.ever._4ever_be_gw.business.dto.InvoiceIdsRequestDto request
    ) {
        log.info("매입 전표 상태 일괄 변경 API 호출 - invoiceIds: {}", request.getInvoiceIds());
        return fcmHttpService.updateApInvoicesResponsePending(request.getInvoiceIds());
    }

    @Operation(
            summary = "공급사 매출 전표 통계"
    )
    @GetMapping("/statistics/supplier/total-sales")
    public ResponseEntity<Object> getTotalPurchaseAmountBySupplierUserId(
            @AuthenticationPrincipal EverUserPrincipal everUserPrincipal
    ) {
        String supplierUserId = everUserPrincipal.getUserId();

        WebClient businessWebClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        // WebClient 호출 (비지니스 서비스 등 외부 서비스)
        Object result = businessWebClient.get()
                .uri("/fcm/statistics/supplier/{supplierUserId}/total-sales",supplierUserId)// 호출할 외부 API 경로
                .accept(MediaType.APPLICATION_JSON)// 요청 본문 전달
                .retrieve()
                .bodyToMono(Object.class)    // 결과 객체 매핑
                .block();                    // 동기 호출

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "고객사 매입 전표 통계"
    )
    @GetMapping("/statistics/customer/total-purchases")
    public ResponseEntity<Object> getTotalSalesAmountByCustomerUserId(
            @AuthenticationPrincipal EverUserPrincipal everUserPrincipal
    ) {
        String customerUserId = everUserPrincipal.getUserId();

        WebClient businessWebClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        // WebClient 호출 (비지니스 서비스 등 외부 서비스)
        Object result = businessWebClient.get()
                .uri("/fcm/statistics/customer/{customerUserId}/total-purchases",customerUserId)// 호출할 외부 API 경로
                .accept(MediaType.APPLICATION_JSON)// 요청 본문 전달
                .retrieve()
                .bodyToMono(Object.class)    // 결과 객체 매핑
                .block();                    // 동기 호출

        return ResponseEntity.ok(result);
    }
}
