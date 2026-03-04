package org.ever._4ever_be_gw.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodStatisticsDto {
    @JsonProperty("totalEmployeeCount")
    private StatisticValueDto totalEmployeeCount;

    @JsonProperty("ongoingProgramCount")
    private StatisticValueDto ongoingProgramCount;

    @JsonProperty("completedProgramCount")
    private StatisticValueDto completedProgramCount;

    @JsonProperty("newEmployeeCount")
    private StatisticValueDto newEmployeeCount;
}
