package org.ever._4ever_be_gw.mockdata.scmpp.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDto {
    private String warehouseId;
    private String warehouseNumber;
    private String warehouseName;
    private String statusCode;
    private String warehouseType;
    private String location;
    private String manager;
    private String managerPhone;
}
