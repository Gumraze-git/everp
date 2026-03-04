package org.ever._4ever_be_business.sd.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Duration;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequestDto {
    private String companyName;
    private String businessNumber;
    private String ceoName;
    private String contactPhone;
    private String contactEmail;
    private String zipCode;
    private String address;
    private String detailAddress;
    private CustomerManagerRequestDto manager;
    private String note;
    private Duration deliveryLeadTime; // ISO-8601 duration (e.g., PT5M)
}
