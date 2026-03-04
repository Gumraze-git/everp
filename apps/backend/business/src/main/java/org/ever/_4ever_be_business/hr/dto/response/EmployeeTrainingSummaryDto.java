package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmployeeTrainingSummaryDto {
    @JsonProperty("id")
    private String id;

    @JsonProperty("employeeNumber")
    private String employeeNumber;

    @JsonProperty("employeeName")
    private String employeeName;

    @JsonProperty("department")
    private String department;

    @JsonProperty("position")
    private String position;

    @JsonProperty("completedCount")
    private Long completedCount;

    @JsonProperty("inProgressCount")
    private Long inProgressCount;

    @JsonProperty("requiredMissingCount")
    private Long requiredMissingCount;

    @JsonProperty("lastTrainingDate")
    private LocalDateTime lastTrainingDate;
}
