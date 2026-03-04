package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QuotationItemRequestDto {
    private String itemId;          // UUID
    private Integer quantity;       // 수량
    private Long unitPrice;         // 단위가격
}
