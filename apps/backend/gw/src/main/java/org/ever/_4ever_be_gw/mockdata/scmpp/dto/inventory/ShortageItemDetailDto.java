package org.ever._4ever_be_gw.mockdata.scmpp.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortageItemDetailDto {
    private String itemId;
    private String itemName;
    private String itemNumber;
    private String category;
    private int currentStock;
    private String uomName;
    private int safetyStock;
    private int unitPrice;
    private int totalAmount;
    private String warehouseName;
    private String warehouseNumber;
    private String statusCode;
}
