package org.ever.event;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessEvent extends BaseEvent {

    private String businessId;
    private String orderId;
    private String userId;
    private BigDecimal totalAmount;
    private BusinessAction action;

    public enum BusinessAction {
        ORDER_CREATED,
        ORDER_CONFIRMED,
        ORDER_CANCELLED,
        INVOICE_GENERATED
    }
}
