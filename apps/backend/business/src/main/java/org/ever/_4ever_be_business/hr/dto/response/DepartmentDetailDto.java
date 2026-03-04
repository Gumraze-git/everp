package org.ever._4ever_be_business.hr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDetailDto {
    private String departmentId;
    private String departmentCode;
    private String departmentName;
    private String headName;
    private Long headcount;
    private String createdAt;
    private String description;
    private List<DepartmentEmployeeDto> employees;
}
