package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingItemDto {
    @JsonProperty("trainingId")
    private String trainingId;

    @JsonProperty("trainingName")
    private String trainingName;

    @JsonProperty("trainingStatus")
    private String trainingStatus;  // Training의 status

    @JsonProperty("durationHours")
    private Long durationHours;

    @JsonProperty("deliveryMethod")
    private String deliveryMethod;

    @JsonProperty("completionStatus")
    private String completionStatus;  // EmployeeTraining의 completionStatus, 신청가능한 교육이면 null

    @JsonProperty("category")
    private String category;  // Training의 category

    @JsonProperty("description")
    private String description;  // Training의 description

    @JsonProperty("complementationDate")
    private String complementationDate;  // 수료일 (yyyy-MM-dd), 수료한 교육만 해당
}
