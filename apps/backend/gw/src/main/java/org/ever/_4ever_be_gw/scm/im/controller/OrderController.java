package org.ever._4ever_be_gw.scm.im.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.scm.im.dto.SalesOrderStatusChangeRequestDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Tag(name = "재고관리(IM)", description = "재고 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/scm-pp")
public class OrderController {

    private final WebClientProvider webClientProvider;
    private final ImHttpService imHttpService;

    @GetMapping("/purchase-orders")
    public ResponseEntity<Object> getPurchaseOrders(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return imHttpService.getPurchaseOrders(status, page, size, startDate, endDate);
    }

    @GetMapping("/sales-orders")
    public ResponseEntity<Object> getSalesOrders(
            @RequestParam String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return imHttpService.getSalesOrders(status, page, size);
    }

    @GetMapping("/sales-orders/{salesOrderId}")
    public ResponseEntity<Object> getSalesOrder(@PathVariable String salesOrderId) {
        return imHttpService.getSalesOrder(salesOrderId);
    }


    // 판매제품
    @GetMapping("/product-options")
    public ResponseEntity<Object> getItemCategoryProducts() {
        var client = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = client.get()
                    .uri("/scm-pp/product-options")
                    .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();

            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    //출고 배송 상태 변경
    @PostMapping("/sales-orders/{salesOrderId}/shipments")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "출고 배송 상태 변경",
            description = "출고 준비 완료 상태를 배송중 상태로 변경합니다."
    )
    public ResponseEntity<Void> createShipment(
            @PathVariable String salesOrderId,
            @RequestBody SalesOrderStatusChangeRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return imHttpService.createShipment(salesOrderId, requestDto, principal.getUserId());
    }
}
