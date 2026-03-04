package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대시보드용(공급사) 주문 목록 아이템 DTO
 * - GW의 DashboardWorkflowItemDto와 필드명을 동일하게 맞춘다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderWorkflowItemDto {
    private String itemId;      // = salesOrderId
    private String itemNumber;  // = salesOrderNumber
    private String itemTitle;   // = customerName
    private String name;        // = manager.managerName
    private String statusCode;  // = statusCode
    private String date;        // = orderDate (YYYY-MM-DD)
}

