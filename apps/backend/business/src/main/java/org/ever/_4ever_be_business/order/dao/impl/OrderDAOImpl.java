package org.ever._4ever_be_business.order.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.order.dao.OrderDAO;
import org.ever._4ever_be_business.order.entity.OrderItem;
import org.ever._4ever_be_business.order.repository.OrderItemRepository;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderDetailResponseDto;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderListItemDto;
import org.ever._4ever_be_business.sd.vo.OrderSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderDAOImpl implements OrderDAO {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Page<SalesOrderListItemDto> findOrderList(OrderSearchConditionVo condition, Pageable pageable) {
        return orderRepository.findOrderList(condition, pageable);
    }

    @Override
    public SalesOrderDetailResponseDto findOrderDetailById(String salesOrderId) {
        return orderRepository.findOrderDetailById(salesOrderId);
    }

    @Override
    public List<OrderItem> findOrderItems(String salesOrderId) {
        return orderItemRepository.findByOrderId(salesOrderId);
    }
}
