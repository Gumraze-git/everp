package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.entity.OrderItem;
import org.ever._4ever_be_business.order.repository.OrderItemRepository;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.ever._4ever_be_business.sd.integration.port.ProductServicePort;
import org.ever._4ever_be_business.sd.service.DashboardOrderService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardOrderServiceImpl implements DashboardOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductServicePort productServicePort;
    private final CustomerUserRepository customerUserRepository;

    @Override
    public List<DashboardWorkflowItemDto> getAllOrders(int size) {
        int limit = size > 0 ? Math.min(size, 20) : 5;

        var page = orderRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
        List<Order> orders = page.getContent();

        if (orders.isEmpty()) {
            log.info("[DASHBOARD][MOCK][SD][SO] 실데이터 없음 - 내부 주문서 목업 데이터 반환");
            return buildMockInternalOrders(limit);
        }

        Map<String, List<OrderItem>> itemsByOrder = mapOrderItems(orders);
        Map<String, ProductInfoResponseDto.ProductDto> productMap = buildProductMap(itemsByOrder);

        return IntStream.range(0, orders.size())
                .mapToObj(index -> {
                    Order order = orders.get(index);
                    return toDashboardItem(
                            order,
                            itemsByOrder.getOrDefault(order.getId(), List.of()),
                            productMap,
                            index
                    );
                })
                .toList();
    }

    private Map<String, List<OrderItem>> mapOrderItems(List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return Map.of();
        }

        List<String> orderIds = orders.stream()
                .map(Order::getId)
                .filter(Objects::nonNull)
                .toList();

        if (orderIds.isEmpty()) {
            return Map.of();
        }

        return orderItemRepository.findByOrder_IdIn(orderIds).stream()
                .filter(item -> item.getOrder() != null && item.getOrder().getId() != null)
                .collect(Collectors.groupingBy(item -> item.getOrder().getId()));
    }

    private Map<String, ProductInfoResponseDto.ProductDto> buildProductMap(Map<String, List<OrderItem>> itemsByOrder) {
        if (itemsByOrder.isEmpty()) {
            return Map.of();
        }

        Set<String> productIds = itemsByOrder.values().stream()
                .flatMap(List::stream)
                .map(OrderItem::getProductId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (productIds.isEmpty()) {
            return Map.of();
        }

        try {
            ProductInfoResponseDto response = productServicePort.getProductsByIds(new ArrayList<>(productIds));
            if (response == null || response.getProducts() == null) {
                log.warn("[DASHBOARD][SD][SO] 제품 정보 조회 결과가 비어 있습니다.");
                return Map.of();
            }

            return response.getProducts().stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(
                            ProductInfoResponseDto.ProductDto::getProductId,
                            Function.identity(),
                            (left, right) -> left
                    ));
        } catch (Exception ex) {
            log.warn("[DASHBOARD][SD][SO] 제품 정보 조회 실패: {}", ex.getMessage());
            return Map.of();
        }
    }

    private DashboardWorkflowItemDto toDashboardItem(
            Order order,
            List<OrderItem> items,
            Map<String, ProductInfoResponseDto.ProductDto> productMap,
            int index
    ) {
        String fallbackTitle = String.format("주문 %d", index + 1);
        String fallbackName = String.format("담당자 %d", index + 1);

        return DashboardWorkflowItemDto.builder()
                .itemId(order.getId())
                .itemTitle(buildItemTitle(items, productMap, fallbackTitle))
                .itemNumber(order.getOrderCode())
                .name(resolveCustomerName(order.getCustomerUserId(), fallbackName))
                .statusCode(order.getStatus() != null ? order.getStatus().name() : null)
                .date(order.getCreatedAt() != null ? order.getCreatedAt().toLocalDate().toString() : null)
                .build();
    }

    private String buildItemTitle(
            List<OrderItem> items,
            Map<String, ProductInfoResponseDto.ProductDto> productMap,
            String defaultTitle
    ) {
        if (items == null || items.isEmpty()) {
            return defaultTitle;
        }

        try {
            OrderItem representative = items.get(0);
            String baseName = Optional.ofNullable(representative.getProductId())
                    .map(productMap::get)
                    .map(ProductInfoResponseDto.ProductDto::getProductName)
                    .filter(name -> !name.isBlank())
                    .orElse(null);

            if (baseName == null || baseName.isBlank()) {
                return defaultTitle;
            }

            int extraCount = Math.max(0, items.size() - 1);
            return extraCount > 0 ? String.format("%s 외 %d건", baseName, extraCount) : baseName;
        } catch (Exception ex) {
            log.warn("[DASHBOARD][SD][SO] itemTitle 생성 실패: {}", ex.getMessage());
            return defaultTitle;
        }
    }

    private String resolveCustomerName(String customerUserId, String defaultName) {
        if (customerUserId == null || customerUserId.isBlank()) {
            return defaultName;
        }

        return customerUserRepository.findById(customerUserId)
                .map(CustomerUser::getCustomerName)
                .filter(name -> name != null && !name.isBlank())
                .orElse(defaultName);
    }

    private List<DashboardWorkflowItemDto> buildMockInternalOrders(int size) {
        int itemCount = Math.min(size > 0 ? size : 5, 5);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> DashboardWorkflowItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemTitle("내부 주문 " + (i + 1))
                        .itemNumber(String.format("SO-MOCK-%04d", i + 1))
                        .name("영업 담당자 " + (i + 1))
                        .statusCode(i % 2 == 0 ? "RELEASED" : "IN_PROGRESS")
                        .date(LocalDate.now().minusDays(i).toString())
                        .build())
                .toList();
    }
}
