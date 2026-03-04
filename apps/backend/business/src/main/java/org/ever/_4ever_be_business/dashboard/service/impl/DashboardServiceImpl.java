package org.ever._4ever_be_business.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.util.DateRangeCalculator;
import org.ever._4ever_be_business.dashboard.dto.response.DashboardPeriodStatisticsDto;
import org.ever._4ever_be_business.dashboard.dto.response.DashboardStatisticsResponseDto;
import org.ever._4ever_be_business.dashboard.dto.response.DashboardValueDto;
import org.ever._4ever_be_business.dashboard.service.DashboardService;
import org.ever._4ever_be_business.fcm.dao.FcmStatisticsDAO;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.sd.dao.DashboardStatisticsDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardStatisticsDAO salesStatisticsDAO;
    private final FcmStatisticsDAO fcmStatisticsDAO;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsResponseDto getDashboardStatistics() {
        LocalDate today = LocalDate.now();
        log.info("종합 대시보드 통계 조회 요청 - 기준일: {}", today);

        // Calculate statistics for each period
        DashboardPeriodStatisticsDto weekStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.WEEK);
        DashboardPeriodStatisticsDto monthStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.MONTH);
        DashboardPeriodStatisticsDto quarterStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.QUARTER);
        DashboardPeriodStatisticsDto yearStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.YEAR);

        log.info("종합 대시보드 통계 조회 완료");

        return new DashboardStatisticsResponseDto(weekStats, monthStats, quarterStats, yearStats);
    }

    /**
     * Calculate statistics for a specific period
     */
    private DashboardPeriodStatisticsDto calculatePeriodStatistics(DateRangeCalculator.PeriodType periodType) {
        Map<String, LocalDate[]> dateRanges = DateRangeCalculator.getDateRanges(periodType);

        // Extract current and previous periods
        LocalDate[] currentPeriod = getCurrentPeriod(dateRanges, periodType);
        LocalDate[] previousPeriod = getPreviousPeriod(dateRanges, periodType);

        // === Current Period Data ===

        // Sales (from SD module)
        BigDecimal currentSales = salesStatisticsDAO.calculateSalesAmount(
                currentPeriod[0], currentPeriod[1]
        );
        if (currentSales == null) currentSales = BigDecimal.ZERO;

        // Purchases (from FCM module)
        BigDecimal currentPurchases = fcmStatisticsDAO.calculateTotalPurchases(
                currentPeriod[0], currentPeriod[1]
        );
        if (currentPurchases == null) currentPurchases = BigDecimal.ZERO;

        // Net profit
        BigDecimal currentProfit = currentSales.subtract(currentPurchases);

        // Employee count (from HR module) - accumulated total as of end date
        long currentEmployeeCount = employeeRepository.countByCreatedAtBefore(
                currentPeriod[1].plusDays(1).atStartOfDay()
        );

        // === Previous Period Data ===

        // Sales
        BigDecimal previousSales = salesStatisticsDAO.calculateSalesAmount(
                previousPeriod[0], previousPeriod[1]
        );
        if (previousSales == null) previousSales = BigDecimal.ZERO;

        // Purchases
        BigDecimal previousPurchases = fcmStatisticsDAO.calculateTotalPurchases(
                previousPeriod[0], previousPeriod[1]
        );
        if (previousPurchases == null) previousPurchases = BigDecimal.ZERO;

        // Net profit
        BigDecimal previousProfit = previousSales.subtract(previousPurchases);

        // Employee count - accumulated total as of previous end date
        long previousEmployeeCount = employeeRepository.countByCreatedAtBefore(
                previousPeriod[1].plusDays(1).atStartOfDay()
        );

        // === Calculate absolute deltas (not percentages) ===

        BigDecimal salesDelta = currentSales.subtract(previousSales);
        BigDecimal purchasesDelta = currentPurchases.subtract(previousPurchases);
        BigDecimal profitDelta = currentProfit.subtract(previousProfit);
        BigDecimal employeeCountDelta = BigDecimal.valueOf(currentEmployeeCount - previousEmployeeCount);

        // === Build DTOs ===

        DashboardValueDto totalSales = new DashboardValueDto(currentSales, salesDelta);
        DashboardValueDto totalPurchases = new DashboardValueDto(currentPurchases, purchasesDelta);
        DashboardValueDto netProfit = new DashboardValueDto(currentProfit, profitDelta);
        DashboardValueDto totalEmployees = new DashboardValueDto(
                BigDecimal.valueOf(currentEmployeeCount),
                employeeCountDelta
        );

        return new DashboardPeriodStatisticsDto(
                totalSales,
                totalPurchases,
                netProfit,
                totalEmployees
        );
    }

    /**
     * Extract current period from date ranges
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
     * Extract previous period from date ranges
     */
    private LocalDate[] getPreviousPeriod(Map<String, LocalDate[]> dateRanges, DateRangeCalculator.PeriodType periodType) {
        return switch (periodType) {
            case WEEK -> dateRanges.get("lastWeek");
            case MONTH -> dateRanges.get("lastMonth");
            case QUARTER -> dateRanges.get("lastQuarter");
            case YEAR -> dateRanges.get("lastYear");
        };
    }
}
