package org.ever._4ever_be_gw.mockdata.scmpp.dto.pr;

import lombok.Getter;
import java.time.LocalDate;
import java.util.List;

@Getter
public class MmPurchaseRequisitionCreateRequestDto {
    private String requesterId;
    private List<Item> items;

    @Getter
    public static class Item {
        private String itemName;
        private Integer quantity;
        private String uomName;
        private Long expectedUnitPrice;
        private String preferredSupplierName;
        private LocalDate dueDate;
        private String purpose;
        private String note;
    }
}

