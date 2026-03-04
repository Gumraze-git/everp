package org.ever._4ever_be_business.tam.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceRecordDto {
    @JsonProperty("workDate")
    private String workDate;

    @JsonProperty("checkInTime")
    private String checkInTime;

    @JsonProperty("checkOutTime")
    private String checkOutTime;

    @JsonProperty("totalWorkMinutes")
    private Long totalWorkMinutes;

    @JsonProperty("overtimeMinutes")
    private Long overtimeMinutes;

    @JsonProperty("statusCode")
    private String statusCode;
}
