package org.ever._4ever_be_business.fcm.service;

import java.util.List;

public interface VoucherStatusService {
    /**
     * 바우처 상태를 수동으로 업데이트합니다.
     *
     * @param voucherId 바우처 ID
     * @param statusCode 새로운 상태 코드
     */
    void updateVoucherStatus(String voucherId, String statusCode);

    /**
     * 모든 바우처의 상태를 지급 기한 기준으로 자동 업데이트합니다.
     * 스케줄러에서 호출됩니다.
     */
    void updateAllVoucherStatusesByDueDate();

    /**
     * 매출 전표(SalesVoucher)의 상태를 RESPONSE_PENDING으로 일괄 업데이트합니다.
     *
     * @param invoiceIds 업데이트할 매출 전표 ID 목록
     */
    void updateSalesVouchersToResponsePending(List<String> invoiceIds);

    /**
     * 매입 전표(PurchaseVoucher)의 상태를 RESPONSE_PENDING으로 일괄 업데이트합니다.
     *
     * @param invoiceIds 업데이트할 매입 전표 ID 목록
     */
    void updatePurchaseVouchersToResponsePending(List<String> invoiceIds);
}
