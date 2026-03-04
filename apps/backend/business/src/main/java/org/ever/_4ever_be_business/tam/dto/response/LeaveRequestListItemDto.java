package org.ever._4ever_be_business.tam.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ever._4ever_be_business.hr.enums.VacationType;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class LeaveRequestListItemDto {
    @JsonProperty("leaveRequestId")
    private String leaveRequestId;

    @JsonProperty("employee")
    private LeaveRequestEmployeeDto employee;

    @JsonProperty("leaveType")
    private VacationType leaveType;

    @JsonProperty("startDate")
    private LocalDate startDate;

    @JsonProperty("endDate")
    private LocalDate endDate;

    @JsonProperty("numberOfLeaveDays")
    private Integer numberOfLeaveDays;

    @JsonProperty("remainingLeaveDays")
    private Long remainingLeaveDays;
}
