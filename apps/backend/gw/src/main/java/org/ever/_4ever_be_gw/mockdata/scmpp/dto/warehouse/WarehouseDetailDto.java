package org.ever._4ever_be_gw.mockdata.scmpp.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDetailDto {
    private WarehouseInfoDto warehouseInfo;
    private WarehouseManagerDto manager;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseInfoDto {
        private String warehouseName;
        private String warehouseNumber;
        private String warehouseType;
        private String statusCode;
        private String location;
        private String description;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseManagerDto {
        private String managerId;
        private String managerName;
        private String managerPhoneNumber;
        private String managerEmail;
    }
}
