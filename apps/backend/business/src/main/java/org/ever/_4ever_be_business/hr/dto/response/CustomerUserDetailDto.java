package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerUserDetailDto {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("customerUserCode")
    private String customerUserCode;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("joinDate")
    private String joinDate;

    @JsonProperty("membershipMonths")
    private Long membershipMonths;

    @JsonProperty("companyName")
    private String companyName;
}
