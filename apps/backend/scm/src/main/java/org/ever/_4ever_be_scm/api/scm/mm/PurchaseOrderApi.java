package org.ever._4ever_be_scm.api.scm.mm;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.common.exception.ErrorCode;
import org.ever._4ever_be_scm.common.exception.handler.ProblemDetailFactory;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.PurchaseOrderDetailResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.PurchaseOrderListResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.PurchaseOrderRejectRequestDto;
import org.ever._4ever_be_scm.scm.mm.service.PurchaseOrderService;
import org.ever._4ever_be_scm.scm.mm.vo.PurchaseOrderSearchVo;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@Tag(name = "구매관리", description = "구매 관리 API")
@ApiServerErrorResponse
public interface PurchaseOrderApi {

    public ResponseEntity<PagedResponseDto<PurchaseOrderListResponseDto>> getPurchaseOrderList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<PagedResponseDto<PurchaseOrderListResponseDto>> getPurchaseOrderListBySupplier(
            @PathVariable String userId,
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<PurchaseOrderDetailResponseDto> getPurchaseOrderDetail(
            @PathVariable String purchaseOrderId);

    public DeferredResult<ResponseEntity<?>> approvePurchaseOrder(
            @PathVariable String purchaseOrderId,
            @RequestParam String requesterId);

    public ResponseEntity<Void> rejectPurchaseOrder(
            @PathVariable String purchaseOrderId,
            @RequestParam String requesterId,
            @RequestBody PurchaseOrderRejectRequestDto requestDto);

    public ResponseEntity<Void> startDelivery(
            @PathVariable String purchaseOrderId);

    public ResponseEntity<Void> completeDelivery(
            @PathVariable String purchaseOrderId);

}
