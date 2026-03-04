package org.ever._4ever_be_gw.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceListResponseDto {
    private List<AttendanceListItemDto> content;
    private PageInfoDto page;
}
