package org.ever._4ever_be_business.tam.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListItemDto {
    @JsonProperty("attendanceId")
    private String attendanceId;

    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("employeeName")
    private String employeeName;

    @JsonProperty("employeeNumber")
    private String employeeNumber;

    @JsonProperty("attendanceDate")
    private String attendanceDate;

    @JsonProperty("checkInTime")
    private String checkInTime;

    @JsonProperty("checkOutTime")
    private String checkOutTime;

    @JsonProperty("statusCode")
    private String statusCode;

    @JsonProperty("workType")
    private String workType;

    @JsonProperty("location")
    private String location;

    @JsonProperty("notes")
    private String notes;

    @JsonProperty("workingHours")
    private Integer workingHours;

    @JsonProperty("overtimeHours")
    private Integer overtimeHours;

    @JsonProperty("approvalStatus")
    private String approvalStatus;

    @JsonProperty("approverName")
    private String approverName;

    @JsonProperty("approverId")
    private String approverId;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonProperty("department")
    private String department;

    @JsonProperty("position")
    private String position;

    @JsonProperty("isLate")
    private Boolean isLate;

    @JsonProperty("isEarlyLeave")
    private Boolean isEarlyLeave;
}
