package org.ever._4ever_be_gw.mockdata.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlannedOrderListItemDto {
    private String mrpId;
    private String quotationId;
    private String quotationNumber;
    private String itemId;
    private String itemName;
    private int quantity;
    private String procurementStartDate;
    private String statusCode;
}
