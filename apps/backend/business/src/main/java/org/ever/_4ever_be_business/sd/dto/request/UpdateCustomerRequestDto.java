package org.ever._4ever_be_business.sd.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Duration;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequestDto {
    private String customerName;
    private String ceoName;
    private String businessNumber;
    private String customerPhone;
    private String customerEmail;
    private String baseAddress;
    private String detailAddress;
    private String statusCode;  // ACTIVE or INACTIVE
    private CustomerManagerRequestDto manager;
    private String note;
    private Duration deliveryLeadTime; // ISO-8601 duration (e.g., PT5M)
}
