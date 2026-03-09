package org.ever._4ever_be_business.sd.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScmQuotationListResponseDto {

    private long total;
    private List<ScmQuotationListItemDto> content;
    private PageInfo page;
}
