package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuotationDetailDto {
    private String quotationId;
    private String quotationNumber;  // was quotationCode
    private String quotationDate;
    private String dueDate;
    private String statusCode;
    private String customerName;
    private String ceoName;          // NEW: CEO name
    private List<QuotationItemDto> items;
    private BigDecimal totalAmount;
}
