package org.ever._4ever_be_business.infrastructure.redis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDeliveryCompletionTask implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;
    private LocalDateTime scheduledCompletionTime;
    private LocalDateTime createdAt;
}
