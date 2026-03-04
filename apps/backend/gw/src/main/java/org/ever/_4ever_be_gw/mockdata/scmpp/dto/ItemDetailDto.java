package org.ever._4ever_be_gw.mockdata.scmpp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemDetailDto {
    private String itemId;
    private String itemNumber;
    private String itemName;
    private String category;
    private String supplierCompanyName;
    private String statusCode;
    private int currentStock;
    private int safetyStock;
    private String uomName;
    private int unitPrice;
    private int totalAmount;
    private String warehouseId;
    private String warehouseName;
    private String warehouseNumber;
    private LocalDateTime lastModified;
    private String location;
    private String description;
    private List<StockMovementDto> stockMovements;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockMovementDto {
        private String type;
        private int quantity;
        private String uomName;
        private String from;
        private String to;
        private LocalDateTime movementDate;
        private String managerName;
        private String referenceNumber;
        private String note;
    }
}
