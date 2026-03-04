package org.ever._4ever_be_gw.business.dto.order;

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
public class SalesOrderListResponseDto {
    private List<SalesOrderListItemDto> content;
    private PageDto page;
}
