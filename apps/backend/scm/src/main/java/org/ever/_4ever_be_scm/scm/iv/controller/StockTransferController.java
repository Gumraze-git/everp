package org.ever._4ever_be_scm.scm.iv.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.StockDeliveryRequestDto;
import org.ever._4ever_be_scm.scm.iv.dto.StockTransferDto;
import org.ever._4ever_be_scm.scm.iv.dto.StockTransferRequestDto;
import org.ever._4ever_be_scm.scm.iv.service.StockTransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 재고 이동 관리 컨트롤러
 */
@Tag(name = "재고관리", description = "재고 관리 API")
@RestController
@RequestMapping("/scm-pp/iv")
@RequiredArgsConstructor
public class StockTransferController {

    private final StockTransferService stockTransferService;
    
    /**
     * 재고 이동 목록 조회 API
     *
     * @return 재고 이동 목록
     */
    @GetMapping("/stock-transfers")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "재고 이동 목록 조회 (상위 5개)"
    )
    public ResponseEntity<PagedResponseDto<StockTransferDto>> getStockTransfers() {
        PageRequest pageable = PageRequest.of(0, 5); // 항상 첫 페이지에서 5개만 조회
        Page<StockTransferDto> stockTransfers = stockTransferService.getStockTransfers(pageable);
        PagedResponseDto<StockTransferDto> response = PagedResponseDto.from(stockTransfers);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 창고간 재고 이동 생성 API
     *
     * @param request 재고 이동 요청 정보
     * @return 재고 이동 결과
     */
    @PostMapping("/stock-transfers")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "창고간 재고 이동"
    )
    public ResponseEntity<Void> createStockTransfer(
            @RequestBody StockTransferRequestDto request,
            @RequestParam String requesterId
    ) {
        stockTransferService.createStockTransfer(request,requesterId);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 재고 입출고 처리 API
     *
     * @param request 재고 입출고 요청 정보
     * @return 재고 입출고 결과
     */
    @PostMapping("/stock-transfers/deliver")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "재고 입출고 처리"
    )
    public ResponseEntity<Void> processStockDelivery(@RequestBody StockDeliveryRequestDto request) {
//        stockTransferService.processStockDelivery(request);
        return ResponseEntity.noContent().build();
    }
}
