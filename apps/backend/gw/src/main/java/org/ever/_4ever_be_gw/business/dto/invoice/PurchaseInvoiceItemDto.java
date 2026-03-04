package org.ever._4ever_be_gw.business.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseInvoiceItemDto {
    private String itemId;      // 품목 ID (UUID)
    private String itemName;    // 품목명
    private Integer quantity;   // 수량
    private String unitOfMaterialName;     // 단위
    private Long unitPrice;     // 단가
    private Long totalPrice;    // 금액(수량*단가)
}

