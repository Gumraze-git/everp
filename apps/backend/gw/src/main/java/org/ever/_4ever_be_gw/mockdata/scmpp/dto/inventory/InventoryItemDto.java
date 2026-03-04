package org.ever._4ever_be_gw.mockdata.scmpp.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItemDto {
    private String itemId;
    private String itemNumber;
    private String itemName;
    private String category;
    private int currentStock;
    private int safetyStock;
    private String uomName;
    private int unitPrice;
    private int totalAmount;
    private String warehouseName;
    private String warehouseType;
    private String statusCode;
}
