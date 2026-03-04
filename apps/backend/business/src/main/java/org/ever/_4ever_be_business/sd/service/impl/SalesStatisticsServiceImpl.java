package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.sd.dao.SalesStatisticsDAO;
import org.ever._4ever_be_business.sd.dto.response.DashboardStatisticsDto;
import org.ever._4ever_be_business.sd.dto.response.PeriodStatisticsDto;
import org.ever._4ever_be_business.sd.dto.response.SalesStatisticsDto;
import org.ever._4ever_be_business.sd.dto.response.StatisticsValueDto;
import org.ever._4ever_be_business.sd.service.SalesStatisticsService;
import org.ever._4ever_be_business.sd.vo.StatisticsSearchConditionVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesStatisticsServiceImpl implements SalesStatisticsService {

    private final SalesStatisticsDAO salesStatisticsDAO;

    @Override
    @Transactional(readOnly = true)
    public SalesStatisticsDto getSalesStatistics(StatisticsSearchConditionVo vo) {
        LocalDate startDate = vo.getStartDate();
        LocalDate endDate = vo.getEndDate();

        // 1. 현재 기간 통계 조회
        BigDecimal currentSalesAmount = salesStatisticsDAO.calculateTotalSalesAmount(startDate, endDate);
        Long currentOrdersCount = salesStatisticsDAO.calculateNewOrdersCount(startDate, endDate);

        // 2. 이전 기간 통계 조회 (delta_rate 계산용)
        LocalDate previousStartDate = calculatePreviousStartDate(startDate, endDate);
        LocalDate previousEndDate = startDate != null ? startDate.minusDays(1) : null;

        BigDecimal previousSalesAmount = salesStatisticsDAO.calculateTotalSalesAmount(previousStartDate, previousEndDate);
        Long previousOrdersCount = salesStatisticsDAO.calculateNewOrdersCount(previousStartDate, previousEndDate);

        // 3. delta_rate 계산
        Double salesAmountDeltaRate = calculateDeltaRate(
                currentSalesAmount,
                previousSalesAmount
        );

        Double ordersCountDeltaRate = calculateDeltaRate(
                BigDecimal.valueOf(currentOrdersCount),
                BigDecimal.valueOf(previousOrdersCount)
        );

        // 4. DTO 생성
        StatisticsValueDto salesAmountDto = new StatisticsValueDto(currentSalesAmount, salesAmountDeltaRate);
        StatisticsValueDto newOrdersCountDto = new StatisticsValueDto(
                BigDecimal.valueOf(currentOrdersCount),
                ordersCountDeltaRate
        );

        return new SalesStatisticsDto(salesAmountDto, newOrdersCountDto);
    }

    /**
     * 이전 기간 시작일 계산
     */
    private LocalDate calculatePreviousStartDate(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return null;
        }

        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return startDate.minusDays(daysBetween);
    }

    /**
     * 증감량 계산: 현재 - 이전 (절대값 차이)
     * 예: 현재 2500000, 이전 2300000 -> delta_rate = 200000
     * 예: 현재 2000000, 이전 2300000 -> delta_rate = -300000
     */
    private Double calculateDeltaRate(BigDecimal current, BigDecimal previous) {
        if (previous == null) {
            return current != null ? current.doubleValue() : 0.0;
        }

        BigDecimal delta = current.subtract(previous);
        return delta.doubleValue();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatisticsDto getPeriodStatistics() {
        log.info("기간별 매출 통계 조회 시작");

        LocalDate today = LocalDate.now();

        // 1. 주간 통계 (최근 7일)
        LocalDate weekStart = today.minusDays(6);
        PeriodStatisticsDto weekStats = calculatePeriodStatistics(weekStart, today, 7);

        // 2. 월간 통계 (최근 30일)
        LocalDate monthStart = today.minusDays(29);
        PeriodStatisticsDto monthStats = calculatePeriodStatistics(monthStart, today, 30);

        // 3. 분기 통계 (최근 90일)
        LocalDate quarterStart = today.minusDays(89);
        PeriodStatisticsDto quarterStats = calculatePeriodStatistics(quarterStart, today, 90);

        // 4. 연간 통계 (최근 365일)
        LocalDate yearStart = today.minusDays(364);
        PeriodStatisticsDto yearStats = calculatePeriodStatistics(yearStart, today, 365);

        log.info("기간별 매출 통계 조회 완료");

        return new DashboardStatisticsDto(weekStats, monthStats, quarterStats, yearStats);
    }

    /**
     * 특정 기간의 통계 계산
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @param periodDays 기간 일수
     * @return PeriodStatisticsDto
     */
    private PeriodStatisticsDto calculatePeriodStatistics(LocalDate startDate, LocalDate endDate, int periodDays) {
        // 1. 현재 기간 통계 조회
        BigDecimal currentSalesAmount = salesStatisticsDAO.calculateTotalSalesAmount(startDate, endDate);
        Long currentOrdersCount = salesStatisticsDAO.calculateNewOrdersCount(startDate, endDate);

        // 2. 이전 기간 통계 조회 (delta_rate 계산용)
        LocalDate previousStartDate = startDate.minusDays(periodDays);
        LocalDate previousEndDate = startDate.minusDays(1);

        BigDecimal previousSalesAmount = salesStatisticsDAO.calculateTotalSalesAmount(previousStartDate, previousEndDate);
        Long previousOrdersCount = salesStatisticsDAO.calculateNewOrdersCount(previousStartDate, previousEndDate);

        // 3. delta_rate 계산
        Double salesAmountDeltaRate = calculateDeltaRate(currentSalesAmount, previousSalesAmount);
        Double ordersCountDeltaRate = calculateDeltaRate(
                BigDecimal.valueOf(currentOrdersCount),
                BigDecimal.valueOf(previousOrdersCount)
        );

        // 4. DTO 생성
        StatisticsValueDto salesAmountDto = new StatisticsValueDto(currentSalesAmount, salesAmountDeltaRate);
        StatisticsValueDto newOrdersCountDto = new StatisticsValueDto(
                BigDecimal.valueOf(currentOrdersCount),
                ordersCountDeltaRate
        );

        return new PeriodStatisticsDto(salesAmountDto, newOrdersCountDto);
    }
}
