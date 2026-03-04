package org.ever._4ever_be_business.sd.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.sd.dao.DashboardStatisticsDAO;
import org.ever._4ever_be_business.sd.repository.DashboardStatisticsRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DashboardStatisticsDAOImpl implements DashboardStatisticsDAO {

    private final DashboardStatisticsRepository dashboardStatisticsRepository;

    @Override
    public BigDecimal calculateSalesAmount(LocalDate startDate, LocalDate endDate) {
        return dashboardStatisticsRepository.calculateSalesAmount(startDate, endDate);
    }

    @Override
    public Long calculateNewOrdersCount(LocalDate startDate, LocalDate endDate) {
        return dashboardStatisticsRepository.calculateNewOrdersCount(startDate, endDate);
    }
}
