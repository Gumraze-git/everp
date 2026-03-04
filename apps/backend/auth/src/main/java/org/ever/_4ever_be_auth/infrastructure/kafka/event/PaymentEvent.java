package org.ever._4ever_be_auth.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 다른 서비스(Payment)로부터 수신하는 이벤트
 */
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
