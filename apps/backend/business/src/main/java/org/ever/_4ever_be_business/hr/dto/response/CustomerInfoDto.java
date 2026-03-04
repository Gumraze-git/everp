package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerInfoDto {
    // CustomerCompany 정보
    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("baseAddress")
    private String baseAddress;

    @JsonProperty("detailAddress")
    private String detailAddress;

    @JsonProperty("officePhone")
    private String officePhone;

    @JsonProperty("businessNumber")
    private String businessNumber;

    // CustomerUser 정보
    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("email")
    private String email;
}
