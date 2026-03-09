package org.ever._4ever_be_scm.scm.pp.controller;

import org.ever._4ever_be_scm.api.scm.pp.QuotationApi;
import lombok.RequiredArgsConstructor;
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

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/scm-pp/pp/quotations")
@RequiredArgsConstructor
public class QuotationController implements QuotationApi {

    private final QuotationService quotationService;

    /**
     * 견적 목록 조회 (그룹핑된 형태)
     */
    @GetMapping

    public ResponseEntity<QuotationGroupListResponseDto> getQuotationList(

            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(required = false) String availableStatus,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size) {

        QuotationGroupListResponseDto result = quotationService.getQuotationList(
                statusCode, availableStatus, startDate, endDate, page, size);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/simulations/search")
    public ResponseEntity<PagedResponseDto<QuotationSimulateResponseDto>> simulateQuotations(
            @RequestBody QuotationSimulateRequestDto requestDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<QuotationSimulateResponseDto> simulationResults = quotationService.simulateQuotations(
            requestDto, PageRequest.of(page, size));
        PagedResponseDto<QuotationSimulateResponseDto> response = PagedResponseDto.from(simulationResults);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/mps-previews")
    public ResponseEntity<List<MpsPreviewResponseDto>> previewMps(
            @RequestBody List<String> quotationIds) {

        List<MpsPreviewResponseDto> mpsPreview = quotationService.previewMps(quotationIds);

        return ResponseEntity.ok(mpsPreview);
    }

    @PostMapping("/reviews")
    public DeferredResult<ResponseEntity<?>> confirmQuotations(
            @RequestBody QuotationConfirmRequestDto requestDto) {

        return quotationService.confirmQuotationsAsync(requestDto);
    }

    /**
     * MPS 조회 (주차별)
     */
    @GetMapping("/mps")

    public ResponseEntity<MpsQueryResponseDto> getMps(

            @RequestParam String bomId,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "7") int size) {

        MpsQueryResponseDto mpsData = quotationService.getMps(bomId, startDate, endDate, page, size);

        return ResponseEntity.ok(mpsData);
    }

    /**
     * MRP 조회 (자재 조달 계획)
     */
    @GetMapping("/mrp")

    public ResponseEntity<PagedResponseDto<MrpQueryResponseDto.MrpItemDto>> getMrp(

            @RequestParam(required = false) String bomId,

            @RequestParam(required = false) String quotationId,

            @RequestParam(required = false, defaultValue = "ALL") String availableStatusCode,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size) {

        Page<MrpQueryResponseDto.MrpItemDto> mrpData = quotationService.getMrp(bomId, quotationId, availableStatusCode, page, size);
        PagedResponseDto<MrpQueryResponseDto.MrpItemDto> response = PagedResponseDto.from(mrpData);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/status-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getQuotationStatusOptions() {
        List<ToggleCodeLabelDto> list = List.of(
                new ToggleCodeLabelDto("전체 상태", "ALL"),
                new ToggleCodeLabelDto("승인", "APPROVAL"),
                new ToggleCodeLabelDto("검토중", "REVIEW")
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/available-status-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getQuotationAvailableStatusOptions() {
        List<ToggleCodeLabelDto> list = List.of(
                new ToggleCodeLabelDto("전체 확인 상태", "ALL"),
                new ToggleCodeLabelDto("확인", "CHECKED"),
                new ToggleCodeLabelDto("미확인", "UNCHECKED")
        );
        return ResponseEntity.ok(list);
    }

    @GetMapping("/bom-options")

    public ResponseEntity<List<ToggleCodeLabelDto>> getBomList() {
        List<ToggleCodeLabelDto> result = quotationService.getBomList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/mrp/quotation-options")

    public ResponseEntity<List<ToggleCodeLabelDto>> getMrpQuotationList() {
        List<ToggleCodeLabelDto> result = quotationService.getMrpQuotationList();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/mrp/available-status-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getMrpAvailableStatusOptions() {
        List<ToggleCodeLabelDto> list = List.of(
                new ToggleCodeLabelDto("전체 상태", "ALL"),
                new ToggleCodeLabelDto("충족", "SUFFICIENT"),
                new ToggleCodeLabelDto("미충족", "INSUFFICIENT")
        );
        return ResponseEntity.ok(list);
    }
}
