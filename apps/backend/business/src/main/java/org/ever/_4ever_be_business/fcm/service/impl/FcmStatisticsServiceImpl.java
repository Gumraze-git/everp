package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.util.DateRangeCalculator;
import org.ever._4ever_be_business.fcm.dao.FcmStatisticsDAO;
import org.ever._4ever_be_business.fcm.dto.response.*;
import org.ever._4ever_be_business.fcm.integration.port.SupplierCompanyServicePort;
import org.ever._4ever_be_business.fcm.service.FcmStatisticsService;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.voucher.entity.PurchaseVoucher;
import org.ever._4ever_be_business.voucher.entity.SalesVoucher;
import org.ever._4ever_be_business.voucher.repository.PurchaseVoucherRepository;
import org.ever._4ever_be_business.voucher.repository.SalesVoucherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmStatisticsServiceImpl implements FcmStatisticsService {

    private final FcmStatisticsDAO fcmStatisticsDAO;
    private final PurchaseVoucherRepository purchaseVoucherRepository;
    private final SalesVoucherRepository salesVoucherRepository;
    private final SupplierCompanyServicePort supplierCompanyServicePort;
    private final CustomerUserRepository customerUserRepository;

    @Override
    @Transactional(readOnly = true)
    public FcmStatisticsDto getFcmStatistics() {
        LocalDate today = LocalDate.now();
        log.info("재무관리 통계 조회 요청 - 기준일: {}", today);

        // 주간 통계
        FcmPeriodStatisticsDto weekStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.WEEK);

        // 월간 통계
        FcmPeriodStatisticsDto monthStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.MONTH);

        // 분기 통계
        FcmPeriodStatisticsDto quarterStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.QUARTER);

        // 연간 통계
        FcmPeriodStatisticsDto yearStats = calculatePeriodStatistics(DateRangeCalculator.PeriodType.YEAR);

        log.info("재무관리 통계 조회 완료");

        return new FcmStatisticsDto(weekStats, monthStats, quarterStats, yearStats);
    }

    /**
     * 기간별 통계 계산
     */
    private FcmPeriodStatisticsDto calculatePeriodStatistics(DateRangeCalculator.PeriodType periodType) {
        Map<String, LocalDate[]> dateRanges = DateRangeCalculator.getDateRanges(periodType);

        // 현재 기간과 이전 기간 추출
        LocalDate[] currentPeriod = getCurrentPeriod(dateRanges, periodType);
        LocalDate[] previousPeriod = getPreviousPeriod(dateRanges, periodType);

        // 현재 기간 데이터
        BigDecimal currentTotalSales = fcmStatisticsDAO.calculateTotalSales(
                currentPeriod[0], currentPeriod[1]
        );
        BigDecimal currentTotalPurchases = fcmStatisticsDAO.calculateTotalPurchases(
                currentPeriod[0], currentPeriod[1]
        );
        // 순이익 = 총 매출 - 총 매입
        BigDecimal currentNetProfit = currentTotalSales.subtract(currentTotalPurchases);

        // 이전 기간 데이터
        BigDecimal previousTotalSales = fcmStatisticsDAO.calculateTotalSales(
                previousPeriod[0], previousPeriod[1]
        );
        BigDecimal previousTotalPurchases = fcmStatisticsDAO.calculateTotalPurchases(
                previousPeriod[0], previousPeriod[1]
        );
        // 이전 기간 순이익 = 총 매출 - 총 매입
        BigDecimal previousNetProfit = previousTotalSales.subtract(previousTotalPurchases);

        // 증감률 계산
        Double salesDeltaRate = calculateDeltaRate(currentTotalSales, previousTotalSales);
        Double purchasesDeltaRate = calculateDeltaRate(currentTotalPurchases, previousTotalPurchases);
        Double netProfitDeltaRate = calculateDeltaRate(currentNetProfit, previousNetProfit);

        return new FcmPeriodStatisticsDto(
                new FcmStatisticsValueDto(currentTotalPurchases, purchasesDeltaRate),
                new FcmStatisticsValueDto(currentNetProfit, netProfitDeltaRate),
                new FcmStatisticsValueDto(currentTotalSales, salesDeltaRate)
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

    @Override
    @Transactional(readOnly = true)
    public TotalAmountDto getTotalPurchaseAmountBySupplierUserId(String supplierUserId) {
        log.info("공급사별 총 매출 금액 조회 요청 - supplierUserId: {}", supplierUserId);

        // 1. SCM에서 supplierUserId로 supplierCompanyId 조회
        String supplierCompanyId = supplierCompanyServicePort.getSupplierCompanyIdByUserId(supplierUserId);
        log.info("공급사 ID 조회 성공 - supplierUserId: {}, supplierCompanyId: {}", supplierUserId, supplierCompanyId);

        // 2. 전체 PurchaseVoucher 조회 (해당 supplierCompanyId)
        List<PurchaseVoucher> allVouchers = purchaseVoucherRepository.findAll().stream()
                .filter(pv -> supplierCompanyId.equals(pv.getSupplierCompanyId()))
                .toList();

        // 3. 기간별 통계 계산
        PeriodTotalAmountDto weekStats = calculatePeriodTotalAmount(allVouchers, DateRangeCalculator.PeriodType.WEEK);
        PeriodTotalAmountDto monthStats = calculatePeriodTotalAmount(allVouchers, DateRangeCalculator.PeriodType.MONTH);
        PeriodTotalAmountDto quarterStats = calculatePeriodTotalAmount(allVouchers, DateRangeCalculator.PeriodType.QUARTER);
        PeriodTotalAmountDto yearStats = calculatePeriodTotalAmount(allVouchers, DateRangeCalculator.PeriodType.YEAR);

        log.info("공급사별 총 매출 금액 조회 성공 - supplierUserId: {}, total vouchers: {}",
                supplierUserId, allVouchers.size());

        return new TotalAmountDto(weekStats, monthStats, quarterStats, yearStats);
    }

    @Override
    @Transactional(readOnly = true)
    public TotalAmountDto getTotalSalesAmountByCustomerUserId(String customerUserId) {
        log.info("고객사별 총 매입 금액 조회 요청 - customerUserId: {}", customerUserId);

        // 1. CustomerUser 조회
        CustomerUser customerUser = customerUserRepository.findByUserId(customerUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));

        // 2. CustomerCompany ID 조회
        if (customerUser.getCustomerCompany() == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_COMPANY_NOT_FOUND);
        }
        String customerCompanyId = customerUser.getCustomerCompany().getId();
        log.info("고객사 ID 조회 성공 - customerUserId: {}, customerCompanyId: {}", customerUserId, customerCompanyId);

        // 3. 전체 SalesVoucher 조회 (해당 customerCompanyId)
        List<SalesVoucher> allVouchers = salesVoucherRepository.findAll().stream()
                .filter(sv -> sv.getCustomerCompany() != null && customerCompanyId.equals(sv.getCustomerCompany().getId()))
                .toList();

        // 4. 기간별 통계 계산
        PeriodTotalAmountDto weekStats = calculatePeriodTotalAmountForSales(allVouchers, DateRangeCalculator.PeriodType.WEEK);
        PeriodTotalAmountDto monthStats = calculatePeriodTotalAmountForSales(allVouchers, DateRangeCalculator.PeriodType.MONTH);
        PeriodTotalAmountDto quarterStats = calculatePeriodTotalAmountForSales(allVouchers, DateRangeCalculator.PeriodType.QUARTER);
        PeriodTotalAmountDto yearStats = calculatePeriodTotalAmountForSales(allVouchers, DateRangeCalculator.PeriodType.YEAR);

        log.info("고객사별 총 매입 금액 조회 성공 - customerUserId: {}, total vouchers: {}",
                customerUserId, allVouchers.size());

        return new TotalAmountDto(weekStats, monthStats, quarterStats, yearStats);
    }

    /**
     * PurchaseVoucher 기간별 총 금액 계산
     */
    private PeriodTotalAmountDto calculatePeriodTotalAmount(List<PurchaseVoucher> vouchers, DateRangeCalculator.PeriodType periodType) {
        Map<String, LocalDate[]> dateRanges = DateRangeCalculator.getDateRanges(periodType);

        LocalDate[] currentPeriod = getCurrentPeriod(dateRanges, periodType);
        LocalDate[] previousPeriod = getPreviousPeriod(dateRanges, periodType);

        // 현재 기간 금액 계산
        BigDecimal currentAmount = vouchers.stream()
                .filter(v -> v.getIssueDate() != null)
                .filter(v -> {
                    LocalDate issueDate = v.getIssueDate().toLocalDate();
                    return !issueDate.isBefore(currentPeriod[0]) && !issueDate.isAfter(currentPeriod[1]);
                })
                .map(PurchaseVoucher::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 이전 기간 금액 계산
        BigDecimal previousAmount = vouchers.stream()
                .filter(v -> v.getIssueDate() != null)
                .filter(v -> {
                    LocalDate issueDate = v.getIssueDate().toLocalDate();
                    return !issueDate.isBefore(previousPeriod[0]) && !issueDate.isAfter(previousPeriod[1]);
                })
                .map(PurchaseVoucher::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 증감률 계산
        Double deltaRate = currentAmount.subtract(previousAmount).doubleValue();

        return new PeriodTotalAmountDto(new FcmStatisticsValueDto(currentAmount, deltaRate));
    }

    /**
     * SalesVoucher 기간별 총 금액 계산
     */
    private PeriodTotalAmountDto calculatePeriodTotalAmountForSales(List<SalesVoucher> vouchers, DateRangeCalculator.PeriodType periodType) {
        Map<String, LocalDate[]> dateRanges = DateRangeCalculator.getDateRanges(periodType);

        LocalDate[] currentPeriod = getCurrentPeriod(dateRanges, periodType);
        LocalDate[] previousPeriod = getPreviousPeriod(dateRanges, periodType);

        // 현재 기간 금액 계산
        BigDecimal currentAmount = vouchers.stream()
                .filter(v -> v.getIssueDate() != null)
                .filter(v -> {
                    LocalDate issueDate = v.getIssueDate().toLocalDate();
                    return !issueDate.isBefore(currentPeriod[0]) && !issueDate.isAfter(currentPeriod[1]);
                })
                .map(SalesVoucher::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 이전 기간 금액 계산
        BigDecimal previousAmount = vouchers.stream()
                .filter(v -> v.getIssueDate() != null)
                .filter(v -> {
                    LocalDate issueDate = v.getIssueDate().toLocalDate();
                    return !issueDate.isBefore(previousPeriod[0]) && !issueDate.isAfter(previousPeriod[1]);
                })
                .map(SalesVoucher::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 증감률 계산
        Double deltaRate = currentAmount.subtract(previousAmount).doubleValue();

        return new PeriodTotalAmountDto(new FcmStatisticsValueDto(currentAmount, deltaRate));
    }
}
