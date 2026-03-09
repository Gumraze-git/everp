package org.ever._4ever_be_gw.api.scm.im;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.scm.im.dto.AddInventoryItemRequest;
import org.ever._4ever_be_gw.scm.im.dto.StockTransferRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseCreateRequestDto;
import org.ever._4ever_be_gw.scm.im.dto.WarehouseUpdateRequestDto;
import org.ever._4ever_be_gw.scm.im.service.ImHttpService;
import org.ever._4ever_be_gw.scm.mm.dto.ItemInfoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재고관리(IM)", description = "재고 관리 API")
@ApiServerErrorResponse
public interface ImApi {

    @Operation(summary = "재고 목록 조회")
    public ResponseEntity<Object> getInventoryItems(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "statusCode", required = false, defaultValue = "ALL") String statusCode,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
    );

    @Operation(summary = "원자재 추가 (재고관리에서 사용) ")
    public ResponseEntity<Object> addInventoryItem(@RequestBody AddInventoryItemRequest request);

    @Operation(summary = "안전재고 수정")
    public ResponseEntity<Object> updateSafetyStock(
            @PathVariable String itemId,
            @RequestParam Integer safetyStock
    );

    @Operation(summary = "재고 상세 조회")
    public ResponseEntity<Object> getInventoryItemDetail(@PathVariable String itemId);

    @Operation(summary = "부족 재고 간단 조회")
    public ResponseEntity<Object> getShortageItemsPreview();

    @Operation(summary = "재고 이동 목록 조회 (상위 5개)")
    public ResponseEntity<Object> getStockTransfers();

    @Operation(summary = "창고 목록 조회")
    public ResponseEntity<Object> getWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "창고 상세 조회")
    public ResponseEntity<Object> getWarehouseDetail(@PathVariable String warehouseId);

    @Operation(summary = "창고 추가")
    public ResponseEntity<Object> createWarehouse(@RequestBody WarehouseCreateRequestDto request);

    @Operation(summary = "창고 정보 수정")
    public ResponseEntity<Object> updateWarehouse(
            @PathVariable String warehouseId,
            @RequestBody WarehouseUpdateRequestDto request
    );

    @Operation(summary = "창고 드롭다운 목록 조회")
    public ResponseEntity<Object> getWarehouseDropdown(@RequestParam(required = false) String warehouseId);

    public ResponseEntity<StatsResponseDto<StatsMetricsDto>> getImStatistic();

}
