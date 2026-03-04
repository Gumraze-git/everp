package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationItemDto {
    private String itemId;          // 제품 id
    private String itemName;        // 제품 이름
    private int quantity;           // 수량
    private String uomName;         // 단위(unit of material name)
    private long unitPrice;         // 단위 가격
    private long amount;            // 총액
}
