package org.ever._4ever_be_gw.business.dto.sd;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryCheckResponseDto {
    private List<InventoryCheckItemDto> items; // 품목별 재고 확인 결과
}

