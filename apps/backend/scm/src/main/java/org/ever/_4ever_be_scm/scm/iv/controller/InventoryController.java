package org.ever._4ever_be_scm.scm.iv.controller;

import org.ever._4ever_be_scm.api.scm.iv.InventoryApi;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.iv.dto.InventoryItemDetailDto;
import org.ever._4ever_be_scm.scm.iv.dto.InventoryItemDto;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.ShortageItemDto;
import org.ever._4ever_be_scm.scm.iv.dto.ShortageItemPreviewDto;
import org.ever._4ever_be_scm.scm.iv.dto.request.AddInventoryItemRequest;
import org.ever._4ever_be_scm.scm.iv.dto.request.ItemInfoRequest;
import org.ever._4ever_be_scm.scm.iv.dto.response.ItemInfoResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.response.ItemToggleResponseDto;
import org.ever._4ever_be_scm.scm.iv.service.InventoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/scm-pp/iv")
@RequiredArgsConstructor
public class InventoryController implements InventoryApi {

    private final InventoryService inventoryService;

    /**
     * 재고 목록 조회 API
     *
     * @param type 검색 타입: WAREHOUSE_NAME 또는 ITEM_NAME
     * @param keyword 검색 키워드
     * @param statusCode 재고 상태: ALL, NORMAL, CAUTION, URGENT
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 재고 목록
     */
    @GetMapping("/inventory-items")

    public ResponseEntity<Object> getInventoryItems(

            @RequestParam(name = "type", required = false) String type,

            @RequestParam(name = "keyword", required = false) String keyword,

            @RequestParam(name = "statusCode", required = false, defaultValue = "ALL") String statusCode,

            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,

            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    ) {

        PagedResponseDto<InventoryItemDto> response = inventoryService.getInventoryItemsWithFilters(type, keyword, statusCode, page, size);

        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("content", response.getContent());
        result.put("page", response.getPage());

        return ResponseEntity.ok(result);
    }

    /**
     * 재고 추가 API
     *
     * @param request 재고 추가 요청
     * @return 추가 결과
     */
    @PostMapping("/items")

    public ResponseEntity<Void> addInventoryItem(
            @RequestBody AddInventoryItemRequest request) {

        inventoryService.addInventoryItem(request);

        return ResponseEntity.noContent().build();
    }

    /**
     * 안전재고 수정 API
     *
     * @param itemId 제품 ID
     * @param safetyStock 안전재고 수량
     * @return 수정 결과
     */
    @PatchMapping("/items/{itemId}/safety-stock")

    public ResponseEntity<Void> updateSafetyStock(
            @PathVariable String itemId,
            @RequestParam Integer safetyStock) {

        inventoryService.updateSafetyStock(itemId, safetyStock);

        return ResponseEntity.noContent().build();
    }

    /**
     * 재고 상세 정보 조회 API
     *
     * @param itemId 재고 ID
     * @return 재고 상세 정보
     */
    @GetMapping("/items/{itemId}")

    public ResponseEntity<InventoryItemDetailDto> getInventoryItemDetail(@PathVariable String itemId) {
        InventoryItemDetailDto itemDetail = inventoryService.getInventoryItemDetail(itemId);

        return ResponseEntity.ok(itemDetail);
    }

    /**
     * 부족 재고 목록 조회 API
     *
     * @param status 재고 상태 필터
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 부족 재고 목록
     */
    @GetMapping("/shortage")

    public ResponseEntity<PagedResponseDto<ShortageItemDto>> getShortageItems(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ShortageItemDto> items = inventoryService.getShortageItems(status, PageRequest.of(page, size));
        PagedResponseDto<ShortageItemDto> response = PagedResponseDto.from(items);

        return ResponseEntity.ok(response);
    }

    /**
     * 부족 재고 간단 정보 조회 API
     *
     * @return 부족 재고 간단 정보 목록
     */
    @GetMapping("/shortage/preview")

    public ResponseEntity<PagedResponseDto<ShortageItemPreviewDto>> getShortageItemsPreview() {
        Page<ShortageItemPreviewDto> items = inventoryService.getShortageItemsPreview(PageRequest.of(0, 5));
        PagedResponseDto<ShortageItemPreviewDto> response = PagedResponseDto.from(items);

        return ResponseEntity.ok(response);
    }

    /**
     * 자재 품목 조회 토글목록 API
     *
     * @return 재고에 존재하지 않는 자재 품목 목록
     */
    @GetMapping("/items/toggle")

    public ResponseEntity<java.util.List<ItemToggleResponseDto>> getItemToggleList() {
        java.util.List<ItemToggleResponseDto> items = inventoryService.getItemToggleList();

        return ResponseEntity.ok(items);
    }

    /**
     * 제품 정보 목록 조회 API
     *
     * @param request 제품 ID 목록 요청
     * @return 제품 정보 목록
     */
    @PostMapping("/items/info")

    public ResponseEntity<java.util.List<ItemInfoResponseDto>> getItemInfoList(
            @RequestBody ItemInfoRequest request) {

        java.util.List<ItemInfoResponseDto> items = inventoryService.getItemInfoList(request.getItemIds());

        return ResponseEntity.ok(items);
    }
}
