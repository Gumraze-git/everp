package org.ever._4ever_be_scm.api.scm.iv;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.iv.dto.response.ImStatisticResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.response.ShortageStatisticResponseDto;
import org.ever._4ever_be_scm.scm.iv.dto.response.WarehouseStatisticResponseDto;
import org.ever._4ever_be_scm.scm.iv.service.IvStatisticService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재고관리", description = "재고 관리 통계 API")
@ApiServerErrorResponse
public interface IvStaticApi {

    @Operation(summary = "재고 부족 통계 조회", description = "재고 부족 관리 통계 정보를 반환합니다. URGENT는 total_emergency, CAUTION은 total_warning으로 분류")
    public ResponseEntity<ShortageStatisticResponseDto> getShortageStatistic();

    @Operation(summary = "창고 통계 조회", description = "창고 관리 통계를 반환합니다. 총 창고 개수와 운영중인 창고 개수 포함")
    public ResponseEntity<WarehouseStatisticResponseDto> getWarehouseStatistic();

}
