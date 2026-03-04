package org.ever._4ever_be_gw.business.dto.fcm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_gw.business.dto.response.PageInfoDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseStatementListResponseDto {
    private List<PurchaseStatementListItemDto> content;
    private PageInfoDto page;
}
