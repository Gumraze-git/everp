package org.ever._4ever_be_gw.business.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderListItemDto {
    private String salesOrderId;           // soId (목록용)
    private String salesOrderNumber;     // SO-2024-001
    private String customerName;        // 고객사 이름
    private ManagerDto manager;         // { managerName, managerPhone, managerEmail }
    private String orderDate;           // 주문일(yyyy-MM-dd)
    private String dueDate;             // 납기일(yyyy-MM-dd)
    private Long totalAmount;           // 총 주문 금액
    private String statusCode;          // MATERIAL_PREPARATION, PRODUCTION, READY_FOR_SHIPMENT, DELIVERING, DELIVERED
}

