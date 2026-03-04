package org.ever._4ever_be_gw.common.dto.stats;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.ever._4ever_be_gw.scm.PeriodStatDto;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class StatsMetricsDto {
    @JsonIgnore
    private final Map<String, PeriodStatDto> metrics;

    private StatsMetricsDto(Map<String, PeriodStatDto> metrics) {
        this.metrics = Map.copyOf(metrics);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Map<String, PeriodStatDto> metrics = new LinkedHashMap<>();

        public Builder put(String key, PeriodStatDto value) {
            metrics.put(key, value);
            return this;
        }

        public StatsMetricsDto build() {
            return new StatsMetricsDto(metrics);
        }
    }

    @JsonAnyGetter
    public Map<String, PeriodStatDto> toMap() {
        return metrics;
    }
}
