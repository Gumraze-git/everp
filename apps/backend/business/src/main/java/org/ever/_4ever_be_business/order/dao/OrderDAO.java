package org.ever._4ever_be_business.order.dao;

import org.ever._4ever_be_business.order.entity.OrderItem;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderDetailResponseDto;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderListItemDto;
import org.ever._4ever_be_business.sd.vo.OrderSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderDAO {
    /**
     * 주문 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 주문 목록
     */
    Page<SalesOrderListItemDto> findOrderList(OrderSearchConditionVo condition, Pageable pageable);

    /**
     * 주문서 상세 조회
     *
     * @param salesOrderId 주문서 ID
     * @return 주문서 상세 정보 (Product 정보 제외)
     */
    SalesOrderDetailResponseDto findOrderDetailById(String salesOrderId);

    /**
     * 주문 항목 목록 조회
     *
     * @param salesOrderId 주문서 ID
     * @return 주문 항목 목록
     */
    List<OrderItem> findOrderItems(String salesOrderId);
}
