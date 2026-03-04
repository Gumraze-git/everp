package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuotationItemDto {
    private String itemId;
    private String itemName;     // was productName
    private Long quantity;
    private String uomName;      // NEW: Unit of Measure name
    private BigDecimal unitPrice;
    private BigDecimal amount;
}
