package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PayItemDto {
    @JsonProperty("itemContent")
    private String itemContent;

    @JsonProperty("itemSum")
    private BigDecimal itemSum;
}
