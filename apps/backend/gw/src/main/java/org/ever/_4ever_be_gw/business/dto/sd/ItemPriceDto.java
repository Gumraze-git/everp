package org.ever._4ever_be_gw.business.dto.sd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPriceDto {         // 제품명, 단가를 보내주는
    private String itemId;         // 제품 ID
    private String itemNumber;     // 제품 코드 (예: PRD-001)
    private String itemName;       // 제품명
    private String uomName;        // 단위 (예: EA, SET)
    private Long unitPrice;        // 단가 (판매 기준)
}

