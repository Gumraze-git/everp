package org.ever._4ever_be_scm.api.scm.pp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.mm.dto.ToggleCodeLabelDto;
import org.ever._4ever_be_scm.scm.pp.dto.MrpRunConvertRequestDto;
import org.ever._4ever_be_scm.scm.pp.dto.MrpRunQueryResponseDto;
import org.ever._4ever_be_scm.scm.pp.service.MrpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MRP 관리", description = "MRP 계획주문 관리 API")
@ApiServerErrorResponse
public interface MrpApi {

    @Operation(summary = "계획주문 전환", description = "선택한 MRP 자재들을 계획주문(MRP_RUN)으로 전환합니다.")
    public ResponseEntity<Void> convertToMrpRun(
            @RequestBody MrpRunConvertRequestDto requestDto);

    @Operation(summary = "계획주문 목록 조회", description = "MRP 계획주문 목록을 조회합니다. 상태별 필터링이 가능합니다.")
    public ResponseEntity<MrpRunQueryResponseDto> getMrpRunList(
            
            @RequestParam(defaultValue = "ALL") String status,
            
            @RequestParam(required = false) String quotationId,
            
            @RequestParam(defaultValue = "0") int page,
            
            @RequestParam(defaultValue = "10") int size);

    @Operation(summary = "MRP Run 상태 목록 조회", description = "MRP Run 상태 필터용 토글 목록을 조회합니다.")
    public ResponseEntity<List<ToggleCodeLabelDto>> getMrpRunStatusOptions();

}
