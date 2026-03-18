package org.ever._4ever_be_business.fcm.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.mock.MockDataProvider;
import org.ever._4ever_be_business.fcm.integration.dto.ProductMultipleResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.ProductsServicePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mock Product 서비스 Adapter
 * dev 환경에서 사용 (external.mock.enabled=true)
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockProductServiceAdapter implements ProductsServicePort {

    private final MockDataProvider mockDataProvider;

    @Override
    public ProductMultipleResponseDto getProductsMultiple(List<String> productIds) {
        log.info("Mock Product 서비스 호출 - productIds: {}", productIds);
        ProductMultipleResponseDto response = mockDataProvider.createMockProductsMultiple(productIds);
        log.info("Mock Product 서비스 응답 - productCount: {}", response.getProducts().size());
        return response;
    }
}
