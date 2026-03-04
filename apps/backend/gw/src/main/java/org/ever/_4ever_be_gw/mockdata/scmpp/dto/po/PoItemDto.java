package org.ever._4ever_be_gw.mockdata.scmpp.dto.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoItemDto {
    private String itemId;
    private String itemName;
    private int quantity;
    private String uomName;
    private long unitPrice;
    private long totalPrice;
}
