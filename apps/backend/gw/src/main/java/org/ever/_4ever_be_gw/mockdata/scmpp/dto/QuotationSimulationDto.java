package org.ever._4ever_be_gw.mockdata.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationSimulationDto {
    private String quotationId;
    private String quotationNumber;
    private String customerCompanyId;
    private String customerCompanyName;
    private String productId;
    private String productName;
    private Integer requestQuantity;
    private String requestDueDate;
    private SimulationResultDto simulation;
    private List<ShortageItemDto> shortages;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimulationResultDto {
        private String status; // FAIL, PENDING, PASS
        private Integer availableQuantity;
        private Integer shortageQuantity;
        private String suggestedDueDate;
        private String generatedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShortageItemDto {
        private String itemId;
        private String itemName;
        private Integer requiredQuantity;
        private Integer currentStock;
        private Integer shortQuantity;
    }
}
