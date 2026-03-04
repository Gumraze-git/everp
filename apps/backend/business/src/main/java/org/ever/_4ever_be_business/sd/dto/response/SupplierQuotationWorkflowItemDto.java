package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대시보드용(공급사) 발주서 목록 아이템 DTO
 * - GW의 DashboardWorkflowItemDto와 동일한 필드 구성
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierQuotationWorkflowItemDto {
    private String itemId;      // = quotationId
    private String itemNumber;  // = quotationNumber
    private String itemTitle;   // = customerName
    private String name;        // 담당자명(옵션, 없으면 빈 문자열)
    private String statusCode;  // 상태 코드
    private String date;        // = requestDate (YYYY-MM-DD)
}

