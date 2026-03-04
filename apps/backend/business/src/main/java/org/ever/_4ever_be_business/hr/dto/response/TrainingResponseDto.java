package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;

import java.util.List;

@Getter
public class TrainingResponseDto {
    @JsonProperty("programId")
    private final String programId;

    @JsonProperty("programName")
    private final String programName;

    @JsonProperty("programDescription")
    private final String programDescription;

    @JsonProperty("category")
    private final TrainingCategory category;

    @JsonProperty("trainingHour")
    private final Long trainingHour;

    @JsonProperty("isOnline")
    private final Boolean isOnline;

    @JsonProperty("startDate")
    private final String startDate;

    @JsonProperty("statusCode")
    private final String statusCode;

    @JsonProperty("designatedEmployee")
    private final List<DesignatedEmployee> designatedEmployee;

    public TrainingResponseDto(Boolean isOnline, String programId, String programName, String programDescription, TrainingCategory category, Long trainingHour, String startDate, String statusCode, List<DesignatedEmployee> designatedEmployee) {
        this.isOnline = isOnline;
        this.programId = programId;
        this.programName = programName;
        this.programDescription = programDescription;
        this.category = category;
        this.trainingHour = trainingHour;
        this.startDate = startDate;
        this.statusCode = statusCode;
        this.designatedEmployee = designatedEmployee;
    }
}
