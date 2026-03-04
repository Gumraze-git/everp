package org.ever._4ever_be_business.hr.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainingProgramDto {
    @JsonProperty("programName")
    private String programName;

    @JsonProperty("category")
    private TrainingCategory category;

    @JsonProperty("trainingHour")
    private Integer trainingHour;

    @JsonProperty("isOnline")
    private Boolean isOnline;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("capacity")
    private Integer capacity;

    @JsonProperty("requiredDepartments")
    private List<Integer> requiredDepartments;

    @JsonProperty("requiredPositions")
    private List<Integer> requiredPositions;

    @JsonProperty("description")
    private String description;
}
