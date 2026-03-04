package org.ever._4ever_be_gw.business.dto.sd;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckItemDto {
    private String itemId;              // 품목 ID
    private String itemName;            // 품목명
    private int requiredQuantity;       // 필요 수량
    private int inventoryQuantity;      // 현재 재고 수량
    private int shortageQuantity;       // 부족 수량(음수 없음)
    private String statusCode;          // FULFILLED | SHORTAGE
    private boolean productionRequired; // 생산 필요 여부
}
