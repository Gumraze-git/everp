package org.ever._4ever_be_business.fcm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TotalAmountDto {
    private PeriodTotalAmountDto week;
    private PeriodTotalAmountDto month;
    private PeriodTotalAmountDto quarter;
    private PeriodTotalAmountDto year;
}
