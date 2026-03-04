package org.ever._4ever_be_gw.mockdata.scmpp.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateStockTransferDto {
    private String fromWarehouseId;
    private String toWarehouseId;
    private String itemId;
    private Integer stockQuantity;
    private String uomName;
    private String reason;
}
