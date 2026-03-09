package org.ever._4ever_be_scm.api.scm.pp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.BomCreateRequestDto;
import org.ever._4ever_be_scm.scm.pp.dto.BomDetailResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.BomListResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.ProductDetailResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.ProductMapResponseDto;
import org.ever._4ever_be_scm.scm.pp.service.BomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "생산관리", description = "생산 관리 API")
@ApiServerErrorResponse
public interface BomApi {

    public ResponseEntity<Void> createBom(@RequestBody BomCreateRequestDto requestDto);

    public ResponseEntity<BomDetailResponseDto> getBomDetail(@PathVariable String bomId);

    public ResponseEntity<Void> updateBom(@PathVariable String bomId, @RequestBody BomCreateRequestDto requestDto);

    @Operation(summary = "Product 상세 정보 조회", description = "Product의 상세 정보(id, 이름, 타입, 제품코드, 단위, 단가, 공급업체명)를 조회합니다.")
    public ResponseEntity<ProductDetailResponseDto> getProductDetail(
            
            @PathVariable String productId);

    @Operation(summary = "Operation ID-이름 맵 조회", description = "Operation의 ID를 key, opName을 value로 하는 맵 형태로 전체 목록을 조회합니다.")
    public ResponseEntity<List<ProductMapResponseDto>> getOperationMap();

}
