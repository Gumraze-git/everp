package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.InvoiceIdsRequestDto;
import org.ever._4ever_be_gw.business.dto.InvoiceUpdateRequestDto;
import org.ever._4ever_be_gw.business.service.FcmHttpService;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/business/fcm")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "재무관리(FCM)", description = "재무 관리 API")
public class FcmController {

    private final FcmHttpService fcmHttpService;

    @GetMapping("/statictics")
    @Operation(summary = "FCM 통계 조회", description = "기간별 재무 관리 통계를 조회합니다.")
    public ResponseEntity<?> getStatistics(
        @Parameter(description = "조회 기간 목록(콤마 구분)")
        @RequestParam(name = "periods", required = false) String periods
    ) {
        log.info("FCM 통계 조회 API 호출 - periods: {}", periods);
        return fcmHttpService.getFcmStatistics(periods);
    }

    @GetMapping("/invoice/ap")
    @Operation(
        summary = "매입 전표 목록 조회",
        description = "매입(AP) 전표 목록을 조회합니다. JWT 토큰이 있고 userType이 SUPPLIER인 경우 해당 공급사의 매입(AP) 전표를 조회합니다."
    )
    public ResponseEntity<?> getApInvoices(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
        @Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
        @Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
        @RequestParam(required = false) String status,
        @Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
    ) {
        if (principal != null && "CUSTOMER".equals(principal.getUserType())) {
            String customerUserId = principal.getUserId();
            log.info(
                "CUSTOMER 사용자의 매출 전표 목록 조회 API 호출 - customerUserId: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                customerUserId,
                startDate,
                endDate,
                page,
                size
            );
            return fcmHttpService.getArInvoicesByCustomerUserId(customerUserId, status, startDate, endDate, page, size);
        }

        log.info(
            "매입 전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
            company,
            startDate,
            endDate,
            page,
            size
        );
        return fcmHttpService.getApInvoices(company, status, startDate, endDate, page, size);
    }

    @GetMapping("/invoice/ar")
    @Operation(
        summary = "매출 전표 목록 조회",
        description = "매출(AR) 전표 목록을 조회합니다. JWT 토큰이 있고 userType이 CUSTOMER인 경우 해당 고객사의 매출(AR) 전표를 조회합니다."
    )
    public ResponseEntity<?> getArInvoices(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @Parameter(description = "거래처 명") @RequestParam(name = "company", required = false) String company,
        @Parameter(description = "시작일(yyyy-MM-dd)") @RequestParam(name = "startDate", required = false) String startDate,
        @Parameter(description = "종료일(yyyy-MM-dd)") @RequestParam(name = "endDate", required = false) String endDate,
        @RequestParam(required = false) String status,
        @Parameter(description = "페이지") @RequestParam(name = "page", required = false) Integer page,
        @Parameter(description = "사이즈") @RequestParam(name = "size", required = false) Integer size
    ) {
        if (principal != null && "SUPPLIER".equals(principal.getUserType())) {
            String supplierUserId = principal.getUserId();
            log.info(
                "SUPPLIER 사용자의 매입 전표 목록 조회 API 호출 - supplierUserId: {}, startDate: {}, endDate: {}, page: {}, size: {}",
                supplierUserId,
                startDate,
                endDate,
                page,
                size
            );
            return fcmHttpService.getApInvoicesBySupplierUserId(supplierUserId, status, startDate, endDate, page, size);
        }

        log.info(
            "매출 전표 목록 조회 API 호출 - company: {}, startDate: {}, endDate: {}, page: {}, size: {}",
            company,
            startDate,
            endDate,
            page,
            size
        );
        return fcmHttpService.getArInvoices(company, status, startDate, endDate, page, size);
    }

    @GetMapping("/invoice/ap/{invoiceId}")
    @Operation(summary = "매입 전표 상세 조회", description = "매입(AP) 전표 상세 정보를 조회합니다.")
    public ResponseEntity<?> getApInvoiceDetail(
        @Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("invoiceId") String invoiceId
    ) {
        log.info("매입 전표 상세 조회 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.getApInvoiceDetail(invoiceId);
    }

    @GetMapping("/invoice/ar/{invoiceId}")
    @Operation(summary = "매출 전표 상세 조회", description = "매출(AR) 전표 상세 정보를 조회합니다.")
    public ResponseEntity<?> getArInvoiceDetail(
        @Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("invoiceId") String invoiceId
    ) {
        log.info("매출 전표 상세 조회 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.getArInvoiceDetail(invoiceId);
    }

    @PatchMapping("/invoice/ap/{invoiceId}")
    @Operation(summary = "매입 전표 수정", description = "매입(AP) 전표를 수정합니다.")
    public ResponseEntity<?> patchApInvoice(
        @Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("invoiceId") String invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    ) {
        log.info("매입 전표 수정 API 호출 - invoiceId: {}, request: {}", invoiceId, request);
        return fcmHttpService.patchApInvoice(invoiceId, toRequestBody(request));
    }

    @PatchMapping("/invoice/ar/{invoiceId}")
    @Operation(summary = "매출 전표 수정", description = "매출(AR) 전표를 수정합니다.")
    public ResponseEntity<?> patchArInvoice(
        @Parameter(description = "전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("invoiceId") String invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    ) {
        log.info("매출 전표 수정 API 호출 - invoiceId: {}, request: {}", invoiceId, request);
        return fcmHttpService.patchArInvoice(invoiceId, toRequestBody(request));
    }

    @PostMapping("/invoice/ar/{invoiceId}/receivable/complete")
    @Operation(
        summary = "매출 전표 미수 처리 완료",
        description = "미납/확인요청 상태의 매출(AR) 전표에 대해 미수 처리를 완료합니다."
    )
    public ResponseEntity<?> completeReceivable(
        @Parameter(description = "매출 전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("invoiceId") String invoiceId
    ) {
        log.info("미수 처리 완료 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.completeReceivable(invoiceId);
    }

    @PostMapping("/invoice/ap/{invoiceId}/payable/complete")
    @Operation(
        summary = "매입 전표 미지급 처리 완료",
        description = "미납/확인요청 상태의 매입(AP) 전표에 대해 미지급 처리를 완료합니다."
    )
    public ResponseEntity<?> completePayable(
        @Parameter(description = "매입 전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab")
        @PathVariable("invoiceId") String invoiceId
    ) {
        log.info("미지급 처리 완료 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.completePayable(invoiceId);
    }

    @PostMapping("/invoice/ap/receivable/request")
    @Operation(summary = "매입 전표 미수 처리 요청", description = "매입(AP) 전표에 대해 공급사에 미수 처리 요청을 발송합니다.")
    public ResponseEntity<?> requestApReceivable(
        @Parameter(description = "매입 전표 ID", example = "0193e7c8-1234-7abc-9def-0123456789ab", required = true)
        @RequestParam("invoiceId") String invoiceId
    ) {
        log.info("매입 전표 미수 처리 요청 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.requestApReceivable(invoiceId);
    }

    @PostMapping("/invoice/ar/customer/response-pending")
    @Operation(summary = "매출 전표 상태 일괄 변경", description = "매출(AR) 전표들의 상태를 RESPONSE_PENDING으로 일괄 변경합니다.")
    public ResponseEntity<?> updateArInvoicesResponsePending(@Valid @RequestBody InvoiceIdsRequestDto request) {
        log.info("매출 전표 상태 일괄 변경 API 호출 - invoiceIds: {}", request.getInvoiceIds());
        return fcmHttpService.updateArInvoicesResponsePending(request.getInvoiceIds());
    }

    @PostMapping("/invoice/ap/supplier/response-pending")
    @Operation(summary = "매입 전표 상태 일괄 변경", description = "매입(AP) 전표들의 상태를 RESPONSE_PENDING으로 일괄 변경합니다.")
    public ResponseEntity<?> updateApInvoicesResponsePending(@Valid @RequestBody InvoiceIdsRequestDto request) {
        log.info("매입 전표 상태 일괄 변경 API 호출 - invoiceIds: {}", request.getInvoiceIds());
        return fcmHttpService.updateApInvoicesResponsePending(request.getInvoiceIds());
    }

    @GetMapping("/statistics/supplier/total-sales")
    @Operation(summary = "공급사 매출 전표 통계")
    public ResponseEntity<?> getTotalPurchaseAmountBySupplierUserId(
        @AuthenticationPrincipal EverUserPrincipal everUserPrincipal
    ) {
        String supplierUserId = everUserPrincipal.getUserId();
        return fcmHttpService.getSupplierTotalSales(supplierUserId);
    }

    @GetMapping("/statistics/customer/total-purchases")
    @Operation(summary = "고객사 매입 전표 통계")
    public ResponseEntity<?> getTotalSalesAmountByCustomerUserId(
        @AuthenticationPrincipal EverUserPrincipal everUserPrincipal
    ) {
        String customerUserId = everUserPrincipal.getUserId();
        return fcmHttpService.getCustomerTotalPurchases(customerUserId);
    }

    private Map<String, Object> toRequestBody(InvoiceUpdateRequestDto request) {
        Map<String, Object> requestBody = new HashMap<>();
        if (request.getStatus() != null) {
            requestBody.put("status", request.getStatus());
        }
        if (request.getDueDate() != null) {
            requestBody.put("dueDate", request.getDueDate());
        }
        if (request.getMemo() != null) {
            requestBody.put("memo", request.getMemo());
        }
        return requestBody;
    }
}
