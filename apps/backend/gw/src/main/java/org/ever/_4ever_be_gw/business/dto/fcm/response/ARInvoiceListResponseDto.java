package org.ever._4ever_be_gw.business.dto.fcm.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_gw.business.dto.response.PageInfoDto;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ARInvoiceListResponseDto {
    private List<ARInvoiceListItemDto> content;
    private PageInfoDto page;
}
