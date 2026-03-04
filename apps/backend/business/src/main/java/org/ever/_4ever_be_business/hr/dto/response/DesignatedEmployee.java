package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DesignatedEmployee {
    @JsonProperty("employeeId")
    private final String employeeId;

    @JsonProperty("employeeName")
    private final String employeeName;

    @JsonProperty("department")
    private final String department;

    @JsonProperty("position")
    private final String position;

    @JsonProperty("statusCode")
    private final String statusCode;

    @JsonProperty("completedAt")
    private final String completedAt;

    public DesignatedEmployee(String employeeId, String employeeName, String department, String position, String statusCode, String completedAt) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.department = department;
        this.position = position;
        this.statusCode = statusCode;
        this.completedAt = completedAt;
    }
}
