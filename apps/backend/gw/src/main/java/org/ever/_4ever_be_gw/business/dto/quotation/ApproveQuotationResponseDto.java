package org.ever._4ever_be_gw.business.dto.quotation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveQuotationResponseDto {
    private String quotationId;         // 승인된 견적 ID (UUID)
    private String salesOrderId;        // 생성된 주문서 ID (UUID)
    private String salesOrderNumber;    // 주문 번호
    private String statusCode;          // READY_FOR_SHIPMENT 등
    private String approvedAt;          // ISO-8601
}

