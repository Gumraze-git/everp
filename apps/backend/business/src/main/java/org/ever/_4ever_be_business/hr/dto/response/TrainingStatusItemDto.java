package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TrainingStatusItemDto {
    @JsonProperty("employeeId")
    private String employeeId;

    @JsonProperty("name")
    private String name;

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
