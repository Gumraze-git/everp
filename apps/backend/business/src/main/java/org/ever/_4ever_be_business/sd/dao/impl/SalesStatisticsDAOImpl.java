package org.ever._4ever_be_business.sd.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.sd.dao.SalesStatisticsDAO;
import org.ever._4ever_be_business.sd.repository.SalesStatisticsRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SalesStatisticsDAOImpl implements SalesStatisticsDAO {

    private final SalesStatisticsRepository salesStatisticsRepository;

    @Override
    public BigDecimal calculateTotalSalesAmount(LocalDate startDate, LocalDate endDate) {
        return salesStatisticsRepository.calculateTotalSalesAmount(startDate, endDate);
    }

    @Override
    public Long calculateNewOrdersCount(LocalDate startDate, LocalDate endDate) {
        return salesStatisticsRepository.calculateNewOrdersCount(startDate, endDate);
    }
}
