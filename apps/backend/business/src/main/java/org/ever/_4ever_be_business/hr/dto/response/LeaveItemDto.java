package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 휴가 항목 DTO (개별 휴가 정보)
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveItemDto {
    @JsonProperty("leaveRequestId")
    private String leaveRequestId;

    @JsonProperty("leaveType")
    private String leaveType;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;

    @JsonProperty("numberOfDays")
    private Integer numberOfDays;

    @JsonProperty("status")
    private String status;  // PENDING, APPROVED, REJECTED

    @JsonProperty("reason")
    private String reason;
}
