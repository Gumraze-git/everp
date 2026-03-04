package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationCreateResponseDto {
    private String quotationId;       // 생성된 견적 ID (UUID v7)
    private String quotationDate;     // 생성일(YYYY-MM-DD)
    private String dueDate;           // 요청 납기일(YYYY-MM-DD)
    private String statusCode;        // PENDING 등
    private Long totalAmount;         // 총액
}