package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EmployeeTrainingListItemDto {
    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("department")
    private String department;

    @JsonProperty("position")
    private String position;

    @JsonProperty("completedCount")
    private Integer completedCount;

    @JsonProperty("inProgressCount")
    private Integer inProgressCount;

    @JsonProperty("requiredMissingCount")
    private Integer requiredMissingCount;

    @JsonProperty("lastTrainingDate")
    private LocalDateTime lastTrainingDate;
}
