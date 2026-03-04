package org.ever._4ever_be_gw.mockdata.scmpp.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseStatisticDto {
    private TotalWarehouseDto totalWarehouse;
    private InOperationWarehouseDto inOperationWarehouse;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TotalWarehouseDto {
        private String value;
        private int delta_rate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InOperationWarehouseDto {
        private int value;
        private int delta_rate;
    }
}
