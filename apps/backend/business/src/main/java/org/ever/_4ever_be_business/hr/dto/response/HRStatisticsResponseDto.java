package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HRStatisticsResponseDto {
    @JsonProperty("week")
    private PeriodStatisticsDto week;

    @JsonProperty("month")
    private PeriodStatisticsDto month;

    @JsonProperty("quarter")
    private PeriodStatisticsDto quarter;

    @JsonProperty("year")
    private PeriodStatisticsDto year;
}
