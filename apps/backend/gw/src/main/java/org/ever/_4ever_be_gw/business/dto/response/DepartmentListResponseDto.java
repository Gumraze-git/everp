package org.ever._4ever_be_gw.business.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_gw.business.dto.response.PageInfo;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentListResponseDto {
    @JsonProperty("total")
    private Integer total;

    @JsonProperty("departments")
    private List<DepartmentListItemDto> departments;

    @JsonProperty("page")
    private PageInfo page;
}
