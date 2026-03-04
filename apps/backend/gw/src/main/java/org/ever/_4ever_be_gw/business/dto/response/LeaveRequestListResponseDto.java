package org.ever._4ever_be_gw.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestListResponseDto {
    private List<LeaveRequestListItemDto> content;
    private PageInfoDto page;
}
