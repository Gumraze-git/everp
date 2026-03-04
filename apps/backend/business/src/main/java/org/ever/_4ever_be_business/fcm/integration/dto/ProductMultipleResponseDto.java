package org.ever._4ever_be_business.fcm.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductMultipleResponseDto {

    @JsonProperty("products")
    private List<ProductDto> products;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {

        @JsonProperty("itemId")
        private String itemId;

        @JsonProperty("itemNumber")
        private String itemNumber;

        @JsonProperty("itemName")
        private String itemName;

        @JsonProperty("uomName")
        private String uomName;

        @JsonProperty("unitPrice")
        private BigDecimal unitPrice;
    }
}
