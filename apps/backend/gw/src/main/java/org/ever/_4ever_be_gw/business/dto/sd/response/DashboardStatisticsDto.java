package org.ever._4ever_be_gw.business.dto.sd.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsDto {
    private PeriodStatisticsDto week;
    private PeriodStatisticsDto month;
    private PeriodStatisticsDto quarter;
    private PeriodStatisticsDto year;
}
