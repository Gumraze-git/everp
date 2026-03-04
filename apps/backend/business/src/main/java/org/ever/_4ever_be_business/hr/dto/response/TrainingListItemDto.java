package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.ever._4ever_be_business.hr.enums.TrainingCategory;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;

/**
 * 교육 프로그램 목록 아이템 DTO
 */
@Getter
@AllArgsConstructor
public class TrainingListItemDto {

    @JsonProperty("programId")
    private String programId;

    @JsonProperty("programName")
    private String programName;

    @JsonProperty("statusCode")
    private TrainingStatus statusCode;

    @JsonProperty("category")
    private TrainingCategory category;

    @JsonProperty("trainingHour")
    private Integer trainingHour;

    @JsonProperty("isOnline")
    private Boolean isOnline;

    @JsonProperty("capacity")
    private Integer capacity;
}
