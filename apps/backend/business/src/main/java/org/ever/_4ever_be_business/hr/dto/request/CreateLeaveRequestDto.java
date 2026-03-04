package org.ever._4ever_be_business.hr.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.hr.enums.LeaveType;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateLeaveRequestDto {
    @JsonProperty("leaveType")
    private LeaveType leaveType;

    @JsonProperty("startDate")
    private String startDate;  // "YYYY-MM-DD" 형식

    @JsonProperty("endDate")
    private String endDate;    // "YYYY-MM-DD" 형식
}
