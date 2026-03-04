package org.ever._4ever_be_gw.mockdata.scmpp.dto.bom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomCreateRequestDto {
    private String productName;
    private String productUomName;
    private String version;
    private List<ComponentDto> components;
    private List<RoutingDto> routing;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ComponentDto {
        private String itemId;
        private String itemNumber;
        private String itemName;
        private Integer quantity;
        private String uomName;
        private String level;
        private String supplierCompanyName;
        private String operationId;
        private String operationName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoutingDto {
        private Integer sequence;
        private String operationId;
        private String operationName;
        private Integer runTime;
    }
}
