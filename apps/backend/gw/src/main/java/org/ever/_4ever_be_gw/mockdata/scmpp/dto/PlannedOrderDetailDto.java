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
public class PlannedOrderDetailDto {
    private String mrpId;
    private String quotationId;
    private String quotationCode;
    private String requesterId;
    private String requesterName;
    private String departmentName;
    private String requestDate;
    private String desiredDueDate;
    private String status;
    private List<OrderItemDto> orderItems;
    private int totalAmount;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        private String itemId;
        private String itemName;
        private int quantity;
        private String uomName;
        private int unitPrice;
    }
}
