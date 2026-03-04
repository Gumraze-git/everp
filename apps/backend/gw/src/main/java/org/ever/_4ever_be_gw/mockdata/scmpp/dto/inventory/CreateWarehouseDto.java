package org.ever._4ever_be_gw.mockdata.scmpp.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWarehouseDto {
    private String warehouseName;
    private String warehouseType;
    private String location;
    private String managerId;
    private String note;
}
