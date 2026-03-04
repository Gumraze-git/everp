package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDto {
    private String itemId;
    private String itemName;
    private Long quantity;
    private String uonName;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}
