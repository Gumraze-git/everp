package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 직원 교육 이력 응답 DTO
 */
@Getter
@AllArgsConstructor
public class EmployeeTrainingHistoryDto {

    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("employeeName")
    private String employeeName;

    @JsonProperty("department")
    private String department;

    @JsonProperty("position")
    private String position;

    @JsonProperty("completedCount")
    private Integer completedCount;

    @JsonProperty("requiredMissingCount")
    private Integer requiredMissingCount;

    @JsonProperty("lastTrainingDate")
    private LocalDateTime lastTrainingDate;

    @JsonProperty("programHistory")
    private List<ProgramHistoryItemDto> programHistory;
}
