package org.ever._4ever_be_gw.business.dto.sd.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
