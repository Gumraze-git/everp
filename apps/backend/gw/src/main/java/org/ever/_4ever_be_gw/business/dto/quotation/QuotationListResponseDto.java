package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_gw.common.dto.PageDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationListResponseDto {
    private List<QuotationListItemDto> items; // 목록 아이템
    private PageDto page;                     // 페이지 메타
}

