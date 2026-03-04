package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StatisticValueDto {
    @JsonProperty("value")
    private Integer value;

    /**
     * Absolute difference from previous period (not percentage)
     * Example: if current = 15, previous = 12, delta_rate = 3
     * Example: if current = 10, previous = 12, delta_rate = -2
     */
    @JsonProperty("delta_rate")
    private Integer deltaRate;
}
