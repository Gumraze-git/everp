package org.ever._4ever_be_business.tam.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ever._4ever_be_business.tam.enums.AttendanceStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TimeRecordListItemDto {
    @JsonProperty("timerecordId")
    private String timerecordId;

    @JsonProperty("employee")
    private EmployeeBasicInfoDto employee;

    @JsonProperty("workDate")
    private LocalDate workDate;

    @JsonProperty("checkInTime")
    private LocalDateTime checkInTime;

    @JsonProperty("checkOutTime")
    private LocalDateTime checkOutTime;

    @JsonProperty("totalWorkMinutes")
    private Long totalWorkMinutes;

    @JsonProperty("overtimeMinutes")
    private Long overtimeMinutes;

    @JsonProperty("statusCode")
    private AttendanceStatus statusCode;
}
