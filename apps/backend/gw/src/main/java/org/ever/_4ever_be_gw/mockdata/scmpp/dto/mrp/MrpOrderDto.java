package org.ever._4ever_be_gw.mockdata.scmpp.dto.mrp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MrpOrderDto {
    private String itemId;
    private String itemName;
    private int requiredQuantity;
    private int currentStock;
    private int safetyStock;
    private int availableStock;
    private String availableStatusCode;
    private Integer shortageQuantity;
    private String itemType;
    private String procurementStartDate;
    private String expectedArrivalDate;
    private String supplierCompanyName;
}
