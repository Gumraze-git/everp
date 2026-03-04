package org.ever._4ever_be_business.tam.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.tam.enums.AttendanceStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTimeRecordDto {
    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("inTime")
    private LocalDateTime inTime;

    @JsonProperty("outTime")
    private LocalDateTime outTime;

    @JsonProperty("attendanceStatus")
    private AttendanceStatus attendanceStatus;
}
