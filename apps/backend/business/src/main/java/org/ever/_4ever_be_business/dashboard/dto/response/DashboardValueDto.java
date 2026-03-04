package org.ever._4ever_be_business.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Dashboard statistics value with absolute delta (not percentage)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardValueDto {
    /**
     * Current accumulated value
     */
    @JsonProperty("value")
    private BigDecimal value;

    /**
     * Absolute difference from previous period (not percentage)
     * Example: if current is 1000 and previous was 900, delta is 100
     * Example: if current is 800 and previous was 900, delta is -100
     */
    @JsonProperty("delta_rate")
    private BigDecimal delta;
}
