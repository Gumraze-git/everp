package org.ever._4ever_be_gw.api.scm.im;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.scm.im.dto.SalesOrderStatusChangeRequestDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재고관리(IM)", description = "재고 관리 API")
@ApiServerErrorResponse
public interface OrderApi {

    public ResponseEntity<Object> getPurchaseOrders(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate);

    public ResponseEntity<Object> getSalesOrders(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<Object> getSalesOrder(@PathVariable String salesOrderId);

    @Operation(summary = "출고 배송 상태 변경", description = "출고 준비 완료 상태를 배송중 상태로 변경합니다.")
    public ResponseEntity<Void> createShipment(
            @PathVariable String salesOrderId,
            @RequestBody SalesOrderStatusChangeRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal principal
    );

}
