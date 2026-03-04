package org.ever._4ever_be_business.tam.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ever._4ever_be_business.hr.enums.VacationType;

@Getter
@Setter
@NoArgsConstructor
public class LeaveRequestSearchConditionVo {
    private String department;
    private String position;
    private String name;
    private VacationType type;

    public LeaveRequestSearchConditionVo(String department, String position, String name, VacationType type) {
        this.department = department;
        this.position = position;
        this.name = name;
        this.type = type;
    }
}
