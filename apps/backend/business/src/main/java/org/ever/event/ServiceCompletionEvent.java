package org.ever.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCompletionEvent {
    private String eventId;
    private String customerUserId;
    private boolean success;
    private String message;
    
    // 알림 발송 관련 정보
    private String recipientEmail;
    private String recipientName;
}
