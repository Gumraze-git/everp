package org.ever._4ever_be_gw.scm.mm.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.common.dto.ValueKeyOptionDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.scm.mm.dto.*;
import org.ever._4ever_be_gw.scm.mm.service.MmHttpService;
import org.ever._4ever_be_gw.scm.mm.service.MmService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "구매관리(MM)", description = "구매 관리 API")
@RestController
@RequestMapping("/scm-pp/mm")
@RequiredArgsConstructor
public class MmController {

    private final WebClientProvider webClientProvider;
    private final MmService mmService;
    private final MmHttpService mmHttpService;

    // 공급업체 목록 조회
    @GetMapping("/suppliers")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "공급업체 목록 조회"
    )
    public ResponseEntity<Object> getSupplierList(
            @io.swagger.v3.oas.annotations.Parameter(description = "상태코드 (ALL, ACTIVE, INACTIVE)")
            @RequestParam(defaultValue = "ALL") String statusCode,
            @io.swagger.v3.oas.annotations.Parameter(description = "카테고리 (ALL, MATERIAL, ITEM, ETC)")
            @RequestParam(defaultValue = "ALL") String category,
            @io.swagger.v3.oas.annotations.Parameter(description = "검색 타입 (SupplierCompanyNumber, SupplierCompanyName)")
            @RequestParam(required = false) String type,
            @io.swagger.v3.oas.annotations.Parameter(description = "검색 키워드")
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/mm/suppliers")
                            .queryParam("statusCode", statusCode)
                            .queryParam("category", category)
                            .queryParam("type", type)
                            .queryParam("keyword", keyword)
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

    // 공급업체 상세 조회
    @GetMapping("/suppliers/{supplierId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "공급업체 상세 조회"
    )
    public ResponseEntity<Object> getSupplierDetail(@PathVariable String supplierId) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .get()
                    .uri("/scm-pp/mm/suppliers/{supplierId}", supplierId)
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

    // 공급업체 등록 (SAGA)
    @PostMapping("/suppliers")
    @PreAuthorize("hasAnyAuthority('MM_USER', 'MM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "공급업체 등록(추가)"
    )
    public Mono<ResponseEntity<CreateAuthUserResultDto>> createSupplier(
        @RequestBody SupplierCreateRequestDto requestDto
    ) {
        return mmService.createSupplier(requestDto)
            .map(ResponseEntity::ok);
    }

    @PatchMapping("/suppliers/{supplierId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "공급업체 수정"
    )
    public ResponseEntity<Object> updateSupplier(
            @PathVariable String supplierId,
            @RequestBody SupplierUpdateRequestDto requestDto
    ) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .patch()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/mm/suppliers/{supplierId}")
                            .build(supplierId))
                    .contentType(MediaType.APPLICATION_JSON)
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


    // 재고성 구매요청 생성
    @PostMapping("/stock-purchase-requisitions")
    @PreAuthorize("hasAnyAuthority('MM_USER', 'MM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "재고성 구매 요청 등록"
    )
    public ResponseEntity<Object> createStockPurchaseRequest(
            @RequestBody StockPurchaseRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal requester
    ) {

        String requesterId = requester.getUserId();

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/mm/stock-purchase-requisitions")
                            .queryParam("requesterId", requesterId)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
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

    // 구매요청서 목록 조회
    @GetMapping("/purchase-requisitions")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "구매요청 목록 조회"
    )
    public ResponseEntity<Object> getPurchaseRequisitionList(
            @io.swagger.v3.oas.annotations.Parameter(
                    description = "상태 코드 (ALL: 전체, APPROVAL: 승인, PENDING: 대기, REJECTED: 반려)"
            )
            @RequestParam(defaultValue = "ALL") String statusCode,
            @io.swagger.v3.oas.annotations.Parameter(description = "검색 타입 (requesterName, departmentName, productRequestNumber)")
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/mm/purchase-requisitions")
                            .queryParam("statusCode", statusCode)
                            .queryParam("type", type)
                            .queryParam("keyword", keyword)
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

    // 구매요청서 상세 조회
    @GetMapping("/purchase-requisitions/{purchaseRequisitionId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "구매요청 상세 조회"
    )
    public ResponseEntity<Object> getPurchaseRequisitionDetail(@PathVariable String purchaseRequisitionId) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .get()
                    .uri("/scm-pp/mm/purchase-requisitions/{purchaseRequisitionId}", purchaseRequisitionId)
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

    // 구매요청서 생성
    @PostMapping("/purchase-requisitions")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "구매요청 생성 (비재고성)"
    )
    public ResponseEntity<Object> createPurchaseRequisition(
            @RequestBody PurchaseRequisitionCreateRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal principal
            ) {

        String requestId = principal.getUserId();

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/mm/purchase-requisitions")
                            .queryParam("requestId", requestId)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
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

    // 구매요청서 승인
    @PostMapping("/purchase-requisitions/{purchaseRequisitionId}/approvals")
    @PreAuthorize("hasAnyAuthority('MM_USER', 'MM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "구매요청 승인"
    )
    public ResponseEntity<Object> approvePurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        String requesterId = principal.getUserId();

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/mm/purchase-requisitions/{purchaseRequisitionId}/approvals")
                            .queryParam("requesterId", requesterId)
                            .build(purchaseRequisitionId))
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


    // 구매요청서 반려
    @PostMapping("/purchase-requisitions/{purchaseRequisitionId}/rejections")
    @PreAuthorize("hasAnyAuthority('MM_USER', 'MM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "구매요청 반려"
    )
    public ResponseEntity<Object> rejectPurchaseRequisition(
            @PathVariable String purchaseRequisitionId,
            @AuthenticationPrincipal EverUserPrincipal principal,
            @RequestBody PurchaseRequisitionRejectRequestDto requestDto
    ) {
        String requesterId = principal.getUserId();

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/mm/purchase-requisitions/{purchaseRequisitionId}/rejections")
                            .queryParam("requesterId", requesterId)
                            .build(purchaseRequisitionId))
                    .contentType(MediaType.APPLICATION_JSON)
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


    // 발주서 목록 조회
    @GetMapping("/purchase-orders")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "발주서 목록 조회"
    )
    public ResponseEntity<Object> getPurchaseOrderList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {

        String userId = principal.getUserId();
        String userType = principal.getUserType();

        // 최종 URI 결정 (람다에서 사용 가능하도록 final 로 유지)
        final String path = ("SUPPLIER".equalsIgnoreCase(userType))
                ? "/scm-pp/mm/purchase-orders/supplier/" + userId
                : "/scm-pp/mm/purchase-orders";

        try {
            return webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam("statusCode", statusCode)
                            .queryParam("type", type)
                            .queryParam("keyword", keyword)
                            .queryParam("startDate", startDate)
                            .queryParam("endDate", endDate)
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(response ->
                            response.bodyToMono(String.class)
                                    .map(body -> ResponseEntity.status(response.statusCode())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body((Object) body))
                    )
                    .block();

        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }


    // 발주서 상세 조회
    @GetMapping("/purchase-orders/{purchaseOrderId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "발주서 상세 조회"
    )
    public ResponseEntity<Object> getPurchaseOrderDetail(@PathVariable String purchaseOrderId) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/mm/purchase-orders/{purchaseOrderId}", purchaseOrderId)
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

    // 발주서 승인
    @PostMapping("/purchase-orders/{purchaseOrderId}/approvals")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "발주서 승인"
    )
    public ResponseEntity<Object> approvePurchaseOrder(
            @PathVariable String purchaseOrderId,
            @AuthenticationPrincipal EverUserPrincipal principal
            ) {
        String requesterId = principal.getUserId();

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/mm/purchase-orders/{purchaseOrderId}/approvals")
                        .queryParam("requesterId", requesterId)
                        .build(purchaseOrderId))
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

    // 발주서 반려
    @PostMapping("/purchase-orders/{purchaseOrderId}/rejections")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "발주서 반려"
    )
    public ResponseEntity<Object> rejectPurchaseOrder(
            @PathVariable String purchaseOrderId,
            @RequestBody PurchaseOrderRejectRequestDto requestDto,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        String requesterId = principal.getUserId();

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/mm/purchase-orders/{purchaseOrderId}/rejections")
                        .queryParam("requesterId", requesterId)
                        .build(purchaseOrderId))
                .contentType(MediaType.APPLICATION_JSON)
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


    // MM 통계 조회
    @GetMapping("/metrics")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "MM 통계 조회"
    )
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getMMStatistics() {
        return mmHttpService.getMetrics();
    }

    @GetMapping("/supplier-users/me/metrics/order-counts")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "공급사 발주서 통계"
    )
    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getSupplierStatistics(
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return mmHttpService.getSupplierOrderMetrics(principal.getUserId());
    }

    // 배송 시작
    @PostMapping("/purchase-orders/{purchaseOrderId}/delivery-starts")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "배송 시작"
    )
    public ResponseEntity<Object> startDelivery(
            @PathVariable String purchaseOrderId
    ) {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/mm/purchase-orders/{purchaseOrderId}/delivery-starts")
                        .queryParam("requesterId", purchaseOrderId)
                        .build(purchaseOrderId))
                .contentType(MediaType.APPLICATION_JSON)
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

    // 입고 시작
    @PostMapping("/purchase-orders/{purchaseOrderId}/delivery-completions")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "입고 완료"
    )
    public ResponseEntity<Object> completeDelivery(
            @PathVariable String purchaseOrderId
    ) {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/mm/purchase-orders/{purchaseOrderId}/delivery-completions")
                        .queryParam("requesterId", purchaseOrderId)
                        .build(purchaseOrderId))
                .contentType(MediaType.APPLICATION_JSON)
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



    // 구매요청서 상태 토글
    @GetMapping("/purchase-requisition-status-options")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "구매요청서 상태 토글"
    )
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseRequisitionStatusOptions() {
        return mmHttpService.getPurchaseRequisitionStatusOptions();
    }

    // 발주서 상태 옵션
    @GetMapping("/purchase-order-status-options")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "발주서 상태 토글"
    )
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseOrderStatusOptions() {
        return mmHttpService.getPurchaseOrderStatusOptions();
    }

    // 공급업체 상태 옵션
    @GetMapping("/supplier-status-options")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "공급업체 상태 토글"
    )
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getSupplierStatusOptions() {
        return mmHttpService.getSupplierStatusOptions();
    }

    // 공급업체 카테고리 옵션
    @GetMapping("/supplier-category-options")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "공급업체 카테고리 토글"
    )
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getSupplierCategoryOptions() {
        return mmHttpService.getSupplierCategoryOptions();
    }

    // 구매 요청 검색 타입 옵션
    @GetMapping("/purchase-requisition-search-type-options")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "구매 요청 검색 타입 토글"
    )
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseRequisitionSearchTypeOptions() {
        return mmHttpService.getPurchaseRequisitionSearchTypeOptions();
    }

    // 발주서 검색 타입 옵션
    @GetMapping("/purchase-order-search-type-options")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "발주서 검색 타입 토글"
    )
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getPurchaseOrderSearchTypeOptions() {
        return mmHttpService.getPurchaseOrderSearchTypeOptions();
    }

    // 공급업체 검색 타입 옵션
    @GetMapping("/supplier-search-type-options")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "공급업체 검색 타입 토글"
    )
    public ResponseEntity<java.util.List<ValueKeyOptionDto>> getSupplierSearchTypeOptions() {
        return mmHttpService.getSupplierSearchTypeOptions();
    }

}
