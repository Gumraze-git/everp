package org.ever._4ever_be_business.tam.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmployeeInfoDto {
    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("employeeNumber")
    private String employeeNumber;

    @JsonProperty("employeeName")
    private String employeeName;

    @JsonProperty("departmentId")
    private String departmentId;

    @JsonProperty("department")
    private String department;

    @JsonProperty("positionId")
    private String positionId;

    @JsonProperty("position")
    private String position;
}
