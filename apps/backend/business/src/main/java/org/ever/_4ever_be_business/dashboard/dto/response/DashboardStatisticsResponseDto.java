package org.ever._4ever_be_business.dashboard.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Dashboard statistics response containing all periods
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatisticsResponseDto {
    @JsonProperty("week")
    private DashboardPeriodStatisticsDto week;

    @JsonProperty("month")
    private DashboardPeriodStatisticsDto month;

    @JsonProperty("quarter")
    private DashboardPeriodStatisticsDto quarter;

    @JsonProperty("year")
    private DashboardPeriodStatisticsDto year;
}
