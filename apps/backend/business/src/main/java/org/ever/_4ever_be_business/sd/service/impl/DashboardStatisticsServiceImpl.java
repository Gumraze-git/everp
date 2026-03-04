package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.util.DateRangeCalculator;
import org.ever._4ever_be_business.sd.dao.DashboardStatisticsDAO;
import org.ever._4ever_be_business.sd.dto.response.DashboardStatisticsDto;
import org.ever._4ever_be_business.sd.dto.response.PeriodStatisticsDto;
import org.ever._4ever_be_business.sd.dto.response.StatisticsValueDto;
import org.ever._4ever_be_business.sd.service.DashboardStatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardStatisticsServiceImpl implements DashboardStatisticsService {

    private final DashboardStatisticsDAO dashboardStatisticsDAO;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsDto getDashboardStatistics() {
        LocalDate today = LocalDate.now();
        log.info("대시보드 통계 조회 요청 - 기준일: {}", today);

        // 주간 통계
        PeriodStatisticsDto weekStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.WEEK);

        // 월간 통계
        PeriodStatisticsDto monthStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.MONTH);

        // 분기 통계
        PeriodStatisticsDto quarterStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.QUARTER);

        // 연간 통계
        PeriodStatisticsDto yearStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.YEAR);

        log.info("대시보드 통계 조회 완료");

        return new DashboardStatisticsDto(weekStats, monthStats, quarterStats, yearStats);
    }

    /**
     * 기간별 통계 계산
     */
    private PeriodStatisticsDto calculatePeriodStatistics(DateRangeCalculator.PeriodType periodType) {
        Map<String, LocalDate[]> dateRanges = DateRangeCalculator.getDateRanges(periodType);

        // 현재 기간과 이전 기간 추출
        LocalDate[] currentPeriod = getCurrentPeriod(dateRanges, periodType);
        LocalDate[] previousPeriod = getPreviousPeriod(dateRanges, periodType);

        // 현재 기간 데이터
        BigDecimal currentSalesAmount = dashboardStatisticsDAO.calculateSalesAmount(
                currentPeriod[0], currentPeriod[1]
        );
        Long currentOrdersCount = dashboardStatisticsDAO.calculateNewOrdersCount(
                currentPeriod[0], currentPeriod[1]
        );

        // 이전 기간 데이터
        BigDecimal previousSalesAmount = dashboardStatisticsDAO.calculateSalesAmount(
                previousPeriod[0], previousPeriod[1]
        );
        Long previousOrdersCount = dashboardStatisticsDAO.calculateNewOrdersCount(
                previousPeriod[0], previousPeriod[1]
        );

        // 증감률 계산
        Double salesDeltaRate = calculateDeltaRate(currentSalesAmount, previousSalesAmount);
        Double ordersDeltaRate = calculateDeltaRate(
                BigDecimal.valueOf(currentOrdersCount),
                BigDecimal.valueOf(previousOrdersCount)
        );

        return new PeriodStatisticsDto(
                new StatisticsValueDto(currentSalesAmount, salesDeltaRate),
                new StatisticsValueDto(BigDecimal.valueOf(currentOrdersCount), ordersDeltaRate)
        );
    }

    /**
     * 현재 기간 추출
     */
    private LocalDate[] getCurrentPeriod(Map<String, LocalDate[]> dateRanges, DateRangeCalculator.PeriodType periodType) {
        return switch (periodType) {
            case WEEK -> dateRanges.get("thisWeek");
            case MONTH -> dateRanges.get("thisMonth");
            case QUARTER -> dateRanges.get("thisQuarter");
            case YEAR -> dateRanges.get("thisYear");
        };
    }

    /**
     * 이전 기간 추출
     */
    private LocalDate[] getPreviousPeriod(Map<String, LocalDate[]> dateRanges, DateRangeCalculator.PeriodType periodType) {
        return switch (periodType) {
            case WEEK -> dateRanges.get("lastWeek");
            case MONTH -> dateRanges.get("lastMonth");
            case QUARTER -> dateRanges.get("lastQuarter");
            case YEAR -> dateRanges.get("lastYear");
        };
    }

    /**
     * 증감률 계산: (현재 - 이전) / 이전
     */
    private Double calculateDeltaRate(BigDecimal current, BigDecimal previous) {

        BigDecimal delta = current.subtract(previous);

        return delta.doubleValue();
    }
}
