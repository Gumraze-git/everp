package org.ever._4ever_be_gw.mockdata.scmpp.dto.po;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PoDetailDto {
    private String statusCode;
    private LocalDate dueDate;
    private String purchaseOrderId;
    private String purchaseOrderNumber;
    private LocalDate orderDate;
    private LocalDate requestedDeliveryDate;
    private String supplierId;
    private String supplierNumber;
    private String supplierName;
    private String managerPhone;
    private String managerEmail;
    private String deliveryAddress;
    private List<PoItemDto> items;
    private long totalAmount;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReferenceInfo {
        private String type;
        private String purchaseOrderId;
        private String purchaseOrderNumber;
        private LocalDate orderDate;
        private LocalDate requestedDeliveryDate;
    }
}
