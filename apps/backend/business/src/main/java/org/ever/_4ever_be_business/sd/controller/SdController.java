package org.ever._4ever_be_business.sd.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.handler.ProblemDetailFactory;
import org.ever._4ever_be_business.sd.dto.request.ApproveOrderRequestDto;
import org.ever._4ever_be_business.sd.dto.request.CreateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.request.CreateQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.request.InventoryCheckRequestDto;
import org.ever._4ever_be_business.sd.dto.request.RejectQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.request.SupplierQuotationRequestDto;
import org.ever._4ever_be_business.sd.dto.request.UpdateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerDetailDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListResponseDto;
import org.ever._4ever_be_business.sd.dto.response.DashboardStatisticsDto;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResponseDto;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;
import org.ever._4ever_be_business.sd.dto.response.QuotationCodeMapDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationCountDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationCreatedResponseDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationDetailDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationListResponseDto;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;
import org.ever._4ever_be_business.sd.dto.response.SalesAnalyticsDto;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderDetailResponseDto;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderListItemDto;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderListResponseDto;
import org.ever._4ever_be_business.sd.dto.response.ScmQuotationListItemDto;
import org.ever._4ever_be_business.sd.dto.response.ScmQuotationListResponseDto;
import org.ever._4ever_be_business.sd.dto.response.SupplierQuotationWorkflowItemDto;
import org.ever._4ever_be_business.sd.service.DashboardCustomerQuotationService;
import org.ever._4ever_be_business.sd.service.DashboardOrderService;
import org.ever._4ever_be_business.sd.service.DashboardStatisticsService;
import org.ever._4ever_be_business.sd.service.DashboardSupplierQuotationService;
import org.ever._4ever_be_business.sd.service.QuotationService;
import org.ever._4ever_be_business.sd.service.SalesAnalyticsService;
import org.ever._4ever_be_business.sd.service.SdCustomerService;
import org.ever._4ever_be_business.sd.service.SdOrderService;
import org.ever._4ever_be_business.sd.vo.CustomerDetailVo;
import org.ever._4ever_be_business.sd.vo.CustomerSearchConditionVo;
import org.ever._4ever_be_business.sd.vo.OrderSearchConditionVo;
import org.ever._4ever_be_business.sd.vo.QuotationDetailVo;
import org.ever._4ever_be_business.sd.vo.QuotationSearchConditionVo;
import org.ever._4ever_be_business.sd.vo.ScmQuotationSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@Slf4j
@RestController
@RequestMapping("/sd")
@RequiredArgsConstructor
public class SdController {

    private final DashboardStatisticsService dashboardStatisticsService;
    private final SalesAnalyticsService salesAnalyticsService;
    private final SdCustomerService customerService;
    private final SdOrderService sdOrderService;
    private final QuotationService quotationService;
    private final DashboardOrderService dashboardOrderService;
    private final DashboardSupplierQuotationService dashboardSupplierQuotationService;
    private final DashboardCustomerQuotationService dashboardCustomerQuotationService;

    @GetMapping("/metrics")
    public ResponseEntity<DashboardStatisticsDto> getMetrics() {
        log.info("SD metrics 조회 API 호출");
        return ResponseEntity.ok(dashboardStatisticsService.getDashboardStatistics());
    }

    @GetMapping("/analytics/sales")
    public ResponseEntity<SalesAnalyticsDto> getSalesAnalytics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        log.info("매출 분석 통계 조회 API 호출 - startDate: {}, endDate: {}", startDate, endDate);
        return ResponseEntity.ok(salesAnalyticsService.getSalesAnalytics(startDate, endDate));
    }

    @GetMapping("/customers")
    public ResponseEntity<CustomerListResponseDto> getCustomerList(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        CustomerSearchConditionVo condition = new CustomerSearchConditionVo(status, type, search);
        return ResponseEntity.ok(customerService.getCustomerList(condition, pageable));
    }

    @PostMapping("/customers")
    public DeferredResult<ResponseEntity<?>> createCustomer(
            @RequestBody @Valid CreateCustomerRequestDto requestDto,
            HttpServletRequest request
    ) {
        log.info("고객사 생성 API 호출 - companyName: {}", requestDto.getCompanyName());
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(30000L);
        deferredResult.onTimeout(() -> deferredResult.setResult(
                ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body(ProblemDetailFactory.of(
                                HttpStatus.REQUEST_TIMEOUT,
                                "요청 시간이 초과되었습니다.",
                                "[SAGA][FAIL] 처리 시간이 초과되었습니다.",
                                null,
                                request,
                                null
                        ))
        ));
        customerService.createCustomer(requestDto, deferredResult);
        return deferredResult;
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<CustomerDetailDto> getCustomerDetail(@PathVariable String customerId) {
        log.info("고객사 상세 조회 API 호출 - customerId: {}", customerId);
        return ResponseEntity.ok(customerService.getCustomerDetail(new CustomerDetailVo(customerId)));
    }

    @PatchMapping("/customers/{customerId}")
    public ResponseEntity<Void> updateCustomer(
            @PathVariable String customerId,
            @RequestBody @Valid UpdateCustomerRequestDto requestDto
    ) {
        log.info("고객사 수정 API 호출 - customerId: {}", customerId);
        customerService.updateCustomer(customerId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        log.info("고객사 삭제 API 호출 - customerId: {}", customerId);
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<SalesOrderListResponseDto> getOrderList(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        OrderSearchConditionVo condition = new OrderSearchConditionVo(
                customerId,
                employeeId,
                startDate,
                endDate,
                search,
                type,
                status
        );
        return ResponseEntity.ok(sdOrderService.getOrderList(condition, pageable));
    }

    @GetMapping("/orders/{salesOrderId}")
    public ResponseEntity<SalesOrderDetailResponseDto> getOrderDetail(@PathVariable String salesOrderId) {
        log.info("주문서 상세 조회 API 호출 - salesOrderId: {}", salesOrderId);
        return ResponseEntity.ok(sdOrderService.getOrderDetail(salesOrderId));
    }

    @GetMapping("/quotations")
    public ResponseEntity<QuotationListResponseDto> getQuotationList(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        QuotationSearchConditionVo condition = new QuotationSearchConditionVo(
                null,
                customerId,
                startDate,
                endDate,
                status,
                type,
                search,
                sort
        );
        Page<QuotationListItemDto> result = quotationService.getQuotationList(condition, pageable);
        return ResponseEntity.ok(new QuotationListResponseDto(result.getContent(), toPageInfo(result)));
    }

    @GetMapping("/quotations/{quotationId}")
    public ResponseEntity<QuotationDetailDto> getQuotationDetail(@PathVariable String quotationId) {
        log.info("견적 상세 조회 API 호출 - quotationId: {}", quotationId);
        return ResponseEntity.ok(quotationService.getQuotationDetail(new QuotationDetailVo(quotationId)));
    }

    @PostMapping("/quotations")
    public ResponseEntity<QuotationCreatedResponseDto> createQuotation(
            @RequestBody @Valid CreateQuotationRequestDto requestDto
    ) {
        log.info("견적 생성 API 호출 - userId: {}", requestDto.getUserId());
        String quotationId = quotationService.createQuotation(requestDto);
        return ResponseEntity.created(URI.create("/sd/quotations/" + quotationId))
                .body(new QuotationCreatedResponseDto(quotationId));
    }

    @PostMapping("/quotations/{quotationId}/orders")
    public ResponseEntity<Void> approveQuotation(
            @PathVariable String quotationId,
            @RequestBody ApproveOrderRequestDto requestDto
    ) {
        log.info("견적 승인 및 주문 생성 API 호출 - quotationId: {}", quotationId);
        quotationService.approveQuotation(quotationId, requestDto.getEmployeeId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/quotations/{quotationId}/reviews")
    public ResponseEntity<Void> createQuotationReview(@PathVariable String quotationId) {
        log.info("견적 검토 요청 API 호출 - quotationId: {}", quotationId);
        quotationService.confirmQuotation(quotationId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/quotations/{quotationId}/rejections")
    public ResponseEntity<Void> rejectQuotation(
            @PathVariable String quotationId,
            @RequestBody RejectQuotationRequestDto requestDto
    ) {
        log.info("견적 거부 API 호출 - quotationId: {}", quotationId);
        quotationService.rejectQuotation(quotationId, requestDto.getReason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/inventory-checks")
    public ResponseEntity<InventoryCheckResponseDto> checkInventory(@RequestBody InventoryCheckRequestDto requestDto) {
        log.info("재고 확인 API 호출 - itemCount: {}", requestDto.getItems() != null ? requestDto.getItems().size() : 0);
        return ResponseEntity.ok(quotationService.checkInventory(requestDto));
    }

    @GetMapping("/quotation-options")
    public ResponseEntity<List<QuotationCodeMapDto>> getQuotationOptions() {
        log.info("견적 옵션 조회 API 호출");
        return ResponseEntity.ok(quotationService.getAvailableQuotationCodeMap());
    }

    @GetMapping("/customer-users/{customerUserId}/metrics/quotation-counts")
    public ResponseEntity<QuotationCountDto> getCustomerQuotationCount(@PathVariable String customerUserId) {
        log.info("고객사 견적 건수 조회 API 호출 - customerUserId: {}", customerUserId);
        return ResponseEntity.ok(quotationService.getQuotationCountByCustomerUserId(customerUserId));
    }

    @GetMapping("/supplier-users/{userId}/workflow-items/quotations")
    public ResponseEntity<List<SupplierQuotationWorkflowItemDto>> getSupplierWorkflowItems(
            @PathVariable String userId,
            @RequestParam(defaultValue = "5") int size
    ) {
        SupplierQuotationRequestDto requestDto = new SupplierQuotationRequestDto(userId, size);
        Pageable pageable = PageRequest.of(0, normalizeSize(size));
        return ResponseEntity.ok(dashboardSupplierQuotationService.getSupplierQuotationList(requestDto, pageable).getContent());
    }

    @GetMapping("/customer-users/{userId}/workflow-items/quotations")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getCustomerWorkflowItems(
            @PathVariable String userId,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardCustomerQuotationService.getCustomerQuotations(userId, normalizeSize(size)));
    }

    @GetMapping("/workflow-items/quotations")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getQuotationWorkflowItems(
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardCustomerQuotationService.getAllQuotations(normalizeSize(size)));
    }

    @GetMapping("/workflow-items/orders")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getOrderWorkflowItems(
            @RequestParam(defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardOrderService.getAllOrders(normalizeSize(size)));
    }

    @GetMapping("/scm/quotations")
    public ResponseEntity<ScmQuotationListResponseDto> getScmQuotationList(
            @RequestParam(required = false) String statusCode,
            @RequestParam(required = false) String availableStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        ScmQuotationSearchConditionVo condition = new ScmQuotationSearchConditionVo(
                startDate,
                endDate,
                statusCode,
                availableStatus
        );
        Page<ScmQuotationListItemDto> result = quotationService.getScmQuotationList(condition, pageable);
        return ResponseEntity.ok(new ScmQuotationListResponseDto(
                result.getTotalElements(),
                result.getContent(),
                toPageInfo(result)
        ));
    }

    private int normalizeSize(int size) {
        return size > 0 ? Math.min(size, 20) : 5;
    }

    private PageInfo toPageInfo(Page<?> page) {
        return new PageInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );
    }
}
