package org.ever._4ever_be_gw.scm.im.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.scm.im.dto.SalesOrderStatusChangeRequestDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Tag(name = "재고관리(IM)", description = "재고 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/scm-pp")
public class OrderController {

    private final WebClientProvider webClientProvider;

    //입고 완료 목록 조회
    @GetMapping("/purchase-orders/received")
    public ResponseEntity<Object> getReceivedPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/purchase-orders/received")
                            .queryParam("startDate", startDate)
                            .queryParam("endDate", endDate)
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
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

    //  입고 대기 목록 조회
    @GetMapping("/purchase-orders/receiving")
    public ResponseEntity<Object> getReceivingPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/purchase-orders/receiving")
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
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

    //  생산중 목록 조회
    @GetMapping("/sales-orders/production")
    public ResponseEntity<Object> getSalesOrdersInProduction(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/sales-orders/production")
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
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

    //  출고 준비 완료 목록 조회
    @GetMapping("/sales-orders/ready-to-ship")
    public ResponseEntity<Object> getReadyToShipSalesOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/sales-orders/ready-to-ship")
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
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

    //  출고 준비 완료 상세 조회
    @GetMapping("/sales-orders/ready-to-ship/{salesOrderId}")
    public ResponseEntity<Object> getReadyToShipOrder(@PathVariable String salesOrderId) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri("/scm-pp/sales-orders/ready-to-ship/{salesOrderId}", salesOrderId)
                    .accept(MediaType.APPLICATION_JSON)
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

    //  생산중 상세 조회
    @GetMapping("/sales-orders/production/{salesOrderId}")
    public ResponseEntity<Object> getProductionOrder(@PathVariable String salesOrderId) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri("/scm-pp/sales-orders/production/{salesOrderId}", salesOrderId)
                    .accept(MediaType.APPLICATION_JSON)
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


    // 판매제품
    @GetMapping("/product/item/toggle")
    public ResponseEntity<Object> getItemCategoryProducts() {
        var client = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = client.get()
                    .uri("/scm-pp/product/item/toggle")
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
    @PatchMapping("/sales-orders/{salesOrderId}/status")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "출고 배송 상태 변경",
            description = "출고 준비 완료 상태를 배송중 상태로 변경합니다."
    )
    public ResponseEntity<Object> updateOrderStatus(
            @PathVariable String salesOrderId,
            @RequestBody SalesOrderStatusChangeRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        String requesterId = principal.getUserId();

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/sales-orders/{salesOrderId}/status")
                            .queryParam("requesterId", requesterId)

                            .build(salesOrderId))
                    .bodyValue(requestDto)
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
}
