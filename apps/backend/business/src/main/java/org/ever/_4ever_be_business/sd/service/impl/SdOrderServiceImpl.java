package org.ever._4ever_be_business.sd.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.order.dao.OrderDAO;
import org.ever._4ever_be_business.order.entity.OrderItem;
import org.ever._4ever_be_business.sd.dto.response.*;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.ever._4ever_be_business.sd.integration.port.ProductServicePort;
import org.ever._4ever_be_business.sd.service.SdOrderService;
import org.ever._4ever_be_business.sd.vo.OrderSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SdOrderServiceImpl implements SdOrderService {

    private final OrderDAO orderDAO;
    private final ProductServicePort productServicePort;

    @Override
    @Transactional(readOnly = true)
    public SalesOrderListResponseDto getOrderList(OrderSearchConditionVo condition, Pageable pageable) {
        log.info("주문 목록 조회 요청 - startDate: {}, endDate: {}, status: {}, type: {}, search: {}",
                condition.getStartDate(), condition.getEndDate(), condition.getStatus(),
                condition.getType(), condition.getSearch());

        Page<SalesOrderListItemDto> page = orderDAO.findOrderList(condition, pageable);

        // Page 객체를 SalesOrderListResponseDto로 변환
        PageInfo pageInfo = new PageInfo(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );

        SalesOrderListResponseDto result = new SalesOrderListResponseDto(
                page.getContent(),
                pageInfo
        );

        log.info("주문 목록 조회 성공 - totalElements: {}, totalPages: {}",
                page.getTotalElements(), page.getTotalPages());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public SalesOrderDetailResponseDto getOrderDetail(String salesOrderId) {
        log.info("주문서 상세 조회 요청 - salesOrderId: {}", salesOrderId);

        // 1. 기본 주문서 정보 조회 (Customer 포함, items 제외)
        SalesOrderDetailResponseDto orderDetail = orderDAO.findOrderDetailById(salesOrderId);

        if (orderDetail == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }

        // 2. OrderItem 목록 조회
        List<OrderItem> orderItems = orderDAO.findOrderItems(salesOrderId);

        // 3. Product 정보 조회를 위한 productId 목록 추출
        List<String> productIds = orderItems.stream()
                .map(item -> String.valueOf(item.getProductId()))
                .collect(Collectors.toList());

        // 4. SCM Product Service에서 Product 정보 조회
        ProductInfoResponseDto productInfo = productServicePort.getProductsByIds(productIds);

        // 5. Product 정보를 Map으로 변환 (빠른 조회를 위해)
        Map<String, ProductInfoResponseDto.ProductDto> productMap = productInfo.getProducts().stream()
                .collect(Collectors.toMap(
                        ProductInfoResponseDto.ProductDto::getProductId,
                        product -> product
                ));

        // 6. OrderItem + Product 정보를 OrderItemDto로 변환
        List<OrderItemDto> items = orderItems.stream()
                .map(orderItem -> {
                    String productId = String.valueOf(orderItem.getProductId());
                    ProductInfoResponseDto.ProductDto product = productMap.get(productId);

                    // Unit enum을 한글 단위명으로 변환
                    String uonName = convertUnitToKorean(orderItem.getUnit().name());

                    // 단가와 총액 계산
                    BigDecimal unitPrice = BigDecimal.valueOf(orderItem.getPrice());
                    BigDecimal quantity = BigDecimal.valueOf(orderItem.getCount());
                    BigDecimal amount = unitPrice.multiply(quantity);

                    return new OrderItemDto(
                            orderItem.getProductId(),  // itemId
                            product != null ? product.getProductName() : "Unknown",  // itemName
                            orderItem.getCount(),                                     // quantity
                            uonName,                                                  // uonName
                            unitPrice,                                                // unitPrice
                            amount                                                    // amount
                    );
                })
                .collect(Collectors.toList());

        // 7. 최종 응답 생성 (items 포함)
        SalesOrderDetailResponseDto result = new SalesOrderDetailResponseDto(
                orderDetail.getOrder(),
                orderDetail.getCustomer(),
                items,
                orderDetail.getNote()
        );

        log.info("주문서 상세 조회 성공 - salesOrderId: {}, items count: {}", salesOrderId, items.size());

        return result;
    }

    /**
     * Unit enum을 한글 단위명으로 변환
     */
    private String convertUnitToKorean(String unit) {
        return switch (unit) {
            case "EA" -> "개";
            case "DOZEN" -> "다스";
            case "PACK" -> "팩";
            case "BOX" -> "박스";
            case "CASE" -> "케이스";
            case "SET" -> "세트";
            case "PAIR" -> "켤레";
            case "ROLL" -> "롤";
            case "SHEET" -> "장";
            case "PALLET" -> "팔레트";
            case "BUNDLE" -> "번들";
            case "BAG" -> "포대";
            case "KG" -> "kg";
            case "G" -> "g";
            case "MG" -> "mg";
            case "TON" -> "톤";
            case "LB" -> "파운드";
            case "OZ" -> "온스";
            case "L" -> "리터";
            case "ML" -> "ml";
            case "CL" -> "cl";
            case "M3" -> "㎥";
            case "CM3" -> "㎤";
            case "GAL" -> "갤런";
            case "QT" -> "쿼트";
            case "PT" -> "파인트";
            case "FLOZ" -> "fl oz";
            case "M" -> "m";
            case "CM" -> "cm";
            case "MM" -> "mm";
            case "KM" -> "km";
            case "INCH" -> "인치";
            case "FT" -> "피트";
            case "YD" -> "야드";
            case "M2" -> "㎡";
            case "CM2" -> "㎠";
            case "MM2" -> "㎟";
            case "KM2" -> "㎢";
            case "FT2" -> "평방피트";
            case "YD2" -> "평방야드";
            case "SEC" -> "초";
            case "MIN" -> "분";
            case "HOUR" -> "시간";
            case "DAY" -> "일";
            default -> unit;
        };
    }
}
