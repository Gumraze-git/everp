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
public class ReadyToShipDetailDto {
    private String salesOrderId;
    private String salesOrderNumber;
    private String customerCompanyName;
    private LocalDateTime dueDate;
    private String statusCode;
    private List<OrderItemDto> orderItems;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private String itemName;
        private int quantity;
        private String uomName;
    }
}
