package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestListItemDto {
    @JsonProperty("leaveRequestId")
    private String leaveRequestId;

    @JsonProperty("employee")
    private LeaveRequestEmployeeDto employee;

    @JsonProperty("leaveType")
    private String leaveType;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

    @JsonProperty("numberOfLeaveDays")
    private Integer numberOfLeaveDays;

    @JsonProperty("remainingLeaveDays")
    private Integer remainingLeaveDays;

    @JsonProperty("status")
    private String status;  // PENDING, APPROVED, REJECTED
}
