package org.ever._4ever_be_business.fcm.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.mock.MockDataProvider;
import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfoResponseDto;
import org.ever._4ever_be_business.fcm.integration.dto.ProductOrderInfosResponseDto;
import org.ever._4ever_be_business.fcm.integration.port.ProductOrderServicePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ProductOrderServicePort의 Mock 구현체
 * dev 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockProductOrderServiceAdapter implements ProductOrderServicePort {

    private final MockDataProvider mockDataProvider;

    @Override
    public ProductOrderInfoResponseDto getProductOrderItemsById(String productOrderId) {
        log.info("[MOCK ADAPTER] getProductOrderItemsById 호출 - productOrderId: {}", productOrderId);
        return mockDataProvider.createMockProductOrderItems(productOrderId);
    }

    @Override
    public List<ProductOrderInfosResponseDto.ProductOrderInfoItem> getProductOrderInfosByIds(List<String> productOrderIds) {
        log.info("[MOCK ADAPTER] getProductOrderInfosByIds 호출 - productOrderIds: {}", productOrderIds);
        return mockDataProvider.createMockProductOrderInfos(productOrderIds);
    }
}
