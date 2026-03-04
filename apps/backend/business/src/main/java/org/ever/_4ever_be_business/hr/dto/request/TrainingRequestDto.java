package org.ever._4ever_be_business.hr.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRequestDto {
    @JsonProperty("programId")
    private String programId;

    private String employeeId;

    public TrainingRequestDto(String programId) {
        this.programId = programId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}
