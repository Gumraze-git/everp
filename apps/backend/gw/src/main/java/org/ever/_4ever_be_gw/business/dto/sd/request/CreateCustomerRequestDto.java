package org.ever._4ever_be_gw.business.dto.sd.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
