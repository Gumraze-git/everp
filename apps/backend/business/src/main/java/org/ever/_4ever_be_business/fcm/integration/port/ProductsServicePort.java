package org.ever._4ever_be_business.fcm.integration.port;

import org.ever._4ever_be_business.fcm.integration.dto.ProductMultipleResponseDto;

import java.util.List;

/**
 * SCM 서버의 Product 서비스와 통신하기 위한 Port 인터페이스
 */
public interface ProductsServicePort {
    /**
     * ProductId 리스트로 Product 정보 조회
     *
     * @param productIds Product ID 리스트
     * @return Product 정보 리스트
     */
    ProductMultipleResponseDto getProductsMultiple(List<String> productIds);
}
