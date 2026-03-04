package org.ever._4ever_be_business.sd.integration.port;

import org.ever._4ever_be_business.sd.dto.request.InventoryCheckRequestDto;
import org.ever._4ever_be_business.sd.dto.response.InventoryCheckResponseDto;

/**
 * SCM Inventory 서비스 통신 포트
 */
public interface InventoryServicePort {
    /**
     * 재고 확인
     *
     * @param requestDto 재고 확인 요청
     * @return 재고 확인 결과
     */
    InventoryCheckResponseDto checkInventory(InventoryCheckRequestDto requestDto);
}
