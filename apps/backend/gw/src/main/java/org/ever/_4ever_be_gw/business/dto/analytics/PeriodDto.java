package org.ever._4ever_be_gw.business.dto.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodDto {
    private String start;
    private String end;
    private String weekStart;
    private String weekEnd;
    private int weekCount;
}

