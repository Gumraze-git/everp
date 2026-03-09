package org.ever._4ever_be_gw.api.scm.mm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.common.dto.ValueKeyOptionDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.scm.mm.dto.*;
import org.ever._4ever_be_gw.scm.mm.service.MmHttpService;
import org.ever._4ever_be_gw.scm.mm.service.MmService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "구매관리(MM)", description = "구매 관리 API")
@ApiServerErrorResponse
public interface MmApi {

    @Operation(summary = "공급업체 목록 조회")
    public ResponseEntity<Object> getSupplierList(
            
            @RequestParam(defaultValue = "ALL") String statusCode,
            
            @RequestParam(defaultValue = "ALL") String category,
            
            @RequestParam(required = false) String type,
            
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "공급업체 상세 조회")
    public ResponseEntity<Object> getSupplierDetail(@PathVariable String supplierId);

    @Operation(summary = "공급업체 수정")
    public ResponseEntity<Object> updateSupplier(
            @PathVariable String supplierId,
            @RequestBody SupplierUpdateRequestDto requestDto
    );

    @Operation(summary = "재고성 구매 요청 등록")
    public ResponseEntity<Object> createStockPurchaseRequest(
            @RequestBody StockPurchaseRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal requester
    );

    @Operation(summary = "구매요청 목록 조회")
    public ResponseEntity<Object> getPurchaseRequisitionList(
            
            @RequestParam(defaultValue = "ALL") String statusCode,
            
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "구매요청 상세 조회")
    public ResponseEntity<Object> getPurchaseRequisitionDetail(@PathVariable String purchaseRequisitionId);

    @Operation(summary = "구매요청 승인")
    public ResponseEntity<Object> approvePurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @AuthenticationPrincipal EverUserPrincipal principal
    );

    @Operation(summary = "구매요청 반려")
    public ResponseEntity<Object> rejectPurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @AuthenticationPrincipal EverUserPrincipal principal,
            @RequestBody PurchaseRequisitionRejectRequestDto requestDto
    );

    @Operation(summary = "발주서 목록 조회")
    public ResponseEntity<Object> getPurchaseOrderList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal EverUserPrincipal principal
    );

    @Operation(summary = "발주서 상세 조회")
    public ResponseEntity<Object> getPurchaseOrderDetail(@PathVariable String purchaseOrderId);

    @Operation(summary = "발주서 승인")
    public ResponseEntity<Object> approvePurchaseOrder(
            @PathVariable String purchaseOrderId,
            @AuthenticationPrincipal EverUserPrincipal principal
            );

    @Operation(summary = "발주서 반려")
    public ResponseEntity<Object> rejectPurchaseOrder(
            @PathVariable String purchaseOrderId,
            @RequestBody PurchaseOrderRejectRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal principal
    );

    @Operation(summary = "MM 통계 조회")
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getMMStatistics();

    @Operation(summary = "배송 시작")
    public ResponseEntity<Object> startDelivery(
            @PathVariable String purchaseOrderId
    );

    @Operation(summary = "입고 완료")
    public ResponseEntity<Object> completeDelivery(
            @PathVariable String purchaseOrderId
    );

    @Operation(summary = "구매요청서 상태 토글")
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseRequisitionStatusOptions();

    @Operation(summary = "공급업체 상태 토글")
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getSupplierStatusOptions();

    @Operation(summary = "구매 요청 검색 타입 토글")
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseRequisitionSearchTypeOptions();

    @Operation(summary = "공급업체 검색 타입 토글")
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getSupplierSearchTypeOptions();

}
