package org.ever._4ever_be_gw.scm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class PeriodStatDto {
    @JsonProperty("value")
    private final long value;

    @JsonProperty("delta_rate")
    private final BigDecimal deltaRate;
}

