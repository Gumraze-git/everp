package org.ever._4ever_be_business.sd.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateQuotationRequestDto {
    private String userId;          // CustomerUser의 userId (String)
    private List<QuotationItemRequestDto> items;
    private String note;
}
