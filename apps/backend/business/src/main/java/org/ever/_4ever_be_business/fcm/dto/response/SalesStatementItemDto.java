package org.ever._4ever_be_business.fcm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalesStatementItemDto {
    private String itemId;
    private String itemName;
    private Integer quantity;
    private String uomName;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
