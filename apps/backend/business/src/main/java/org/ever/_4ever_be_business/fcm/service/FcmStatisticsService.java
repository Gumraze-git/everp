package org.ever._4ever_be_business.fcm.service;

import org.ever._4ever_be_business.fcm.dto.response.FcmStatisticsDto;
import org.ever._4ever_be_business.fcm.dto.response.TotalAmountDto;

public interface FcmStatisticsService {
    /**
     * 재무관리 통계 조회 (주/월/분기/년)
     *
     * @return 재무관리 통계
     */
    FcmStatisticsDto getFcmStatistics();

    /**
     * 공급사별 총 매출 금액 조회 (PurchaseVoucher 기준)
     *
     * @param supplierUserId 공급사 사용자 ID
     * @return 총 매출 금액
     */
    TotalAmountDto getTotalPurchaseAmountBySupplierUserId(String supplierUserId);

    /**
     * 고객사별 총 매입 금액 조회 (SalesVoucher 기준)
     *
     * @param customerUserId 고객사 사용자 ID
     * @return 총 매입 금액
     */
    TotalAmountDto getTotalSalesAmountByCustomerUserId(String customerUserId);
}
