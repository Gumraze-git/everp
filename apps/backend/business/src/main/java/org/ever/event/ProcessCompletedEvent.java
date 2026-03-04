package org.ever.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessCompletedEvent {
    private String eventId;
    private String TransactionId;
    private String customerUserId;
    private String loginEmail;
    private boolean success;
}