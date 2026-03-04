package org.ever._4ever_be_gw.business.dto.invoice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesInvoiceItemDto {
    private Long itemId;       // 품목 ID
    private String itemName;   // 품목명
    private int quantity;      // 수량
    private String uomName;    // 단위
    private long unitPrice;    // 단가
    private long amount;       // 금액
}
