package org.ever._4ever_be_business.sd.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoResponseDto {
    private List<ProductDto> products;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        @JsonProperty("itemId")
        private String productId;

        @JsonProperty("itemNumber")
        private String productCode;

        @JsonProperty("itemName")
        private String productName;

        @JsonProperty("uomName")
        private String uomName;

        @JsonProperty("unitPrice")
        private BigDecimal unitPrice;
    }
}
