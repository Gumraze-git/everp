package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.Getter;

@Getter
public class QuotationConfirmRequestDto {
    private String quotationId; // 확인 대상 견적 ID (UUID)
}
