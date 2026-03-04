package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodInfoDto {
    private String start;
    private String end;
    private String weekStart;
    private String weekEnd;
    private int weekCount;
}
