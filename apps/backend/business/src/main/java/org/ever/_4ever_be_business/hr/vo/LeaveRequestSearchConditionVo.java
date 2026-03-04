package org.ever._4ever_be_business.hr.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.hr.enums.LeaveType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestSearchConditionVo {
    private String departmentId;
    private String positionId;
    private String name;
    private LeaveType type;
    private String sortOrder;  // DESC or ASC
}
