package org.ever._4ever_be_scm.scm.iv.controller;

import org.ever._4ever_be_scm.api.scm.iv.IvStaticApi;
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

@RestController
@RequestMapping("/scm-pp/iv")
@RequiredArgsConstructor
public class IvStaticController implements IvStaticApi {

    private final IvStatisticService ivStatisticService;

    /**
     * 재고 부족 관리 통계 API
     *
     * @return 재고 부족 통계 정보
     */
    @GetMapping("/shortage-metrics")

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

    public ResponseEntity<WarehouseStatisticResponseDto> getWarehouseStatistic() {
        WarehouseStatisticResponseDto statistics = ivStatisticService.getWarehouseStatistic();

        return ResponseEntity.ok(statistics);
    }
}
