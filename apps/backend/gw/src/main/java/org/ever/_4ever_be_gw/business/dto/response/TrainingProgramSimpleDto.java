package org.ever._4ever_be_gw.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingProgramSimpleDto {
    @JsonProperty("programId")
    private String programId;

    @JsonProperty("programName")
    private String programName;
}
