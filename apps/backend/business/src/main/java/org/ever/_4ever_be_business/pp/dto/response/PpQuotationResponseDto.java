package org.ever._4ever_be_business.pp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.sd.dto.response.QuotationListItemDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PpQuotationResponseDto {
    private List<QuotationListItemDto> items;
    private PageInfo page;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private Integer number;
        private Integer size;
        private Long totalElements;
        private Integer totalPages;
        private Boolean hasNext;
    }
}
