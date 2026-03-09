package org.ever._4ever_be_scm.api.scm.iv;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
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

@Tag(name = "재고관리", description = "재고 관리 API")
@ApiServerErrorResponse
public interface InventoryApi {

    @Operation(summary = "재고 목록 조회", description = "재고 목록을 조회합니다. 타입(WAREHOUSE_NAME, ITEM_NAME)과 키워드로 검색, 상태코드(ALL, NORMAL, CAUTION, URGENT) 필터링 가능")
    public ResponseEntity<Object> getInventoryItems(
            
            @RequestParam(name = "type", required = false) String type,
            
            @RequestParam(name = "keyword", required = false) String keyword,
            
            @RequestParam(name = "statusCode", required = false, defaultValue = "ALL") String statusCode,
            
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    );

    @Operation(summary = "재고 추가", description = "product 테이블에는 존재하지만 productStock에 존재하지 않는 제품을 productStock에 추가합니다.")
    public ResponseEntity<Void> addInventoryItem(
            @RequestBody AddInventoryItemRequest request);

    @Operation(summary = "안전재고 수정", description = "itemId(productId)를 받아서 productStock의 safetyStock값을 변경합니다.")
    public ResponseEntity<Void> updateSafetyStock(
            @PathVariable String itemId,
            @RequestParam Integer safetyStock);

    @Operation(summary = "재고 상세 조회")
    public ResponseEntity<InventoryItemDetailDto> getInventoryItemDetail(@PathVariable String itemId);

    @Operation(summary = "부족 재고 간단 조회")
    public ResponseEntity<PagedResponseDto<ShortageItemPreviewDto>> getShortageItemsPreview();

    @Operation(summary = "제품 정보 목록 조회", description = "제품 ID 배열을 받아서 해당 제품들의 itemName, itemCode, unitPrice, supplierName을 반환합니다.")
    public ResponseEntity<java.util.List<ItemInfoResponseDto>> getItemInfoList(
            @RequestBody ItemInfoRequest request);

}
