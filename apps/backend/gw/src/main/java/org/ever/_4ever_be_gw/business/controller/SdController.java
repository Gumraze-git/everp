package org.ever._4ever_be_gw.business.controller;

import org.ever._4ever_be_gw.api.business.SdApi;
import jakarta.validation.Valid;
import java.util.Map;
import org.ever._4ever_be_gw.business.dto.analytics.SalesAnalyticsResponseDto;
import org.ever._4ever_be_gw.business.dto.customer.CustomerCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.business.service.SdHttpService;
import org.ever._4ever_be_gw.business.service.SdService;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/business/sd")

public class SdController implements SdApi {

    private final SdHttpService sdHttpService;
    private final SdService sdService;

    public SdController(SdHttpService sdHttpService, SdService sdService) {
        this.sdHttpService = sdHttpService;
        this.sdService = sdService;
    }

    @GetMapping("/metrics")

    public ResponseEntity<Object> getMetrics() {
        return sdHttpService.getDashboardStatistics();
    }

    @GetMapping("/analytics/sales")

    public ResponseEntity<SalesAnalyticsResponseDto> getSalesAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        return sdHttpService.getSalesAnalytics(startDate, endDate);
    }

    @PostMapping("/inventory-checks")
    @PreAuthorize("hasAnyAuthority('SD_USER', 'SD_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> checkInventory(

            @RequestBody Map<String, Object> requestBody
    ) {
        return sdHttpService.checkInventory(requestBody);
    }

    @GetMapping("/quotation-options")

    public ResponseEntity<Object> getQuotationOptions() {
        return sdHttpService.getQuotationOptions();
    }

    @GetMapping("/quotations")

    public ResponseEntity<Object> getQuotations(
            @AuthenticationPrincipal EverUserPrincipal principal,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        String customerId = null;
        if (principal != null && "CUSTOMER".equalsIgnoreCase(principal.getUserType())) {
            customerId = principal.getUserId();
        }
        return sdHttpService.getQuotationList(customerId, startDate, endDate, status, type, keyword, sort, page, size);
    }

    @GetMapping("/quotations/{quotationId}")

    public ResponseEntity<Object> getQuotationDetail(@PathVariable String quotationId) {
        return sdHttpService.getQuotationDetail(quotationId);
    }

    @PostMapping("/quotations")

    public ResponseEntity<Object> createQuotation(
            @AuthenticationPrincipal EverUserPrincipal principal,
            @RequestBody Map<String, Object> requestBody
    ) {
        if (principal != null) {
            requestBody.put("userId", principal.getUserId());
        }
        return sdHttpService.createQuotation(requestBody);
    }

    @PostMapping("/quotations/{quotationId}/reviews")
    @PreAuthorize("hasAnyAuthority('SD_USER', 'SD_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> createQuotationReview(@PathVariable String quotationId) {
        return sdHttpService.confirmQuotation(quotationId);
    }

    @PostMapping("/quotations/{quotationId}/rejections")
    @PreAuthorize("hasAnyAuthority('SD_USER', 'SD_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> rejectQuotation(
            @PathVariable String quotationId,
            @RequestBody Map<String, Object> requestBody
    ) {
        return sdHttpService.rejectQuotation(quotationId, requestBody);
    }

    @PostMapping("/customers")
    @PreAuthorize("hasAnyAuthority('SD_USER', 'SD_ADMIN', 'ALL_ADMIN', 'HRM_USER', 'HRM_ADMIN')")

    public Mono<ResponseEntity<CreateAuthUserResultDto>> createCustomer(@Valid @RequestBody CustomerCreateRequestDto requestDto) {
        return sdService.createCustomer(requestDto).map(result -> {
            if (result != null && result.getUserId() != null && !result.getUserId().isBlank()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            return ResponseEntity.ok(result);
        });
    }

    @GetMapping("/customers")

    public ResponseEntity<Object> getCustomers(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return sdHttpService.getCustomerList(status, type, keyword, page, size);
    }

    @GetMapping("/customers/{customerId}")

    public ResponseEntity<Object> getCustomerDetail(@PathVariable String customerId) {
        return sdHttpService.getCustomerDetail(customerId);
    }

    @PatchMapping("/customers/{customerId}")

    public ResponseEntity<Object> updateCustomer(
            @PathVariable String customerId,
            @RequestBody Map<String, Object> requestBody
    ) {
        return sdHttpService.updateCustomer(customerId, requestBody);
    }

    @DeleteMapping("/customers/{customerId}")

    public ResponseEntity<Object> deleteCustomer(@PathVariable String customerId) {
        return sdHttpService.deleteCustomer(customerId);
    }

    @GetMapping("/orders")

    public ResponseEntity<Object> getSalesOrders(
            @AuthenticationPrincipal EverUserPrincipal principal,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        String customerId = null;
        String employeeId = null;
        if (principal != null) {
            if ("CUSTOMER".equalsIgnoreCase(principal.getUserType())) {
                customerId = principal.getUserId();
            } else if ("EMPLOYEE".equalsIgnoreCase(principal.getUserType())) {
                employeeId = principal.getUserId();
            }
        }
        return sdHttpService.getOrderList(customerId, employeeId, startDate, endDate, keyword, type, status, page, size);
    }

    @GetMapping("/orders/{salesOrderId}")

    public ResponseEntity<Object> getSalesOrderDetail(@PathVariable String salesOrderId) {
        return sdHttpService.getOrderDetail(salesOrderId);
    }

    @PostMapping("/quotations/{quotationId}/orders")
    @PreAuthorize("hasAnyAuthority('SD_USER', 'SD_ADMIN', 'ALL_ADMIN')")

    public ResponseEntity<Object> approveQuotationAndCreateOrder(
            @PathVariable String quotationId,
            @RequestBody(required = false) Map<String, Object> requestBody
    ) {
        return sdHttpService.approveQuotation(quotationId, requestBody);
    }

    @GetMapping("/customer-users/me/metrics/quotation-counts")

    public ResponseEntity<Object> getCurrentCustomerQuotationCount(
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }
        return sdHttpService.getQuotationCountByCustomerUserId(principal.getUserId());
    }

    @GetMapping("/scm/quotations")

    public ResponseEntity<Object> getScmQuotations(
            @RequestParam(name = "statusCode", required = false) String statusCode,
            @RequestParam(name = "availableStatus", required = false) String availableStatus,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return sdHttpService.getScmQuotationList(statusCode, availableStatus, startDate, endDate, page, size);
    }
}
