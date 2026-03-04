package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RemainingLeaveDaysDto {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("totalLeaveDays")
    private Integer totalLeaveDays;  // 기본 연차 (18일)

    @JsonProperty("usedLeaveDays")
    private Integer usedLeaveDays;   // 사용한 연차

    @JsonProperty("remainingLeaveDays")
    private Integer remainingLeaveDays;  // 잔여 연차

    @JsonProperty("leaveRequests")
    private List<LeaveItemDto> leaveRequests;  // 1년 내 휴가 목록 (상태 포함)
}
