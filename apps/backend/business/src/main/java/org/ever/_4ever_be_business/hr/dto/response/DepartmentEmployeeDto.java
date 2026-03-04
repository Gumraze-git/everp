package org.ever._4ever_be_business.hr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentEmployeeDto {
    private String employeeId;
    private String employeeName;
    private String position;
    private String hireDate;
}
