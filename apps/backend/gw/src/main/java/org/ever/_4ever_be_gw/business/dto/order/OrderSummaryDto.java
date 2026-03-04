package org.ever._4ever_be_gw.business.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {
    private String salesOrderId;             // 주문서 ID
    private String salesOrderNumber;         // 예: SO-______ -> uuid v7 id의 앞 6자리
    private String orderDate;                // 주문일(yyyy-MM-dd)
    private String dueDate;                  // 납기일(yyyy-MM-dd)
    private String statusCode;               // MATERIAL_PREPARATION, PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED, ALL
    private Long totalAmount;                // 총액
}
