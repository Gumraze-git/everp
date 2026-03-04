package org.ever._4ever_be_gw.business.dto.fcm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FcmStatisticsDto {
    private FcmPeriodStatisticsDto week;
    private FcmPeriodStatisticsDto month;
    private FcmPeriodStatisticsDto quarter;
    private FcmPeriodStatisticsDto year;
}
