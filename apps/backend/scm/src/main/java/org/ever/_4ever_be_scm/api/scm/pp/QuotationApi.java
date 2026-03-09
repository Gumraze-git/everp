package org.ever._4ever_be_scm.api.scm.pp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.ToggleCodeLabelDto;
import org.ever._4ever_be_scm.scm.pp.dto.*;
import org.ever._4ever_be_scm.scm.pp.service.QuotationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@Tag(name = "생산관리", description = "생산 관리 API")
@ApiServerErrorResponse
public interface QuotationApi {

    @Operation(summary = "견적 목록 조회", description = "견적 목록을 그룹핑하여 조회합니다. 같은 견적번호의 여러 품목은 하나로 그룹핑됩니다.")
    public ResponseEntity<QuotationGroupListResponseDto> getQuotationList(
            
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) String availableStatus,
            
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @RequestParam(defaultValue = "0") int page,
            
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<PagedResponseDto<QuotationSimulateResponseDto>> simulateQuotations(
            @RequestBody QuotationSimulateRequestDto requestDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<List<MpsPreviewResponseDto>> previewMps(
            @RequestBody List<String> quotationIds);

    public DeferredResult<ResponseEntity<?>> confirmQuotations(
            @RequestBody QuotationConfirmRequestDto requestDto);

    @Operation(summary = "MPS 조회", description = "BOM ID를 기준으로 주차별 생산 계획(MPS)을 조회합니다. startDate 앞 3주차부터 조회되며 최소 7주차를 보장합니다.")
    public ResponseEntity<MpsQueryResponseDto> getMps(
            
            @RequestParam String bomId,
            
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @RequestParam(defaultValue = "0") int page,
            
            @RequestParam(defaultValue = "7") int size);

    @Operation(summary = "MRP 조회", description = "자재 소요 계획(MRP)을 조회합니다. 원자재별로 그룹핑되어 필요량이 합산되며, bomId 또는 quotationId로 필터링할 수 있습니다.")
    public ResponseEntity<PagedResponseDto<MrpQueryResponseDto.MrpItemDto>> getMrp(
            
            @RequestParam(required = false) String bomId,
            
            @RequestParam(required = false) String quotationId,
            
            @RequestParam(required = false, defaultValue = "ALL") String availableStatusCode,
            
            @RequestParam(defaultValue = "0") int page,
            
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<List<ToggleCodeLabelDto>> getQuotationStatusOptions();

    @Operation(summary = "BOM 목록 조회", description = "BOM 목록을 조회합니다. bomId를 key로, productName을 value로 반환합니다.")
    public ResponseEntity<List<ToggleCodeLabelDto>> getBomList();

    public ResponseEntity<List<ToggleCodeLabelDto>> getMrpAvailableStatusOptions();

}
