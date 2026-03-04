package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeTrainingItemDto {
    @JsonProperty("trainingId")
    private String trainingId;

    @JsonProperty("trainingName")
    private String trainingName;

    @JsonProperty("category")
    private String category;

    @JsonProperty("durationHours")
    private Long durationHours;

    @JsonProperty("completionStatus")
    private String completionStatus;  // TrainingCompletionStatus enum의 name()
}
