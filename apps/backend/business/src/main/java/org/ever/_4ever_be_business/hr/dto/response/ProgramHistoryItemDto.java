package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 교육 프로그램 이력 아이템 DTO
 */
@Getter
@AllArgsConstructor
public class ProgramHistoryItemDto {

    @JsonProperty("programId")
    private String programId;

    @JsonProperty("programName")
    private String programName;

    @JsonProperty("programStatus")
    private String programStatus; // COMPLETED, INCOMPLETED

    @JsonProperty("completedAt")
    private LocalDateTime completedAt;
}
