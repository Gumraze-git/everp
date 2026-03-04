package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class QuotationRequestDto {
    private String dueDate; // YYYY-MM-DD
    private List<QuotationItemRequestDto> items;
    private String note;
}
