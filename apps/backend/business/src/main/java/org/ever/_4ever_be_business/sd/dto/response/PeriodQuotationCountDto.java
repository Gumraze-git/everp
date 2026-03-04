package org.ever._4ever_be_business.sd.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.fcm.dto.response.FcmStatisticsValueDto;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodQuotationCountDto {
    @JsonProperty("quotation_count")
    private FcmStatisticsValueDto quotationCount;
}
