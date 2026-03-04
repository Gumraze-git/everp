package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.sd.dao.SalesAnalyticsDAO;
import org.ever._4ever_be_business.sd.dto.response.*;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.ever._4ever_be_business.sd.integration.port.ProductServicePort;
import org.ever._4ever_be_business.sd.service.SalesAnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesAnalyticsServiceImpl implements SalesAnalyticsService {

    private final SalesAnalyticsDAO salesAnalyticsDAO;
    private final ProductServicePort productServicePort;

    @Override
    @Transactional(readOnly = true)
    public SalesAnalyticsDto getSalesAnalytics(LocalDate startDate, LocalDate endDate) {
        log.info("매출 분석 통계 조회 - startDate: {}, endDate: {}", startDate, endDate);

        // 1. Period 정보 계산
        PeriodInfoDto period = calculatePeriodInfo(startDate, endDate);

        // 2. 주차별 트렌드 조회
        List<TrendDto> trend = salesAnalyticsDAO.findWeeklyTrend(startDate, endDate);

        // 3. 총 매출액과 총 주문 수 계산
        BigDecimal totalSale = trend.stream()
                .map(TrendDto::getSale)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long totalOrders = trend.stream()
                .mapToLong(TrendDto::getOrderCount)
                .sum();

        // 4. 트렌드 스케일 계산
        TrendScaleDto trendScale = calculateTrendScale(trend);

        // 5. 제품별 매출 점유율 계산
        List<ProductShareDto> productShare = calculateProductShare(startDate, endDate);

        // 6. TOP 고객사 조회
        List<TopCustomerDto> topCustomers = calculateTopCustomers(startDate, endDate);

        log.info("매출 분석 통계 조회 완료 - totalSale: {}, totalOrders: {}, trend 개수: {}, productShare 개수: {}, topCustomers 개수: {}",
                totalSale, totalOrders, trend.size(), productShare.size(), topCustomers.size());

        return new SalesAnalyticsDto(period, totalSale, totalOrders, trend, trendScale, productShare, topCustomers);
    }

    /**
     * Period 정보 계산 (주차의 시작/종료일, 주차 수)
     */
    private PeriodInfoDto calculatePeriodInfo(LocalDate startDate, LocalDate endDate) {
        // 주차의 시작일: startDate가 속한 주의 월요일
        LocalDate weekStart = startDate.with(DayOfWeek.MONDAY);

        // 주차의 종료일: endDate가 속한 주의 일요일
        LocalDate weekEnd = endDate.with(DayOfWeek.SUNDAY);

        // 주차 수 계산
        long weekCount = ChronoUnit.WEEKS.between(weekStart, weekEnd) + 1;

        return new PeriodInfoDto(
                startDate.toString(),
                endDate.toString(),
                weekStart.toString(),
                weekEnd.toString(),
                (int) weekCount
        );
    }

    /**
     * 트렌드 스케일 계산 (min, max)
     */
    private TrendScaleDto calculateTrendScale(List<TrendDto> trend) {
        if (trend.isEmpty()) {
            return new TrendScaleDto(
                    new TrendScaleDto.ScaleDto(BigDecimal.ZERO, BigDecimal.ZERO),
                    new TrendScaleDto.ScaleDto(BigDecimal.ZERO, BigDecimal.ZERO)
            );
        }

        BigDecimal minSale = trend.stream()
                .map(TrendDto::getSale)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxSale = trend.stream()
                .map(TrendDto::getSale)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal minOrderCount = trend.stream()
                .map(TrendDto::getOrderCount)
                .map(BigDecimal::valueOf)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal maxOrderCount = trend.stream()
                .map(TrendDto::getOrderCount)
                .map(BigDecimal::valueOf)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        return new TrendScaleDto(
                new TrendScaleDto.ScaleDto(minSale, maxSale),
                new TrendScaleDto.ScaleDto(minOrderCount, maxOrderCount)
        );
    }

    /**
     * 제품별 매출 점유율 계산 (상위 5개 + ETC)
     */
    private List<ProductShareDto> calculateProductShare(LocalDate startDate, LocalDate endDate) {
        // 1. Product ID별 매출액 조회
        Map<String, BigDecimal> productSales = salesAnalyticsDAO.findProductSales(startDate, endDate);

        if (productSales.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 전체 매출액 계산
        BigDecimal totalSales = productSales.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. SCM 서비스에서 Product 정보 조회
        List<String> productIds = new ArrayList<>(productSales.keySet());
        ProductInfoResponseDto productInfo = productServicePort.getProductsByIds(productIds);

        // Product ID -> Product 정보 매핑
        Map<String, ProductInfoResponseDto.ProductDto> productMap = productInfo.getProducts().stream()
                .collect(Collectors.toMap(
                        ProductInfoResponseDto.ProductDto::getProductId,
                        p -> p
                ));

        // 4. 매출액 내림차순으로 정렬된 ProductShareDto 생성
        List<ProductShareDto> allProductShares = productSales.entrySet().stream()
                .map(entry -> {
                    String productId = entry.getKey();
                    BigDecimal sale = entry.getValue();
                    ProductInfoResponseDto.ProductDto product = productMap.get(productId);

                    // 점유율 계산 (%)
                    int saleShare = totalSales.compareTo(BigDecimal.ZERO) > 0
                            ? sale.multiply(BigDecimal.valueOf(100))
                                .divide(totalSales, 0, RoundingMode.HALF_UP)
                                .intValue()
                            : 0;

                    return new ProductShareDto(
                            product != null ? product.getProductCode() : "UNKNOWN",
                            product != null ? product.getProductName() : "Unknown Product",
                            sale,
                            saleShare
                    );
                })
                .sorted((a, b) -> b.getSale().compareTo(a.getSale())) // 매출액 내림차순
                .collect(Collectors.toList());

        // 5. 상위 5개 제품만 추출하고 나머지는 ETC로 묶기
        List<ProductShareDto> result = new ArrayList<>();

        if (allProductShares.size() <= 5) {
            // 5개 이하면 모두 반환
            return allProductShares;
        }

        // 상위 5개 추가
        result.addAll(allProductShares.subList(0, 5));

        // 나머지를 ETC로 묶기
        BigDecimal etcSale = allProductShares.subList(5, allProductShares.size()).stream()
                .map(ProductShareDto::getSale)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int etcShare = allProductShares.subList(5, allProductShares.size()).stream()
                .mapToInt(ProductShareDto::getSaleShare)
                .sum();

        result.add(new ProductShareDto("ETC", "etc", etcSale, etcShare));

        return result;
    }

    /**
     * TOP 고객사 계산
     */
    private List<TopCustomerDto> calculateTopCustomers(LocalDate startDate, LocalDate endDate) {
        // TOP 10 고객사 조회
        List<Object[]> topCustomersData = salesAnalyticsDAO.findTopCustomers(startDate, endDate, 10);

        return topCustomersData.stream()
                .map(row -> new TopCustomerDto(
                        (String) row[0],      // customerNumber (companyCode)
                        (String) row[1],      // customerName
                        (Long) row[2],        // orderCount
                        (BigDecimal) row[3],  // sale
                        (Boolean) row[4]      // isActive (from database)
                ))
                .collect(Collectors.toList());
    }
}
