package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAttendanceRecordDto {
    @JsonProperty("date")
    private String date;  // 2025-10-11 형식

    @JsonProperty("status")
    private String status;  // 지각, 정상

    @JsonProperty("startTime")
    private String startTime;  // HH:mm 형식

    @JsonProperty("endTime")
    private String endTime;  // HH:mm 형식

    @JsonProperty("workHours")
    private String workHours;  // "8시간 30분" 형식
}
