package org.ever._4ever_be_scm.scm.iv.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.iv.dto.response.ImStatisticResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.response.ShortageStatisticResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.response.WarehouseStatisticResponseDto;
import org.ever._4ever_be_scm.scm.iv.service.IvStatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * IV 통계 관리 컨트롤러
 */
@Tag(name = "재고관리", description = "재고 관리 통계 API")
@RestController
@RequestMapping("/scm-pp/iv")
@RequiredArgsConstructor
public class IvStaticController {
    
    private final IvStatisticService ivStatisticService;
    
    /**
     * 재고 부족 관리 통계 API
     * 
     * @return 재고 부족 통계 정보
     */
    @GetMapping("/shortage-metrics")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "재고 부족 통계 조회",
            description = "재고 부족 관리 통계 정보를 반환합니다. URGENT는 total_emergency, CAUTION은 total_warning으로 분류"
    )
    public ResponseEntity<ShortageStatisticResponseDto> getShortageStatistic() {
        ShortageStatisticResponseDto statistics = ivStatisticService.getShortageStatistic();
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * IM 통계 조회 API
     * 
     * @return 재고 및 입출고 현황 통계
     */
    @GetMapping("/metrics")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "IM 통계 조회",
            description = "재고관리의 통계를 반환합니다. 총 재고 가치, 입고대기/완료, 출고대기/완료 현황 포함"
    )
    public ResponseEntity<ImStatisticResponseDto> getImStatistic() {
        ImStatisticResponseDto statistics = ivStatisticService.getImStatistic();
        
        return ResponseEntity.ok(statistics);
    }
    
    /**
     * 창고관리 통계 API
     * 
     * @return 창고 현황 통계
     */
    @GetMapping("/warehouse-metrics")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "창고 통계 조회",
            description = "창고 관리 통계를 반환합니다. 총 창고 개수와 운영중인 창고 개수 포함"
    )
    public ResponseEntity<WarehouseStatisticResponseDto> getWarehouseStatistic() {
        WarehouseStatisticResponseDto statistics = ivStatisticService.getWarehouseStatistic();
        
        return ResponseEntity.ok(statistics);
    }
}
