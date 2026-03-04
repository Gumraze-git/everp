package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuotationListItemDto {
    private String quotationId;         // 견적 ID
    private String quotationNumber;     // 견적 번호 (예: QO-2024-001)
    private String customerName;        // 고객사명
    private String managerName;         // 담당자명
    private String quotationDate;       // 견적일 (YYYY-MM-DD)
    private String dueDate;             // 납기일 (YYYY-MM-DD)
    private Long totalAmount;           // 총액
    private String statusCode;          // PENDING, REVIEW, APPROVAL, REJECTED
}

