package org.ever._4ever_be_business.sd.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.sd.dao.SalesAnalyticsDAO;
import org.ever._4ever_be_business.sd.dto.response.TrendDto;
import org.ever._4ever_be_business.sd.repository.SalesAnalyticsRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SalesAnalyticsDAOImpl implements SalesAnalyticsDAO {

    private final SalesAnalyticsRepository salesAnalyticsRepository;

    @Override
    public List<TrendDto> findWeeklyTrend(LocalDate startDate, LocalDate endDate) {
        return salesAnalyticsRepository.findWeeklyTrend(startDate, endDate);
    }

    @Override
    public Map<String, BigDecimal> findProductSales(LocalDate startDate, LocalDate endDate) {
        return salesAnalyticsRepository.findProductSales(startDate, endDate);
    }

    @Override
    public List<Object[]> findTopCustomers(LocalDate startDate, LocalDate endDate, int limit) {
        return salesAnalyticsRepository.findTopCustomers(startDate, endDate, limit);
    }
}
