package org.ever._4ever_be_business.pp.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PpQuotationRequestDto {
    private String quotationId;
    private Integer page = 0;
    private Integer size = 10;
}
