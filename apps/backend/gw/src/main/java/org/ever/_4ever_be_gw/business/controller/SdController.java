package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "영업관리(SD)", description = "영업관리(SD) API")
public class SdController {

    private final SdHttpService sdHttpService;
    private final SdService sdService;

    public SdController(SdHttpService sdHttpService, SdService sdService) {
        this.sdHttpService = sdHttpService;
        this.sdService = sdService;
    }

    @GetMapping("/metrics")
    @Operation(summary = "SD 통계 조회", description = "주간/월간/분기/연간 영업 통계를 조회합니다.")
    public ResponseEntity<Object> getMetrics() {
        return sdHttpService.getDashboardStatistics();
    }

    @GetMapping("/analytics/sales")
    @Operation(summary = "매출 분석 통계 조회", description = "기간별 매출 분석 통계를 조회합니다.")
    public ResponseEntity<SalesAnalyticsResponseDto> getSalesAnalytics(
            @Parameter(description = "시작일(YYYY-MM-DD)") @RequestParam(required = false) String startDate,
            @Parameter(description = "종료일(YYYY-MM-DD)") @RequestParam(required = false) String endDate
    ) {
        return sdHttpService.getSalesAnalytics(startDate, endDate);
    }

    @PostMapping("/inventory-checks")
    @PreAuthorize("hasAnyAuthority('SD_USER', 'SD_ADMIN', 'ALL_ADMIN')")
    @Operation(summary = "견적 품목 재고 확인", description = "요청한 품목들의 현재 재고를 확인하고 부족 여부를 반환합니다.")
    public ResponseEntity<Object> checkInventory(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "request", value = "{\n  \"items\": [\n    { \"itemId\": \"018f2c1a-3bfa-7e21-8a3c-7f9d5e2a1c44\", \"requiredQuantity\": 10 },\n    { \"itemId\": \"018f2c1a-3bfb-7e21-9b3c-1a2b3c4d5e6f\", \"requiredQuantity\": 5 }\n  ]\n}"))
            )
            @RequestBody Map<String, Object> requestBody
    ) {
        return sdHttpService.checkInventory(requestBody);
    }

    @GetMapping("/quotation-options")
    @Operation(summary = "견적 옵션 조회", description = "사용 가능한 견적 ID/코드 목록을 조회합니다.")
    public ResponseEntity<Object> getQuotationOptions() {
        return sdHttpService.getQuotationOptions();
    }

    @GetMapping("/quotations")
    @Operation(summary = "견적 목록 조회", description = "견적을 페이지네이션으로 조회합니다.")
    public ResponseEntity<Object> getQuotations(
            @AuthenticationPrincipal EverUserPrincipal principal,
            @Parameter(description = "시작일(YYYY-MM-DD)") @RequestParam(name = "startDate", required = false) String startDate,
            @Parameter(description = "종료일(YYYY-MM-DD)") @RequestParam(name = "endDate", required = false) String endDate,
            @Parameter(description = "상태: PENDING, REVIEW, APPROVAL, REJECTED, ALL") @RequestParam(name = "status", required = false) String status,
            @Parameter(description = "검색 타입", example = "quotationNumber") @RequestParam(name = "type", required = false) String type,
            @Parameter(description = "검색어") @RequestParam(name = "keyword", required = false) String keyword,
            @Parameter(description = "정렬 필드,정렬방향") @RequestParam(name = "sort", required = false) String sort,
            @Parameter(description = "페이지 번호(0-base)") @RequestParam(name = "page", required = false) Integer page,
            @Parameter(description = "페이지 크기(최대 200)") @RequestParam(name = "size", required = false) Integer size
    ) {
        String customerId = null;
        if (principal != null && "CUSTOMER".equalsIgnoreCase(principal.getUserType())) {
            customerId = principal.getUserId();
        }
        return sdHttpService.getQuotationList(customerId, startDate, endDate, status, type, keyword, sort, page, size);
    }

    @GetMapping("/quotations/{quotationId}")
    @Operation(summary = "견적 상세 조회", description = "견적 단건 상세 정보를 조회합니다.")
    public ResponseEntity<Object> getQuotationDetail(@PathVariable String quotationId) {
        return sdHttpService.getQuotationDetail(quotationId);
    }

    @PostMapping("/quotations")
    @Operation(summary = "신규 견적서 생성", description = "신규 견적서를 생성합니다.")
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
    @Operation(summary = "견적 검토 요청", description = "선택한 견적의 검토 요청을 수행합니다.")
    public ResponseEntity<Object> createQuotationReview(@PathVariable String quotationId) {
        return sdHttpService.confirmQuotation(quotationId);
    }

    @PostMapping("/quotations/{quotationId}/rejections")
    @PreAuthorize("hasAnyAuthority('SD_USER', 'SD_ADMIN', 'ALL_ADMIN')")
    @Operation(summary = "견적 거부", description = "견적서를 거부하고 거부 사유를 기록합니다.")
    public ResponseEntity<Object> rejectQuotation(
            @PathVariable String quotationId,
            @RequestBody Map<String, Object> requestBody
    ) {
        return sdHttpService.rejectQuotation(quotationId, requestBody);
    }

    @PostMapping("/customers")
    @PreAuthorize("hasAnyAuthority('SD_USER', 'SD_ADMIN', 'ALL_ADMIN', 'HRM_USER', 'HRM_ADMIN')")
    @Operation(summary = "고객사 등록", description = "고객사 정보를 신규 등록합니다.")
    public Mono<ResponseEntity<CreateAuthUserResultDto>> createCustomer(@Valid @RequestBody CustomerCreateRequestDto requestDto) {
        return sdService.createCustomer(requestDto).map(result -> {
            if (result != null && result.getUserId() != null && !result.getUserId().isBlank()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(result);
            }
            return ResponseEntity.ok(result);
        });
    }

    @GetMapping("/customers")
    @Operation(summary = "고객사 목록 조회", description = "고객사를 페이지네이션으로 조회합니다.")
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
    @Operation(summary = "고객사 상세 조회", description = "고객사 상세 정보를 조회합니다.")
    public ResponseEntity<Object> getCustomerDetail(@PathVariable String customerId) {
        return sdHttpService.getCustomerDetail(customerId);
    }

    @PatchMapping("/customers/{customerId}")
    @Operation(summary = "고객사 정보 수정", description = "고객사 기본/연락/담당자 정보를 수정합니다.")
    public ResponseEntity<Object> updateCustomer(
            @PathVariable String customerId,
            @RequestBody Map<String, Object> requestBody
    ) {
        return sdHttpService.updateCustomer(customerId, requestBody);
    }

    @DeleteMapping("/customers/{customerId}")
    @Operation(summary = "고객사 삭제", description = "고객사 정보를 삭제합니다.")
    public ResponseEntity<Object> deleteCustomer(@PathVariable String customerId) {
        return sdHttpService.deleteCustomer(customerId);
    }

    @GetMapping("/orders")
    @Operation(summary = "주문서 목록 조회", description = "견적 승인에 따라 자동 생성된 주문서 목록을 조회합니다.")
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
    @Operation(summary = "주문서 상세 조회", description = "주문서 상세 정보를 조회합니다.")
    public ResponseEntity<Object> getSalesOrderDetail(@PathVariable String salesOrderId) {
        return sdHttpService.getOrderDetail(salesOrderId);
    }

    @PostMapping("/quotations/{quotationId}/orders")
    @PreAuthorize("hasAnyAuthority('SD_USER', 'SD_ADMIN', 'ALL_ADMIN')")
    @Operation(summary = "견적 승인 및 주문 전환", description = "견적서를 승인하고 주문서를 생성합니다.")
    public ResponseEntity<Object> approveQuotationAndCreateOrder(
            @PathVariable String quotationId,
            @RequestBody(required = false) Map<String, Object> requestBody
    ) {
        return sdHttpService.approveQuotation(quotationId, requestBody);
    }

    @GetMapping("/customer-users/me/metrics/quotation-counts")
    @Operation(summary = "내 견적 건수 조회", description = "현재 로그인한 고객사 사용자의 견적 건수를 조회합니다.")
    public ResponseEntity<Object> getCurrentCustomerQuotationCount(
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }
        return sdHttpService.getQuotationCountByCustomerUserId(principal.getUserId());
    }

    @GetMapping("/scm/quotations")
    @Operation(summary = "SCM용 견적 목록 조회", description = "SCM 내부 소비자가 사용하는 견적 목록을 조회합니다.")
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
