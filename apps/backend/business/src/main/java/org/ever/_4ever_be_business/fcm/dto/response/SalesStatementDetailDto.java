package org.ever._4ever_be_business.fcm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatementDetailDto {
    private String invoiceId;
    private String invoiceCode;
    private String invoiceType;
    private String statusCode;
    private String issueDate;
    private String dueDate;
    private String name;
    private String referenceCode;
    private List<SalesStatementItemDto> items;
    private BigDecimal totalAmount;
    private String note;
}
