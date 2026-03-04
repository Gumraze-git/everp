package org.ever._4ever_be_gw.scm.im.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.scm.im.dto.AddInventoryItemRequest;
import org.ever._4ever_be_gw.scm.im.dto.StockTransferRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseCreateRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseUpdateRequestDto;
import org.ever._4ever_be_gw.scm.mm.dto.ItemInfoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Tag(name = "재고관리(IM)", description = "재고 관리 API")
@RestController
@RequestMapping("/scm-pp/iv")
@RequiredArgsConstructor
public class ImController {

    private final WebClientProvider webClientProvider;

    // 재고 목록 조회 (외부 서버)
    @GetMapping("/inventory-items")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "재고 목록 조회"
    )
    public ResponseEntity<Object> getInventoryItems(
            @io.swagger.v3.oas.annotations.Parameter(description = "검색 타입: WAREHOUSE_NAME 또는 ITEM_NAME")
            @RequestParam(name = "type", required = false) String type,
            @io.swagger.v3.oas.annotations.Parameter(description = "검색 키워드")
            @RequestParam(name = "keyword", required = false) String keyword,
            @io.swagger.v3.oas.annotations.Parameter(description = "재고 상태: ALL, NORMAL, CAUTION, URGENT")
            @RequestParam(name = "statusCode", required = false, defaultValue = "ALL") String statusCode,
            @io.swagger.v3.oas.annotations.Parameter(description = "페이지 번호")
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @io.swagger.v3.oas.annotations.Parameter(description = "페이지 크기")
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/inventory-items")
                            .queryParam("type", type)
                            .queryParam("keyword", keyword)
                            .queryParam("statusCode", statusCode)
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object) body));
                    })
                    .block();

            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // 재고 추가 (외부 서버)
    @PostMapping("/items")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "원자재 추가 (재고관리에서 사용) "
    )
    public ResponseEntity<Object> addInventoryItem(@RequestBody AddInventoryItemRequest request) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.post()
                    .uri("/scm-pp/iv/items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
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

    // 안전재고 수정 (외부 서버)
    @PatchMapping("/items/{itemId}/safety-stock")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "안전재고 수정"
    )
    public ResponseEntity<Object> updateSafetyStock(
            @PathVariable String itemId,
            @RequestParam Integer safetyStock
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);
        
        try {
            ResponseEntity<Object> result = scmPpWebClient.patch()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/items/{itemId}/safety-stock")
                            .queryParam("safetyStock", safetyStock)
                            .build(itemId))
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

    // 재고 상세 정보 조회 (외부 서버)
    @GetMapping("/items/{itemId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "재고 상세 조회"
    )
    public ResponseEntity<Object> getInventoryItemDetail(@PathVariable String itemId) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri("/scm-pp/iv/items/{itemId}", itemId)
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

    // 부족 재고 목록 조회 (외부 서버)
    @GetMapping("/shortage")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "부족 재고 목록 조회"
    )
    public ResponseEntity<Object> getShortageItems(
            @RequestParam(required = false) String statusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/shortage")
                            .queryParam("status", statusCode)
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

    // 부족 재고 간단 정보 조회 (외부 서버)
    @GetMapping("/shortage/preview")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "부족 재고 간단 조회"
    )
    public ResponseEntity<Object> getShortageItemsPreview() {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/shortage/preview")
                            .queryParam("page", 0)
                            .queryParam("size", 5)
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

    // 재고에 존재하지 않는 자재 품목 목록
    @GetMapping("/items/toggle")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "자재 추가 시 자재 토글 목록 조회"
    )
    public ResponseEntity<Object> getItemToggleList() {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/items/toggle")
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

    // 재고 이동 목록 조회 (외부 서버)
    @GetMapping("/stock-transfers")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "재고 이동 목록 조회 (상위 5개)"
    )
    public ResponseEntity<Object> getStockTransfers() {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/stock-transfers")
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

    // 창고간 재고 이동 생성 (외부 서버)
    @PostMapping("/stock-transfers")
    @PreAuthorize("hasAnyAuthority('IM_USER', 'IM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "창고간 재고 이동"
    )
    public ResponseEntity<Object> createStockTransfer(
            @RequestBody StockTransferRequestDto request,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {

        String requesterId = principal.getUserId();

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/stock-transfers")
                            .queryParam("requesterId", requesterId)
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
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

    // 창고 목록 조회 (외부 서버)
    @GetMapping("/warehouses")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "창고 목록 조회"
    )
    public ResponseEntity<Object> getWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/warehouses")
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

    // 창고 상세 정보 조회 (외부 서버)
    @GetMapping("/warehouses/{warehouseId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "창고 상세 조회"
    )
    public ResponseEntity<Object> getWarehouseDetail(@PathVariable String warehouseId) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri("/scm-pp/iv/warehouses/{warehouseId}", warehouseId)
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

    // 재고 관리 부서 명단 반환
    @GetMapping("/warehouses/managers/toggle")
    public ResponseEntity<Object> getInventoryEmployees() {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri("/hrm/departments/inventory/employees")
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

    // 창고 추가 생성 (외부 서버)
    @PostMapping("/warehouses")
    @PreAuthorize("hasAnyAuthority('IM_USER', 'IM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "창고 추가"
    )
    public ResponseEntity<Object> createWarehouse(
            @RequestBody WarehouseCreateRequestDto request
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.post()
                    .uri("/scm-pp/iv/warehouses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
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

    //재고성 구매요청을 위한 item 정보 get
    @PostMapping("/items/info")
    @PreAuthorize("hasAnyAuthority('IM_USER', 'IM_ADMIN', 'ALL_ADMIN')")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "재고성 구매요청을 위한 item 정보 get"
    )
    public ResponseEntity<Object> getItemInfoList(@RequestBody ItemInfoRequest request) {

        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);
        
        try {
            ResponseEntity<Object> result = scmPpWebClient.post()
                    .uri("/scm-pp/iv/items/info")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
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

    // 창고 정보 수정 수정 (외부 서버)
    @PutMapping("warehouses/{warehouseId}")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "창고 정보 수정"
    )
    public ResponseEntity<Object> updateWarehouse(
            @PathVariable String warehouseId,
            @RequestBody WarehouseUpdateRequestDto request
    ) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.put()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/warehouses/{warehouseId}")
                            .build(warehouseId))
                    .bodyValue(request)
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

    // 창고 드롭다운 목록 조회
    @GetMapping("/warehouses/dropdown")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "창고 드롭다운 목록 조회"
    )
    public ResponseEntity<Object> getWarehouseDropdown(@RequestParam(required = false) String warehouseId) {
        WebClient scmPpWebClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = scmPpWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/scm-pp/iv/warehouses/dropdown")
                            .queryParam("warehouseId", warehouseId)
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

     // 재고 부족 통계 조회
    @GetMapping("/shortage/count/critical/statistic")
    public ResponseEntity<Object> getShortageStatistic() {
        var client = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = client.get()
                    .uri("/scm-pp/iv/shortage/count/critical/statistic")
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

    // IM 통계 조회
    @GetMapping("/statistic")
    public ResponseEntity<Object> getImStatistic() {
        var client = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = client.get()
                    .uri("/scm-pp/iv/statistic")
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

    // 창고 통계 조회
    @GetMapping("/warehouses/statistic")
    public ResponseEntity<Object> getWarehouseStatistic() {
        var client = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        try {
            ResponseEntity<Object> result = client.get()
                    .uri("/scm-pp/iv/warehouses/statistic")
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