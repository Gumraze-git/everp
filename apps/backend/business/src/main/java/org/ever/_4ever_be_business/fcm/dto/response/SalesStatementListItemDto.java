package org.ever._4ever_be_business.fcm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatementListItemDto {
    private String invoiceId;
    private String invoiceCode;
    private SalesStatementConnectionDto connection;
    private BigDecimal totalAmount;
    private String issueDate;
    private String dueDate;
    private String status;
    private String referenceCode;
}
