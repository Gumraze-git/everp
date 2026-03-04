package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodayAttendanceDto {
    @JsonProperty("checkInTime")
    private String checkInTime;  // HH:mm 형식, 없으면 null

    @JsonProperty("checkOutTime")
    private String checkOutTime;  // HH:mm 형식, 없으면 null

    @JsonProperty("workHours")
    private String workHours;  // "8시간 30분" 형식, 없으면 null

    @JsonProperty("status")
    private String status;  // "근무중", "출근전", "퇴근완료"
}
