package org.ever._4ever_be_gw.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeListResponseDto {
    private List<EmployeeListItemDto> content;
    private PageInfoDto page;
}
