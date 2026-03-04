package org.ever.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserEvent {
    private String eventId;
    private String transactionId;
    private String customerUserId;
    private String companyName;
    private String businessNumber;
    private String managerName;
    private String managerEmail;
    private String managerMobile;
}