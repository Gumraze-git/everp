package org.ever._4ever_be_business.sd.integration.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.mock.MockDataProvider;
import org.ever._4ever_be_business.sd.dto.request.InventoryCheckRequestDto;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResponseDto;
import org.ever._4ever_be_business.sd.integration.port.InventoryServicePort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * InventoryServicePort의 Mock 구현체
 * dev 환경에서 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "external.mock.enabled", havingValue = "true", matchIfMissing = true)
public class MockInventoryServiceAdapter implements InventoryServicePort {

    private final MockDataProvider mockDataProvider;

    @Override
    public InventoryCheckResponseDto checkInventory(InventoryCheckRequestDto requestDto) {
        log.info("[MOCK ADAPTER] checkInventory 호출 - items count: {}", requestDto.getItems() != null ? requestDto.getItems().size() : 0);

        var itemIds = requestDto.getItems() != null ?
                requestDto.getItems().stream()
                        .map(item -> item.getItemId())
                        .collect(Collectors.toList()) :
                new ArrayList<String>();

        return mockDataProvider.createMockInventoryCheck(itemIds);
    }
}
