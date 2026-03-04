package org.ever._4ever_be_business.fcm.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCompanyResponseDto {
    @JsonProperty("companyId")
    private String companyId;

    @JsonProperty("companyNumber")
    private String companyNumber;

    @JsonProperty("companyName")
    private String companyName;

    @JsonProperty("baseAddress")
    private String baseAddress;

    @JsonProperty("detailAddress")
    private String detailAddress;

    @JsonProperty("category")
    private String category;

    @JsonProperty("officePhone")
    private String officePhone;

    @JsonProperty("managerId")
    private String managerId;
}
