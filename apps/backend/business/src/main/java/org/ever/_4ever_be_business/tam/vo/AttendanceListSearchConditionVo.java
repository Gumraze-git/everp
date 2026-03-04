package org.ever._4ever_be_business.tam.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListSearchConditionVo {
    private String employeeId;
    private String startDate;    // YYYY-MM-DD
    private String endDate;      // YYYY-MM-DD
    private String status;       // NORMAL, LATE
}
