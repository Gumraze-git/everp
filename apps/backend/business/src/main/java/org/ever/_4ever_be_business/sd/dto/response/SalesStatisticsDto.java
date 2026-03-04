package org.ever._4ever_be_business.sd.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SalesStatisticsDto {
    @JsonProperty("sales_amount")
    private StatisticsValueDto salesAmount;

    @JsonProperty("new_orders_count")
    private StatisticsValueDto newOrdersCount;
}
