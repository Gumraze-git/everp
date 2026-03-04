package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrainingListResponseDto {
    @JsonProperty("total")
    private int total;

    @JsonProperty("content")
    private List<TrainingListItemDto> content;

    @JsonProperty("pageInfo")
    private PageInfo pageInfo;
}
