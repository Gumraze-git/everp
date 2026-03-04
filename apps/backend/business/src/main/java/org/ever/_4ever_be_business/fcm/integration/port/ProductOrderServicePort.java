package org.ever._4ever_be_business.fcm.integration.port;

import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfoResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfosResponseDto;

import java.util.List;

/**
 * SCM 서버의 ProductOrder 서비스와 통신하기 위한 Port 인터페이스
 */
public interface ProductOrderServicePort {
    /**
     * ProductOrder ID로 ProductOrder 아이템 정보 조회
     *
     * @param productOrderId ProductOrder ID
     * @return ProductOrder 아이템 정보
     */
    ProductOrderInfoResponseDto getProductOrderItemsById(String productOrderId);

    /**
     * 여러 ProductOrder ID로 ProductOrder 정보 조회 (totalAmount만)
     *
     * @param productOrderIds ProductOrder ID 목록
     * @return ProductOrder 정보 목록
     */
    List<ProductOrderInfosResponseDto.ProductOrderInfoItem> getProductOrderInfosByIds(List<String> productOrderIds);
}
