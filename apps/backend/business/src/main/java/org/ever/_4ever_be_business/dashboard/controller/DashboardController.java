package org.ever._4ever_be_business.dashboard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.ever._4ever_be_business.dashboard.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Get comprehensive dashboard statistics
     * Includes: total sales, purchases, profit, and employee count
     * Periods: week, month, quarter, year
     * Delta values are absolute differences (not percentages)
     *
     * Example response:
     * {
     *   "week": {
     *     "total_sales": { "value": 1000000, "delta": 50000 },
     *     "total_purchases": { "value": 600000, "delta": 30000 },
     *     "net_profit": { "value": 400000, "delta": 20000 },
     *     "total_employees": { "value": 123, "delta": 11 }
     *   },
     *   ...
     * }
     */
    @GetMapping("/statistics")
    public ApiResponse<DashboardStatisticsResponseDto> getDashboardStatistics() {
        log.info("종합 대시보드 통계 조회 API 호출");
        DashboardStatisticsResponseDto result = dashboardService.getDashboardStatistics();
        log.info("종합 대시보드 통계 조회 성공");
        return ApiResponse.success(result, "대시보드 통계를 조회했습니다.", HttpStatus.OK);
    }
}
