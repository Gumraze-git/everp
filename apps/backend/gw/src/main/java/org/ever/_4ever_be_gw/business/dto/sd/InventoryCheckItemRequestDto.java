package org.ever._4ever_be_gw.business.dto.sd;

import lombok.Getter;

@Getter
public class InventoryCheckItemRequestDto {
    private String itemId;             // 품목 ID
    private String itemName;           // 품목명
    private Integer requiredQuantity;  // 필요 수량 (> 0)
}
