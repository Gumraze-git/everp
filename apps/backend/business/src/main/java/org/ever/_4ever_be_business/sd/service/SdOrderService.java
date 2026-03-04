package org.ever._4ever_be_business.sd.service;

import org.ever._4ever_be_business.sd.dto.response.SalesOrderDetailResponseDto;
import org.ever._4ever_be_business.sd.dto.response.SalesOrderListResponseDto;
import org.ever._4ever_be_business.sd.vo.OrderSearchConditionVo;
import org.springframework.data.domain.Pageable;

public interface SdOrderService {
    /**
     * 주문 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 주문 목록 및 페이징 정보
     */
    SalesOrderListResponseDto getOrderList(OrderSearchConditionVo condition, Pageable pageable);

    /**
     * 주문서 상세 조회
     *
     * @param salesOrderId 주문서 ID
     * @return 주문서 상세 정보
     */
    SalesOrderDetailResponseDto getOrderDetail(String salesOrderId);
}
