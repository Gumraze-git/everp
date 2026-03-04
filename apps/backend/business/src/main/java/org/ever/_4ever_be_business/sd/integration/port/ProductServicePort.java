package org.ever._4ever_be_business.sd.integration.port;

import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;

import java.util.List;

/**
 * SCM 서버의 Product 서비스와 통신하기 위한 Port 인터페이스
 */
public interface ProductServicePort {
    /**
     * 여러 Product ID로 Product 정보 조회
     *
     * @param productIds Product ID 리스트
     * @return Product 정보 리스트
     */
    ProductInfoResponseDto getProductsByIds(List<String> productIds);
}
