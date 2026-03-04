package org.ever._4ever_be_gw.business.dto.sd;

import java.util.List;
import lombok.Getter;

@Getter
public class InventoryCheckRequestDto {
    private List<InventoryCheckItemRequestDto> items; // 요청 품목 목록 (1..200)
}

