package org.ever.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent extends BaseEvent {

    private String paymentId;
    private String orderId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private PaymentStatus status;
    private String description;

    public enum PaymentStatus {
        REQUESTED,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}
