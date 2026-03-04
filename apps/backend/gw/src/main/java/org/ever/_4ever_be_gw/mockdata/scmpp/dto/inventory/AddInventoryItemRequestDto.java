package org.ever._4ever_be_gw.mockdata.scmpp.dto.inventory;

import lombok.Data;

@Data
public class AddInventoryItemRequestDto {
    private String itemId;
    private Integer safetyStock;
    private Integer currentStock;
    private String warehouseId;
}