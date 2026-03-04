package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.dto.response.HRStatisticsResponseDto;
import org.ever._4ever_be_business.hr.dto.response.PeriodStatisticsDto;
import org.ever._4ever_be_business.hr.dto.response.StatisticValueDto;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.service.StatisticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public HRStatisticsResponseDto getHRStatistics() {
        log.info("HR 대시보드 통계 조회 요청");

        LocalDateTime now = LocalDateTime.now();

        // Week statistics
        PeriodStatisticsDto weekStats = calculatePeriodStatistics(now.minusWeeks(1), now, now.minusWeeks(2), now.minusWeeks(1));

        // Month statistics
        PeriodStatisticsDto monthStats = calculatePeriodStatistics(now.minusMonths(1), now, now.minusMonths(2), now.minusMonths(1));

        // Quarter statistics
        PeriodStatisticsDto quarterStats = calculatePeriodStatistics(now.minusMonths(3), now, now.minusMonths(6), now.minusMonths(3));

        // Year statistics
        PeriodStatisticsDto yearStats = calculatePeriodStatistics(now.minusYears(1), now, now.minusYears(2), now.minusYears(1));

        HRStatisticsResponseDto response = new HRStatisticsResponseDto(weekStats, monthStats, quarterStats, yearStats);

        log.info("HR 대시보드 통계 조회 성공");

        return response;
    }

    private PeriodStatisticsDto calculatePeriodStatistics(
            LocalDateTime currentStart, LocalDateTime currentEnd,
            LocalDateTime previousStart, LocalDateTime previousEnd) {

        // Current period counts
        long currentTotalEmployees = employeeRepository.count();
        long currentNewEmployees = employeeRepository.countByCreatedAtBetween(currentStart, currentEnd);

        // Previous period counts (for delta calculation)
        long previousTotalEmployees = employeeRepository.countByCreatedAtBefore(currentStart);
        long previousNewEmployees = employeeRepository.countByCreatedAtBetween(previousStart, previousEnd);

        // Calculate absolute deltas (not percentages)
        int totalEmployeeDelta = calculateAbsoluteDelta(previousTotalEmployees, currentTotalEmployees);
        int newEmployeeDelta = calculateAbsoluteDelta(previousNewEmployees, currentNewEmployees);

        return new PeriodStatisticsDto(
                new StatisticValueDto((int) currentTotalEmployees, totalEmployeeDelta),
                new StatisticValueDto((int) currentNewEmployees, newEmployeeDelta)
        );
    }

    /**
     * Calculate absolute difference (not percentage)
     * Example: if current = 15, previous = 12, returns 3
     * Example: if current = 10, previous = 12, returns -2
     */
    private int calculateAbsoluteDelta(long previous, long current) {
        return (int) (current - previous);
    }
}
