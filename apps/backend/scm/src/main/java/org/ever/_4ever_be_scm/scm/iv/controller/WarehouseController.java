package org.ever._4ever_be_scm.scm.iv.controller;

import org.ever._4ever_be_scm.api.scm.iv.WarehouseApi;
import lombok.RequiredArgsConstructor;
import java.net.URI;
import org.ever._4ever_be_scm.scm.iv.dto.*;
import org.ever._4ever_be_scm.scm.iv.service.WarehouseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 창고 관리 컨트롤러
 */

@RestController
@RequestMapping("/scm-pp/iv")
@RequiredArgsConstructor
public class WarehouseController implements WarehouseApi {

    private final WarehouseService warehouseService;

    /**
     * 창고 목록 조회 API
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 창고 목록
     */
    @GetMapping("/warehouses")

    public ResponseEntity<PagedResponseDto<WarehouseDto>> getWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<WarehouseDto> warehouses = warehouseService.getWarehouses(PageRequest.of(page, size));
        PagedResponseDto<WarehouseDto> response = PagedResponseDto.from(warehouses);
        return ResponseEntity.ok(response);
    }

    /**
     * 창고 상세 정보 조회 API
     *
     * @param warehouseId 창고 ID
     * @return 창고 상세 정보
     */
    @GetMapping("/warehouses/{warehouseId}")

    public ResponseEntity<WarehouseDetailDto> getWarehouseDetail(@PathVariable String warehouseId) {
        WarehouseDetailDto warehouseDetail = warehouseService.getWarehouseDetail(warehouseId);
        return ResponseEntity.ok(warehouseDetail);
    }

    /**
     * 창고 추가 API
     *
     * @param request 창고 생성 요청 정보
     * @return 생성된 창고 정보
     */
    @PostMapping("/warehouses")

    public ResponseEntity<Void> createWarehouse(@RequestBody WarehouseCreateRequestDto request) {
        warehouseService.createWarehouse(request);
        return ResponseEntity.created(URI.create("/scm-pp/iv/warehouses")).build();
    }

    /**
     * 창고 정보 수정 API
     *
     * @param warehouseId 창고 ID
     * @param request 창고 수정 요청 정보
     * @return 수정 결과
     */
    @PutMapping("/warehouses/{warehouseId}")

    public ResponseEntity<Void> updateWarehouse(
            @PathVariable String warehouseId,
            @RequestBody WarehouseUpdateRequestDto request) {
        warehouseService.updateWarehouse(warehouseId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * 창고 드롭다운 목록 조회 API
     *
     * @param warehouseId 제외할 창고 ID (선택사항)
     * @return 창고 드롭다운 목록
     */
    @GetMapping("/warehouse-options")

    public ResponseEntity<WarehouseDropdownResponseDto> getWarehouseDropdown(
            @RequestParam(required = false) String warehouseId) {
        WarehouseDropdownResponseDto dropdown = warehouseService.getWarehouseDropdown(warehouseId);
        return ResponseEntity.ok(dropdown);
    }
}
