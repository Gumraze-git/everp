package org.ever._4ever_be_business.sd.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckItemDto {
    @JsonProperty("itemId")
    private String itemId;

    @JsonProperty("requiredQuantity")
    private Integer requiredQuantity;
}
