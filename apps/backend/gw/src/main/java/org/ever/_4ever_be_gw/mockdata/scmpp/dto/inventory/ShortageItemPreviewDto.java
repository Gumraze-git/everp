package org.ever._4ever_be_gw.mockdata.scmpp.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortageItemPreviewDto {
    private String itemId;
    private String itemName;
    private int currentStock;
    private String uomName;
    private int safetyStock;
    private String statusCode;
}
