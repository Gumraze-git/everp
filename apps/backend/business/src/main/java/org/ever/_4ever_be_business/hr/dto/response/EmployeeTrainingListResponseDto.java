package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EmployeeTrainingListResponseDto {
    @JsonProperty("items")
    private List<EmployeeTrainingListItemDto> items;

    @JsonProperty("page")
    private PageInfoDto page;
}
