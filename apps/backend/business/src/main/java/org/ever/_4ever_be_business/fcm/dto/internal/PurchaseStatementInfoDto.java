package org.ever._4ever_be_business.fcm.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 매입전표 기본 정보 (Service 레이어에서 사용)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseStatementInfoDto {
    private String invoiceId;
    private String invoiceCode;
    private String statusCode;
    private String issueDate;
    private String dueDate;
    private String supplierCompanyId;
    private String productOrderId;
    private BigDecimal totalAmount;
    private String note;
}
