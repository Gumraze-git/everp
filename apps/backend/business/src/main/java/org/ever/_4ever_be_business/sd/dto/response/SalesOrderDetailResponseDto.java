package org.ever._4ever_be_business.sd.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderDetailResponseDto {
    private OrderDetailDto order;
    private OrderCustomerDto customer;
    private List<OrderItemDto> items;
    private String note;
}
