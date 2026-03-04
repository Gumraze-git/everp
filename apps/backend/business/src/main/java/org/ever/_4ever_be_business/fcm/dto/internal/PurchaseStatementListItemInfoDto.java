package org.ever._4ever_be_business.fcm.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 매입전표 목록 아이템 기본 정보 (Service 레이어에서 사용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseStatementListItemInfoDto {
    private String invoiceId;
    private String invoiceCode;
    private String supplierCompanyId;
    private String issueDate;
    private String dueDate;
    private String status;
    private String productOrderId;
}
