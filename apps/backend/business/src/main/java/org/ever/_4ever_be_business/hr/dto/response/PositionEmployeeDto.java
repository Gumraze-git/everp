package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PositionEmployeeDto {
    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("employeeCode")
    private String employeeCode;

    @JsonProperty("employeeName")
    private String employeeName;

    @JsonProperty("positionId")
    private String positionId;

    @JsonProperty("position")
    private String position;

    @JsonProperty("departmentId")
    private String departmentId;

    @JsonProperty("department")
    private String department;

    @JsonProperty("hireDate")
    private String hireDate;
}
