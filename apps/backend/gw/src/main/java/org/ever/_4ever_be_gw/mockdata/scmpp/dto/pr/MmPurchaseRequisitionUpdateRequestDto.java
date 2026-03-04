package org.ever._4ever_be_gw.mockdata.scmpp.dto.pr;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class MmPurchaseRequisitionUpdateRequestDto {

    private LocalDate dueDate;
    private List<ItemOperation> items;

    @Getter
    public static class ItemOperation {
        private String operation;
        private Long itemId;
        private String itemName;
        private Integer quantity;
        private String uomName;
        private Long unitPrice;
        private String supplierName;
        private String purpose;
        private String note;
    }
}

