package org.ever._4ever_be_gw.business.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderDetailDto {
    private OrderSummaryDto order;
    private CustomerSummaryDto customer;
    private List<OrderItemDto> items;
    private String note;
}

