package org.ever.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 고객사 담당자 계정 생성을 Auth 서비스에 요청하기 위한 이벤트.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerUserEvent {
    private String eventId;
    private String transactionId;

    private String customerCompanyId;
    private String customerCompanyCode;
    private String customerCompanyName;

    private String customerUserId;
    private String userId;

    private String managerName;
    private String managerEmail;
    private String managerPhone;
}
