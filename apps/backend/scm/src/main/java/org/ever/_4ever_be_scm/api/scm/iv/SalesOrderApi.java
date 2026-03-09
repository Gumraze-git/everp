package org.ever._4ever_be_scm.api.scm.iv;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.common.exception.BusinessException;
import org.ever._4ever_be_scm.common.exception.ErrorCode;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.SalesOrderDetailDto;
import org.ever._4ever_be_scm.scm.iv.dto.SalesOrderDto;
import org.ever._4ever_be_scm.scm.iv.dto.SalesOrderStatusChangeRequestDto;
import org.ever._4ever_be_scm.scm.iv.service.SalesOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@Tag(name = "재고관리", description = "재고 관리 API")
@ApiServerErrorResponse
public interface SalesOrderApi {

    @Operation(summary = "판매 주문 목록 조회")
    public ResponseEntity<PagedResponseDto<SalesOrderDto>> getSalesOrders(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "판매 주문 상세 조회")
    public ResponseEntity<SalesOrderDetailDto> getSalesOrder(@PathVariable String salesOrderId);

    @Operation(summary = "출고 배송 상태 변경", description = "출고 준비 완료 상태를 배송중 상태로 변경합니다.")
    public DeferredResult<ResponseEntity<?>> createShipment(
            @PathVariable String salesOrderId,
            @RequestBody SalesOrderStatusChangeRequestDto requestDto,
            @RequestParam String requesterId
    );

}
