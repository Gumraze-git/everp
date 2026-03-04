package org.ever._4ever_be_gw.business.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private String itemId;       // 제품 Id
    private String itemName;     // 제품 이름
    private Integer quantity;    // 수량
    private String uonName;      // 단위
    private Long unitPrice;      // 단위가격
    private Long amount;         // 수량 * 단위가격
}

