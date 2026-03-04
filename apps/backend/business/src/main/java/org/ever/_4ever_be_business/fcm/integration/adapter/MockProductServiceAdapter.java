package org.ever._4ever_be_business.fcm.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.integration.dto.ProductMultipleResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.ProductsServicePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mock Product 서비스 Adapter
 * dev 환경에서 사용 (external.mock.enabled=true)
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockProductServiceAdapter implements ProductsServicePort {

    @Override
    public ProductMultipleResponseDto getProductsMultiple(List<String> productIds) {
        log.info("Mock Product 서비스 호출 - productIds: {}", productIds);

        List<ProductMultipleResponseDto.ProductDto> products = productIds.stream()
                .map(productId -> new ProductMultipleResponseDto.ProductDto(
                        productId,
                        "PROD-" + productId.substring(0, 8),
                        "Mock Product " + productId.substring(0, 8),
                        "EA",
                        BigDecimal.valueOf(10000)
                ))
                .collect(Collectors.toList());

        log.info("Mock Product 서비스 응답 - productCount: {}", products.size());

        return new ProductMultipleResponseDto(products);
    }
}
