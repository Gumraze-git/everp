package org.ever._4ever_be_scm.scm.pp.controller;

import org.ever._4ever_be_scm.api.scm.pp.BomApi;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.BomDetailResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.BomListResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.BomCreateRequestDto;
import org.ever._4ever_be_scm.scm.pp.dto.ProductMapResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.ProductDetailResponseDto;
import org.ever._4ever_be_scm.scm.pp.service.BomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/scm-pp/pp/boms")
@RequiredArgsConstructor
public class BomController implements BomApi {
    private final BomService bomService;

    @PostMapping
    public ResponseEntity<Void> createBom(@RequestBody BomCreateRequestDto requestDto) {
        bomService.createBom(requestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<PagedResponseDto<BomListResponseDto>> getBomList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<BomListResponseDto> bomList = bomService.getBomList(PageRequest.of(page, size));
        PagedResponseDto<BomListResponseDto> response = PagedResponseDto.from(bomList);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{bomId}")
    public ResponseEntity<BomDetailResponseDto> getBomDetail(@PathVariable String bomId) {
        BomDetailResponseDto detail = bomService.getBomDetail(bomId);
        return ResponseEntity.ok(detail);
    }

    @PatchMapping("/{bomId}")
    public ResponseEntity<Void> updateBom(@PathVariable String bomId, @RequestBody BomCreateRequestDto requestDto) {
        bomService.updateBom(bomId, requestDto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Product ID와 이름 맵 조회 (전체 목록)
     */
    @GetMapping("/products")

    public ResponseEntity<List<ProductMapResponseDto>> getProductMap() {

        List<ProductMapResponseDto> productMapList = bomService.getProductMap();

        return ResponseEntity.ok(productMapList);
    }

    /**
     * Product 상세 정보 조회
     */
    @GetMapping("/products/{productId}")

    public ResponseEntity<ProductDetailResponseDto> getProductDetail(

            @PathVariable String productId) {

        ProductDetailResponseDto detail = bomService.getProductDetail(productId);

        return ResponseEntity.ok(detail);
    }

    /**
     * Operation ID와 이름 맵 조회 (전체 목록)
     */
    @GetMapping("/operations")

    public ResponseEntity<List<ProductMapResponseDto>> getOperationMap() {

        List<ProductMapResponseDto> operationMapList = bomService.getOperationMap();

        return ResponseEntity.ok(operationMapList);
    }

}
