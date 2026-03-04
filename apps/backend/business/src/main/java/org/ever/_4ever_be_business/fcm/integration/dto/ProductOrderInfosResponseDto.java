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
public class ProductOrderInfosResponseDto {
    private List<ProductOrderInfoItem> data;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductOrderInfoItem {
        @JsonProperty("productOrderId")
        private String productOrderId;

        @JsonProperty("productOrderNumber")
        private String productOrderNumber;

        @JsonProperty("totalAmount")
        private BigDecimal totalAmount;
    }
}
