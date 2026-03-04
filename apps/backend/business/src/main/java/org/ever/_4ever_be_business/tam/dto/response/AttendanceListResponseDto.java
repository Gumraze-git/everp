package org.ever._4ever_be_business.tam.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListResponseDto {
    @JsonProperty("total")
    private int total;

    @JsonProperty("content")
    private List<AttendanceListItemDto> content;

    @JsonProperty("pageInfo")
    private PageInfo pageInfo;
}
