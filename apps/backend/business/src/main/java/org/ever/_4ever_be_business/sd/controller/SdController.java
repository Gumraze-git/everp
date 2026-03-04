package org.ever._4ever_be_business.sd.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.hr.dto.response.PageResponseDto;
import org.ever._4ever_be_business.sd.dto.request.*;
import org.ever._4ever_be_business.sd.dto.response.*;
import org.ever._4ever_be_business.sd.service.*;
import org.ever._4ever_be_business.sd.vo.*;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private final SdSupplierOrderService sdSupplierOrderService;
    private final DashboardOrderService dashboardOrderService;
    private final DashboardSupplierQuotationService dashboardSupplierQuotationService;
    private final DashboardCustomerQuotationService dashboardCustomerQuotationService;

    // ==================== Statistics ====================

    /**
     * 대시보드 통계 조회 (주/월/분기/년)
     */
    @GetMapping("/dashboard/statistics")
    public ApiResponse<DashboardStatisticsDto> getDashboardStatistics() {
        log.info("대시보드 통계 조회 API 호출");
        DashboardStatisticsDto result = dashboardStatisticsService.getDashboardStatistics();
        log.info("대시보드 통계 조회 성공");
        return ApiResponse.success(result, "OK", HttpStatus.OK);
    }

    /**
     * 매출 분석 통계 데이터 조회
     */
    @GetMapping("/analytics/sales")
    public ApiResponse<SalesAnalyticsDto> getSalesAnalytics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("매출 분석 통계 조회 API 호출 - startDate: {}, endDate: {}", startDate, endDate);

        if (startDate.isAfter(endDate)) {
            log.warn("시작일이 종료일보다 늦습니다 - startDate: {}, endDate: {}", startDate, endDate);
            return ApiResponse.fail("시작일은 종료일보다 이전이어야 합니다.", HttpStatus.BAD_REQUEST);
        }

        SalesAnalyticsDto result = salesAnalyticsService.getSalesAnalytics(startDate, endDate);
        log.info("매출 분석 통계 조회 성공");
        return ApiResponse.success(result, "매출 통계 데이터를 조회했습니다.", HttpStatus.OK);
    }

    // ==================== Customers ====================

    /**
     * 고객사 목록 조회 (검색 + 페이징)
     */
    @GetMapping("/customers")
    public ApiResponse<CustomerListResponseDto> getCustomerList(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("고객사 목록 조회 API 호출 - status: {}, type: {}, search: {}, page: {}, size: {}",
                status, type, search, page, size);

        CustomerSearchConditionVo condition = new CustomerSearchConditionVo(status, type, search);
        Pageable pageable = PageRequest.of(page, size);
        CustomerListResponseDto result = customerService.getCustomerList(condition, pageable);

        log.info("고객사 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getPage().getTotalElements(), result.getPage().getTotalPages());
        return ApiResponse.success(result, "고객사 목록을 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 고객사 등록
     */
    @PostMapping("/customers")
    public DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> createCustomer(
        @RequestBody CreateCustomerRequestDto dto
    ) {
        log.info("고객사 등록 API 호출 - companyName: {}, businessNumber: {}", dto.getCompanyName(), dto.getBusinessNumber());

        DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> deferredResult = new DeferredResult<>(30000L);
        deferredResult.onTimeout(() -> {
            log.warn("[SAGA][TIMEOUT] 고객사 등록 처리 타임아웃 - businessNumber: {}", dto.getBusinessNumber());
            deferredResult.setResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ApiResponse.fail("[SAGA][FAIL] 처리 시간이 초과되었습니다.", HttpStatus.REQUEST_TIMEOUT)));
        });

        customerService.createCustomer(dto, deferredResult);
        return deferredResult;
    }

    /**
     * 고객사 상세 정보 조회
     */
    @GetMapping("/customers/{customerId}")
    public ApiResponse<CustomerDetailDto> getCustomerDetail(@PathVariable String customerId) {
        log.info("고객사 상세 정보 조회 API 호출 - customerId: {}", customerId);
        CustomerDetailVo vo = new CustomerDetailVo(customerId);
        CustomerDetailDto result = customerService.getCustomerDetail(vo);
        log.info("고객사 상세 정보 조회 성공 - customerId: {}, customerName: {}", customerId, result.getCustomerName());
        return ApiResponse.success(result, "고객사 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 고객사 정보 수정
     */
    @PatchMapping("/customers/{customerId}")
    public ApiResponse<Void> updateCustomer(
            @PathVariable String customerId,
            @RequestBody UpdateCustomerRequestDto dto) {
        log.info("고객사 정보 수정 API 호출 - customerId: {}, customerName: {}", customerId, dto.getCustomerName());
        customerService.updateCustomer(customerId, dto);
        log.info("고객사 정보 수정 성공 - customerId: {}", customerId);
        return ApiResponse.success(null, "고객사 정보가 수정되었습니다.", HttpStatus.OK);
    }

    /**
     * 고객사 삭제 (Soft Delete)
     */
    @DeleteMapping("/customers/{customerId}")
    public ApiResponse<Void> deleteCustomer(@PathVariable String customerId) {
        log.info("고객사 삭제 API 호출 - customerId: {}", customerId);
        customerService.deleteCustomer(customerId);
        log.info("고객사 삭제 성공 (Soft Delete) - customerId: {}", customerId);
        return ApiResponse.success(null, "고객사가 삭제되었습니다.", HttpStatus.OK);
    }

    // ==================== Orders ====================

    /**
     * 주문 목록 조회 (검색 + 페이징)
     */
    @GetMapping("/orders")
    public ApiResponse<SalesOrderListResponseDto> getOrderList(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String employeeId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("주문 목록 조회 API 호출 - customerId: {}, employeeId: {}, startDate: {}, endDate: {}, status: {}, type: {}, search: {}, page: {}, size: {}",
                customerId, employeeId, startDate, endDate, status, type, search, page, size);

        OrderSearchConditionVo condition = new OrderSearchConditionVo(customerId, employeeId, startDate, endDate, search, type, status);
        Pageable pageable = PageRequest.of(page, size);
        SalesOrderListResponseDto result = sdOrderService.getOrderList(condition, pageable);

        log.info("주문 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getPage().getTotalElements(), result.getPage().getTotalPages());
        return ApiResponse.success(result, "주문 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 주문서 상세 조회
     */
    @GetMapping("/orders/{salesOrderId}")
    public ApiResponse<SalesOrderDetailResponseDto> getOrderDetail(@PathVariable String salesOrderId) {
        log.info("주문서 상세 조회 API 호출 - salesOrderId: {}", salesOrderId);
        SalesOrderDetailResponseDto result = sdOrderService.getOrderDetail(salesOrderId);
        log.info("주문서 상세 조회 성공 - salesOrderId: {}", salesOrderId);
        return ApiResponse.success(result, "주문서 상세 정보를 조회했습니다.", HttpStatus.OK);
    }

    /**
     * 공급사 사용자 기준 주문서 목록 조회 (대시보드용)
     */
    @GetMapping("/orders/supplier")
    public ApiResponse<PageResponseDto<org.ever._4ever_be_business.sd.dto.response.SupplierOrderWorkflowItemDto>> getSupplierOrderList(
            @RequestParam("userId") String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        log.info("[INFO] 공급사 주문서 목록 조회 API 호출 - userId: {}, page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        org.springframework.data.domain.Page<org.ever._4ever_be_business.sd.dto.response.SupplierOrderWorkflowItemDto> result =
                sdSupplierOrderService.getSupplierOrderList(userId, pageable);

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        PageResponseDto<org.ever._4ever_be_business.sd.dto.response.SupplierOrderWorkflowItemDto> responseDto = new PageResponseDto<>(
                (int) result.getTotalElements(),
                result.getContent(),
                pageInfo
        );

        log.info("[INFO]공급사 주문서 목록 조회 성공 - total: {}, size: {}", result.getTotalElements(), result.getContent().size());
        return ApiResponse.success(responseDto, "공급사 주문서 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    // ==================== Quotations ====================

    /**
     * 견적 목록 조회 (검색 + 페이징)
     */
    @GetMapping("/quotations")
    public ApiResponse<PageResponseDto<QuotationListItemDto>> getQuotationList(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("견적 목록 조회 API 호출 - customerId: {}, startDate: {}, endDate: {}, status: {}, type: {}, search: {}, sort: {}, page: {}, size: {}",
                customerId, startDate, endDate, status, type, search, sort, page, size);

        QuotationSearchConditionVo condition = new QuotationSearchConditionVo(null, customerId, startDate, endDate, status, type, search, sort);
        Pageable pageable = PageRequest.of(page, size);
        Page<QuotationListItemDto> result = quotationService.getQuotationList(condition, pageable);

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        PageResponseDto<QuotationListItemDto> responseDto = new PageResponseDto<>(
                (int) result.getTotalElements(),
                result.getContent(),
                pageInfo
        );

        log.info("견적 목록 조회 성공 - totalElements: {}, totalPages: {}", result.getTotalElements(), result.getTotalPages());
        return ApiResponse.success(responseDto, "견적 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * scm을 위한 견적 목록 조회 (페이징, 날짜 필터링)
     */
    @GetMapping("/scm/quotations")
    public ApiResponse<PageResponseDto<ScmQuotationListItemDto>> getQuotationForScmList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String statusCode,
            @RequestParam(required = false) String availableStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("SCM 견적 목록 조회 API 호출 - startDate: {}, endDate: {}, statusCode: {}, availableStatus: {}, page: {}, size: {}",
                startDate, endDate, statusCode, availableStatus, page, size);

        ScmQuotationSearchConditionVo condition = new ScmQuotationSearchConditionVo(startDate, endDate, statusCode, availableStatus);
        Pageable pageable = PageRequest.of(page, size);
        Page<ScmQuotationListItemDto> result = quotationService.getScmQuotationList(condition, pageable);

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        PageResponseDto<ScmQuotationListItemDto> responseDto = new PageResponseDto<>(
                (int) result.getTotalElements(),
                result.getContent(),
                pageInfo
        );

        log.info("SCM 견적 목록 조회 성공 - totalElements: {}, totalPages: {}", result.getTotalElements(), result.getTotalPages());
        return ApiResponse.success(responseDto, "SCM 견적 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 대시보드용(공급사) 발주서 목록 조회
     * GET /sd/quotation/supplier?userId={userId}&size={size}
     */
    @GetMapping("/quotation/supplier")
    public ApiResponse<PageResponseDto<SupplierQuotationWorkflowItemDto>> getSupplierQuotationList(
            @ModelAttribute SupplierQuotationRequestDto request
    ) {
        int size = (request.getSize() != null && request.getSize() > 0) ? request.getSize() : 5;
        Pageable pageable = PageRequest.of(0, size);

        Page<SupplierQuotationWorkflowItemDto> result = dashboardSupplierQuotationService.getSupplierQuotationList(request, pageable);

        PageInfo pageInfo = new PageInfo(
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.hasNext()
        );

        PageResponseDto<SupplierQuotationWorkflowItemDto> responseDto = new PageResponseDto<>(
                (int) result.getTotalElements(),
                result.getContent(),
                pageInfo
        );

        return ApiResponse.success(responseDto, "공급사 발주서 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 대시보드용(내부 사용자) 주문서 목록 조회
     * GET /sd/dashboard/orders/mm?size={size}
     */
    @GetMapping("/dashboard/orders/mm")
    public ApiResponse<List<DashboardWorkflowItemDto>> getInternalOrderList(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        List<DashboardWorkflowItemDto> items =
                dashboardOrderService.getAllOrders(size);

        return ApiResponse.success(items, "내부 주문서 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 대시보드용(고객사) 견적서 목록 조회
     * GET /sd/dashboard/quotation/customer?userId={userId}&size={size}
     */
    @GetMapping("/dashboard/quotation/customer")
    public ApiResponse<List<DashboardWorkflowItemDto>> getCustomerQuotationList(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        if (userId == null || userId.isBlank()) {
            return ApiResponse.fail("userId는 필수입니다.", HttpStatus.BAD_REQUEST);
        }

        List<DashboardWorkflowItemDto> items =
                dashboardCustomerQuotationService.getCustomerQuotations(userId, size);

        return ApiResponse.success(items, "고객사 견적서 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 대시보드용(내부 사용자) 견적서 목록 조회
     * GET /sd/dashboard/quotation/mm?size={size}
     */
    @GetMapping("/dashboard/quotation/mm")
    public ApiResponse<List<DashboardWorkflowItemDto>> getInternalQuotationList(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        List<DashboardWorkflowItemDto> items =
                dashboardCustomerQuotationService.getAllQuotations(size);

        return ApiResponse.success(items, "내부 견적서 목록 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 견적 상세 조회
     */
    @GetMapping("/quotations/{quotationId}")
    public ApiResponse<QuotationDetailDto> getQuotationDetail(@PathVariable String quotationId) {
        log.info("견적 상세 조회 API 호출 - quotationId: {}", quotationId);
        QuotationDetailVo vo = new QuotationDetailVo(quotationId);
        QuotationDetailDto result = quotationService.getQuotationDetail(vo);
        log.info("견적 상세 조회 성공 - quotationId: {}, quotationNumber: {}", quotationId, result.getQuotationNumber());
        return ApiResponse.success(result, "견적 상세 조회에 성공했습니다.", HttpStatus.OK);
    }

    /**
     * 견적서 생성
     * TODO : 영업 role의 유저들에게 알람을 보내야합니다.
     */
    @PostMapping("/quotations")
    public ApiResponse<Map<String, String>> createQuotation(@RequestBody CreateQuotationRequestDto dto) {
        log.info("견적서 생성 API 호출 - userId: {}, items count: {}",
                dto.getUserId(),  dto.getItems() != null ? dto.getItems().size() : 0);
        String quotationId = quotationService.createQuotation(dto);
        log.info("견적서 생성 성공 - quotationId: {}", quotationId);
        return ApiResponse.success(Map.of("quotationId", quotationId), "견적서가 생성되었습니다.", HttpStatus.CREATED);
    }

    /**
     * 견적서 승인 및 주문 생성
     * TODO : 고객사 ID에 해당하는 유저들 혹은 고객ID에 해당하는 유저에게 알람을 보내야합니다.
     */
    @PostMapping("/quotations/{quotationId}/approve-order")
    public ApiResponse<Void> approveQuotation(
            @PathVariable String quotationId,
            @RequestBody ApproveOrderRequestDto dto) {
        log.info("견적서 승인 및 주문 생성 API 호출 - quotationId: {}, employeeId: {}", quotationId, dto.getEmployeeId());
        quotationService.approveQuotation(quotationId, dto.getEmployeeId());
        log.info("견적서 승인 및 주문 생성 성공 - quotationId: {}", quotationId);
        return ApiResponse.success(null, "견적서가 승인되고 주문이 생성되었습니다.", HttpStatus.OK);
    }

    /**
     * 견적서 검토 확정
     */
    @PostMapping("/quotations/confirm")
    public ApiResponse<Void> confirmQuotation(@RequestBody ConfirmQuotationRequestDto dto) {
        log.info("견적서 검토 확정 API 호출 - quotationId: {}", dto.getQuotationId());
        quotationService.confirmQuotation(dto.getQuotationId());
        log.info("견적서 검토 확정 성공 - quotationId: {}", dto.getQuotationId());
        return ApiResponse.success(null, "견적서가 검토 확정되었습니다.", HttpStatus.OK);
    }

    /**
     * 견적서 거부
     * TODO : 고객사 ID에 해당하는 유저들 혹은 고객 ID에 해당하는 유저에게 알람을 보내야합니다.
     */
    @PostMapping("/quotations/{quotationId}/rejected")
    public ApiResponse<Void> rejectQuotation(
            @PathVariable String quotationId,
            @RequestBody RejectQuotationRequestDto dto) {
        log.info("견적서 거부 API 호출 - quotationId: {}, reason: {}", quotationId, dto.getReason());
        quotationService.rejectQuotation(quotationId, dto.getReason());
        log.info("견적서 거부 성공 - quotationId: {}", quotationId);
        return ApiResponse.success(null, "견적서가 거부되었습니다.", HttpStatus.OK);
    }

    /**
     * 재고 확인
     */
    @PostMapping("/quotations/inventory/check")
    public ApiResponse<InventoryCheckResponseDto> checkInventory(@RequestBody InventoryCheckRequestDto requestDto) {
        log.info("재고 확인 API 호출 - items count: {}", requestDto.getItems() != null ? requestDto.getItems().size() : 0);
        InventoryCheckResponseDto result = quotationService.checkInventory(requestDto);
        log.info("재고 확인 성공 - items count: {}", result.getItems().size());
        return ApiResponse.success(result, "재고 확인을 완료했습니다.", HttpStatus.OK);
    }

    /**
     * availableStatus가 null이 아닌 견적서 ID/코드 맵 조회
     */
    @GetMapping("/quotations/available/map")
    public ApiResponse<List<QuotationCodeMapDto>> getAvailableQuotationCodeMap() {
        log.info("availableStatus가 null이 아닌 견적서 ID/코드 맵 조회 API 호출");
        List<QuotationCodeMapDto> result = quotationService.getAvailableQuotationCodeMap();
        log.info("availableStatus가 null이 아닌 견적서 ID/코드 맵 조회 성공 - count: {}", result.size());
        return ApiResponse.success(result, "견적서 ID/코드 맵을 조회했습니다", HttpStatus.OK);
    }

    /**
     * 고객사별 견적 건수 조회 (기간별)
     */
    @GetMapping("/quotations/customer/{customerUserId}/count")
    public ApiResponse<QuotationCountDto> getQuotationCountByCustomerUserId(@PathVariable String customerUserId) {
        log.info("고객사별 견적 건수 조회 API 호출 (기간별) - customerUserId: {}", customerUserId);
        QuotationCountDto result = quotationService.getQuotationCountByCustomerUserId(customerUserId);
        log.info("고객사별 견적 건수 조회 성공 (기간별) - customerUserId: {}", customerUserId);
        return ApiResponse.success(result, "고객사별 견적 건수를 조회했습니다.", HttpStatus.OK);
    }
}
