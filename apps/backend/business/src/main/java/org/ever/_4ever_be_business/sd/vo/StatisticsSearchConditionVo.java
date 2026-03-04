package org.ever._4ever_be_business.sd.vo;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class StatisticsSearchConditionVo {
    private final LocalDate startDate;
    private final LocalDate endDate;

    public StatisticsSearchConditionVo(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
