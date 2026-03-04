package org.ever._4ever_be_business.fcm.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SupplyDto {

    @JsonProperty("connectionId")
    private String supplierId;

    @JsonProperty("connectionNumber")
    private String supplierNumber;

    @JsonProperty("connectionName")
    private String supplierName;
}
