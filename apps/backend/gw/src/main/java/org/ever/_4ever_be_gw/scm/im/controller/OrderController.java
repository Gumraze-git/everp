package org.ever._4ever_be_gw.scm.im.controller;

import org.ever._4ever_be_gw.api.scm.im.OrderApi;
import lombok.RequiredArgsConstructor;
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


@RestController
@RequiredArgsConstructor
@RequestMapping("/scm-pp")
public class OrderController implements OrderApi {

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

    @GetMapping("/products/options")
    public ResponseEntity<Object> getItemCategoryProducts() {
        return imHttpService.getProductOptions();
    }

    @PostMapping("/sales-orders/{salesOrderId}/shipments")

    public ResponseEntity<Void> createShipment(
            @PathVariable String salesOrderId,
            @RequestBody SalesOrderStatusChangeRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return imHttpService.createShipment(salesOrderId, requestDto, principal.getUserId());
    }
}
