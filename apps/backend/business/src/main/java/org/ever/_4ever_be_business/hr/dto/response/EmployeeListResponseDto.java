package org.ever._4ever_be_business.hr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.sd.dto.response.PageInfo;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeListResponseDto {
    private List<EmployeeListItemDto> content; // 직원 목록
    private PageInfo pageInfo; // 페이징 정보
}
