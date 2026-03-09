package org.ever._4ever_be_scm.api.scm.mm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.mm.dto.MMStatisticsResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.SupplierOrderStatisticsResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.ToggleCodeLabelDto;
import org.ever._4ever_be_scm.scm.mm.service.MMStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "구매관리", description = "구매 관리 API")
@ApiServerErrorResponse
public interface MMStatisticsApi {

    public ResponseEntity<MMStatisticsResponseDto> getMMStatistics();

    public ResponseEntity<List<ToggleCodeLabelDto>> getPurchaseRequisitionSearchTypeOptions();

    public ResponseEntity<List<ToggleCodeLabelDto>> getPurchaseOrderSearchTypeOptions();

    public ResponseEntity<List<ToggleCodeLabelDto>> getSupplierCategoryOptions();

    @Operation(summary = "공급업체별 주문 통계 조회", description = "공급업체 사용자 ID를 기준으로 해당 공급업체의 주문 통계를 조회합니다. 주, 월, 분기, 년 단위로 제공됩니다.")
    public ResponseEntity<SupplierOrderStatisticsResponseDto> getSupplierOrderStatistics(
        @PathVariable String userId
    );

}
