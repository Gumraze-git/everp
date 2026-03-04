package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuotationListItemDto {
    private String quotationId;
    private String quotationNumber;  // was quotationCode
    private String customerName;
    private String managerName;
    private String quotationDate;
    private String productId;        // 첫 번째 견적 아이템의 제품 ID (UUID)
    private BigDecimal totalAmount;
    private String dueDate;
    private Long quantity;           // 첫 번째 견적 아이템의 수량
    private String uomName;          // 첫 번째 견적 아이템의 단위명
    private String statusCode;
}
