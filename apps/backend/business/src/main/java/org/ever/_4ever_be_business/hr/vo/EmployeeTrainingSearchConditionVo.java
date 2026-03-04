package org.ever._4ever_be_business.hr.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeTrainingSearchConditionVo {
    private String department;
    private String position;
    private String name;

    public EmployeeTrainingSearchConditionVo(String department, String position, String name) {
        this.department = department;
        this.position = position;
        this.name = name;
    }
}
