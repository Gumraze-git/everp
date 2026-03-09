package org.ever._4ever_be_scm.api.scm.pp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.ToggleCodeLabelDto;
import org.ever._4ever_be_scm.scm.pp.dto.MesDetailResponseDto;
import org.ever._4ever_be_scm.scm.pp.dto.MesQueryResponseDto;
import org.ever._4ever_be_scm.scm.pp.service.MesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@Tag(name = "MES 관리", description = "작업 지시 관리 API")
@ApiServerErrorResponse
public interface MesApi {

    @Operation(summary = "MES 목록 조회", description = "작업 지시(MES) 목록을 조회합니다. 견적ID와 상태로 필터링할 수 있습니다.")
    public ResponseEntity<PagedResponseDto<MesQueryResponseDto.MesItemDto>> getMesList(
            
            @RequestParam(required = false) String quotationId,
            
            @RequestParam(defaultValue = "ALL") String status,
            
            @RequestParam(defaultValue = "0") int page,
            
            @RequestParam(defaultValue = "20") int size);

    @Operation(summary = "MES 상세 조회", description = "작업 지시(MES)의 상세 정보와 공정별 진행 상태를 조회합니다.")
    public ResponseEntity<MesDetailResponseDto> getMesDetail(
            
            @PathVariable String mesId);

    @Operation(summary = "MES 시작", description = "PENDING 상태의 MES를 시작하고 자재를 소비합니다.")
    public DeferredResult<ResponseEntity<?>> startMes(
            
            @PathVariable String mesId,
            @RequestParam String requesterId
            );

    @Operation(summary = "공정 시작", description = "특정 공정을 시작합니다. 이전 공정들이 모두 완료되어야 합니다. logId는 MesOperationLog의 ID입니다.")
    public ResponseEntity<Void> startOperation(
            
            @PathVariable String mesId,
            
            @PathVariable String logId,
            
            @RequestParam(required = false) String managerId);

    @Operation(summary = "공정 완료", description = "IN_PROGRESS 상태의 공정을 완료하고 진행률을 업데이트합니다. logId는 MesOperationLog의 ID입니다.")
    public ResponseEntity<Void> completeOperation(
            
            @PathVariable String mesId,
            
            @PathVariable String logId);

    @Operation(summary = "MES 완료", description = "모든 공정이 완료된 MES를 완료 처리하고 완제품 재고를 증가시킵니다.")
    public DeferredResult<ResponseEntity<?>> completeMes(
            
            @PathVariable String mesId,
            @RequestParam String requesterId
            );

    public ResponseEntity<List<ToggleCodeLabelDto>> getMesStatusOptions();

}
