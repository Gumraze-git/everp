package org.ever._4ever_be_business.tam.vo;

import lombok.Getter;

@Getter
public class TimeRecordDetailVo {
    private final String timerecordId;

    public TimeRecordDetailVo(String timerecordId) {
        this.timerecordId = timerecordId;
    }
}
