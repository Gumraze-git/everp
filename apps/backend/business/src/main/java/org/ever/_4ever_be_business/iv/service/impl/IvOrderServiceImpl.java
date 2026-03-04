package org.ever._4ever_be_business.iv.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.iv.dto.request.IvOrderRequestDto;
import org.ever._4ever_be_business.iv.dto.response.IvOrderResponseDto;
import org.ever._4ever_be_business.iv.service.IvOrderService;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IvOrderServiceImpl implements IvOrderService {

    private final OrderRepository orderRepository;

    @Override
    public IvOrderResponseDto getOrder(IvOrderRequestDto request) {
        log.info("IV 주문서 조회 - orderId: {}", request.getOrderId());

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문서를 찾을 수 없습니다. orderId: " + request.getOrderId()));

        String orderStatusLabel = getOrderStatusLabel(order.getStatus().name());

        IvOrderResponseDto response = new IvOrderResponseDto(
                order.getId(),
                order.getOrderCode(),
                orderStatusLabel
        );

        log.info("IV 주문서 조회 성공 - orderCode: {}", order.getOrderCode());
        return response;
    }

    private String getOrderStatusLabel(String status) {
        return switch (status) {
            case "PENDING" -> "대기";
            case "CONFIRMED" -> "확정";
            case "IN_PRODUCTION" -> "생산중";
            case "READY_FOR_SHIPMENT" -> "출고 준비";
            case "SHIPPED" -> "출고 완료";
            case "DELIVERED" -> "배송 완료";
            case "CANCELLED" -> "취소";
            default -> "알 수 없음";
        };
    }
}
