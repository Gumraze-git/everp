package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuotationCountDto {
    private PeriodQuotationCountDto week;
    private PeriodQuotationCountDto month;
    private PeriodQuotationCountDto quarter;
    private PeriodQuotationCountDto year;
}
