package org.ever._4ever_be_business.sd.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.mock.MockDataProvider;
import org.ever._4ever_be_business.sd.integration.dto.ProductInfoResponseDto;
import org.ever._4ever_be_business.sd.integration.port.ProductServicePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ProductServicePort의 Mock 구현체
 * dev 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockProductServiceAdapter implements ProductServicePort {

    private final MockDataProvider mockDataProvider;

    @Override
    public ProductInfoResponseDto getProductsByIds(List<String> productIds) {
        log.info("[MOCK ADAPTER] getProductsByIds 호출 - productIds: {}", productIds);
        return mockDataProvider.createMockProductInfo(productIds);
    }
}
