package org.ever._4ever_be_scm.scm.pp.controller;

import org.ever._4ever_be_scm.api.scm.pp.PpStatisticsApi;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.pp.dto.PpStatisticsResponseDto;
import org.ever._4ever_be_scm.scm.pp.service.PpStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * PP 통계 관리 컨트롤러
 */

@RestController
@RequestMapping("/scm-pp/pp")
@RequiredArgsConstructor
public class PpStatisticsController implements PpStatisticsApi {

    private final PpStatisticsService ppStatisticsService;

    /**
     * PP 통계 조회 API
     *
     * @return 생산 관련 통계 정보
     */
    @GetMapping("/metrics")

    public ResponseEntity<PpStatisticsResponseDto> getPpStatistic() {
        PpStatisticsResponseDto statistics = ppStatisticsService.getPpStatistics();

        return ResponseEntity.ok(statistics);
    }
}
