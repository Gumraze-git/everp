package org.ever._4ever_be_scm.api.scm.iv;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.iv.dto.*;
import org.ever._4ever_be_scm.scm.iv.service.WarehouseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "재고관리", description = "재고 관리 API")
@ApiServerErrorResponse
public interface WarehouseApi {

    @Operation(summary = "창고 목록 조회")
    public ResponseEntity<PagedResponseDto<WarehouseDto>> getWarehouses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "창고 상세 조회")
    public ResponseEntity<WarehouseDetailDto> getWarehouseDetail(@PathVariable String warehouseId);

    @Operation(summary = "창고 정보 수정")
    public ResponseEntity<Void> updateWarehouse(
            @PathVariable String warehouseId,
            @RequestBody WarehouseUpdateRequestDto request);

    @Operation(summary = "창고 드롭다운 목록 조회")
    public ResponseEntity<WarehouseDropdownResponseDto> getWarehouseDropdown(
            @RequestParam(required = false) String warehouseId);

}
