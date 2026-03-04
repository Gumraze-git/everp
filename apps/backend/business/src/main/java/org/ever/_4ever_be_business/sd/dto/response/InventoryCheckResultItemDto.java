package org.ever._4ever_be_business.sd.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckResultItemDto {
    @JsonProperty("itemId")
    private String itemId;

    @JsonProperty("itemName")
    private String itemName;

    @JsonProperty("requiredQuantity")
    private Integer requiredQuantity;

    @JsonProperty("inventoryQuantity")
    private Integer inventoryQuantity;

    @JsonProperty("shortageQuantity")
    private Integer shortageQuantity;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("productionRequired")
    private Boolean productionRequired;
}
