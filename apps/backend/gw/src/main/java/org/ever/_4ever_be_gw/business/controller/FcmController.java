package org.ever._4ever_be_gw.business.controller;

import org.ever._4ever_be_gw.api.business.FcmApi;
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

public class FcmController implements FcmApi {

    private final FcmHttpService fcmHttpService;

    @GetMapping("/metrics")

    public ResponseEntity<?> getStatistics(

        @RequestParam(name = "periods", required = false) String periods
    ) {
        log.info("FCM 통계 조회 API 호출 - periods: {}", periods);
        return fcmHttpService.getFcmStatistics(periods);
    }

    @GetMapping("/invoices/purchase")

    public ResponseEntity<?> getApInvoices(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @RequestParam(name = "company", required = false) String company,
        @RequestParam(name = "startDate", required = false) String startDate,
        @RequestParam(name = "endDate", required = false) String endDate,
        @RequestParam(required = false) String status,
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size
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

    @GetMapping("/invoices/sales")

    public ResponseEntity<?> getArInvoices(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @RequestParam(name = "company", required = false) String company,
        @RequestParam(name = "startDate", required = false) String startDate,
        @RequestParam(name = "endDate", required = false) String endDate,
        @RequestParam(required = false) String status,
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size
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

    @GetMapping("/invoices/purchase/{invoiceId}")

    public ResponseEntity<?> getApInvoiceDetail(

        @PathVariable("invoiceId") String invoiceId
    ) {
        log.info("매입 전표 상세 조회 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.getApInvoiceDetail(invoiceId);
    }

    @GetMapping("/invoices/sales/{invoiceId}")

    public ResponseEntity<?> getArInvoiceDetail(

        @PathVariable("invoiceId") String invoiceId
    ) {
        log.info("매출 전표 상세 조회 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.getArInvoiceDetail(invoiceId);
    }

    @PatchMapping("/invoices/purchase/{invoiceId}")

    public ResponseEntity<?> patchApInvoice(

        @PathVariable("invoiceId") String invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    ) {
        log.info("매입 전표 수정 API 호출 - invoiceId: {}, request: {}", invoiceId, request);
        return fcmHttpService.patchApInvoice(invoiceId, toRequestBody(request));
    }

    @PatchMapping("/invoices/sales/{invoiceId}")

    public ResponseEntity<?> patchArInvoice(

        @PathVariable("invoiceId") String invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    ) {
        log.info("매출 전표 수정 API 호출 - invoiceId: {}, request: {}", invoiceId, request);
        return fcmHttpService.patchArInvoice(invoiceId, toRequestBody(request));
    }

    @PostMapping("/invoice/ar/{invoiceId}/receivable/complete")

    public ResponseEntity<?> completeReceivable(

        @PathVariable("invoiceId") String invoiceId
    ) {
        log.info("미수 처리 완료 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.completeReceivable(invoiceId);
    }

    @PostMapping("/invoice/ap/{invoiceId}/payable/complete")

    public ResponseEntity<?> completePayable(

        @PathVariable("invoiceId") String invoiceId
    ) {
        log.info("미지급 처리 완료 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.completePayable(invoiceId);
    }

    @PostMapping("/invoice/ap/receivable/request")

    public ResponseEntity<?> requestApReceivable(

        @RequestParam("invoiceId") String invoiceId
    ) {
        log.info("매입 전표 미수 처리 요청 API 호출 - invoiceId: {}", invoiceId);
        return fcmHttpService.requestApReceivable(invoiceId);
    }

    @PostMapping("/invoice/ar/customer/response-pending")

    public ResponseEntity<?> updateArInvoicesResponsePending(@Valid @RequestBody InvoiceIdsRequestDto request) {
        log.info("매출 전표 상태 일괄 변경 API 호출 - invoiceIds: {}", request.getInvoiceIds());
        return fcmHttpService.updateArInvoicesResponsePending(request.getInvoiceIds());
    }

    @PostMapping("/invoice/ap/supplier/response-pending")

    public ResponseEntity<?> updateApInvoicesResponsePending(@Valid @RequestBody InvoiceIdsRequestDto request) {
        log.info("매입 전표 상태 일괄 변경 API 호출 - invoiceIds: {}", request.getInvoiceIds());
        return fcmHttpService.updateApInvoicesResponsePending(request.getInvoiceIds());
    }

    @GetMapping("/suppliers/me/metrics/total-sales")

    public ResponseEntity<?> getTotalPurchaseAmountBySupplierUserId(
        @AuthenticationPrincipal EverUserPrincipal everUserPrincipal
    ) {
        String supplierUserId = everUserPrincipal.getUserId();
        return fcmHttpService.getSupplierTotalSales(supplierUserId);
    }

    @GetMapping("/customers/me/metrics/total-purchases")

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
