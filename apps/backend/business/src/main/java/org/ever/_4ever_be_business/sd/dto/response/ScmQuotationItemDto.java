package org.ever._4ever_be_business.sd.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScmQuotationItemDto {
    @JsonProperty("itemId")
    private String itemId;

    @JsonProperty("quantity")
    private Long quantity;
}
