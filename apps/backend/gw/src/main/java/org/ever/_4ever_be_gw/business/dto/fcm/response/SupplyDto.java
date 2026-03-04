package org.ever._4ever_be_gw.business.dto.fcm.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SupplyDto {

    @JsonProperty("supplierId")
    private String supplierId;

    @JsonProperty("supplierNumber")
    private String supplierNumber;

    @JsonProperty("supplierName")
    private String supplierName;
}
