package org.ever._4ever_be_gw.mockdata.scmpp.dto.bom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BomDetailDto {
    private String bomId;
    private String bomNumber;
    private String productId;
    private String productNumber;
    private String productName;
    private String version;
    private String status;
    private LocalDateTime lastModifiedAt;
    private List<BomCreateRequestDto.ComponentDto> components;
    private Map<String, List<LevelComponentDto>> levelStructure;
    private List<BomCreateRequestDto.RoutingDto> routing;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LevelComponentDto {
        private String itemId;
        private String itemNumber;
        private String itemName;
        private Integer quantity;
        private String uomName;
    }
}
