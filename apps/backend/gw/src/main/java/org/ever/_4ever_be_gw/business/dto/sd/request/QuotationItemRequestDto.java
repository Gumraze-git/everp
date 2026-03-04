package org.ever._4ever_be_gw.business.dto.sd.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuotationItemRequestDto {
    private String itemId;         // Product UUID
    private Long quantity;
    private BigDecimal unitPrice;
}
