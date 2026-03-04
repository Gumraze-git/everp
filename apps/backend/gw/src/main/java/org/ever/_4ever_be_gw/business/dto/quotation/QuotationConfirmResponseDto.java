package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationConfirmResponseDto {
    private String quotationId;        // 확인 대상 견적 ID (UUID)
    private String statusCode;  // REVIEW 등
    private String requestedAt; // ISO-8601 문자열
}

