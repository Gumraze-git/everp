package org.ever._4ever_be_business.hr.vo;

import lombok.Getter;

@Getter
public class EmployeeTrainingHistoryVo {
    private final String employeeId;

    public EmployeeTrainingHistoryVo(String employeeId) {
        this.employeeId = employeeId;
    }
}
