package org.ever._4ever_be_gw.scm.pp.controller;

import org.ever._4ever_be_gw.api.scm.pp.PpApi;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.scm.pp.PpHttpService;
import org.ever._4ever_be_gw.scm.pp.dto.BomCreateRequestDto;
import org.ever._4ever_be_gw.scm.pp.dto.MrpRunConvertRequestDto;
import org.ever._4ever_be_gw.scm.pp.dto.QuotationConfirmRequestDto;
import org.ever._4ever_be_gw.scm.pp.dto.QuotationSimulateRequestDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriBuilder;


@RestController
@RequestMapping("/scm-pp/pp")
@RequiredArgsConstructor
public class PpController implements PpApi {

    private final PpHttpService ppHttpService;

    @PostMapping("/boms")
    public ResponseEntity<Object> createBom(@RequestBody BomCreateRequestDto requestDto) {
        return ppHttpService.post("BOM 생성", "/scm-pp/pp/boms", requestDto);
    }

    @GetMapping("/boms")
    public ResponseEntity<Object> getBomList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ppHttpService.get(
            "BOM 목록 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/pp/boms")
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
        );
    }

    @GetMapping("/boms/{bomId}")
    public ResponseEntity<Object> getBomDetail(@PathVariable String bomId) {
        return ppHttpService.get("BOM 상세 조회", "/scm-pp/pp/boms/{bomId}", bomId);
    }

    @PatchMapping("/boms/{bomId}")
    public ResponseEntity<Object> updateBom(@PathVariable String bomId, @RequestBody BomCreateRequestDto requestDto) {
        return ppHttpService.patch("BOM 수정", "/scm-pp/pp/boms/{bomId}", requestDto, bomId);
    }

    @GetMapping("/mes")
    public ResponseEntity<Object> getMesList(
            @RequestParam(required = false) String quotationId,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ppHttpService.get(
            "MES 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder
                    .path("/scm-pp/pp/mes")
                    .queryParam("status", status)
                    .queryParam("page", page)
                    .queryParam("size", size);
                builder = addQueryParamIfPresent(builder, "quotationId", quotationId);
                return builder.build();
            }
        );
    }

    @GetMapping("/mes/status-options")
    public ResponseEntity<Object> getMesStatusDetail() {
        return ppHttpService.get("MES 상태 옵션 조회", "/scm-pp/pp/mes/status-options");
    }

    @GetMapping("/mes/{mesId}")
    public ResponseEntity<Object> getMesDetail(@PathVariable String mesId) {
        return ppHttpService.get("MES 상세 조회", "/scm-pp/pp/mes/{mesId}", mesId);
    }

    @PostMapping("/mes/{mesId}/starts")
    public ResponseEntity<Object> startMes(
            @PathVariable String mesId,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return ppHttpService.postWithoutBody(
            "MES 시작",
            uriBuilder -> uriBuilder
                .path("/scm-pp/pp/mes/{mesId}/starts")
                .queryParam("requesterId", principal.getUserId())
                .build(mesId)
        );
    }

    @PostMapping("/mes/{mesId}/operations/{operationId}/starts")
    public ResponseEntity<Object> startOperation(
            @PathVariable String mesId,
            @PathVariable String operationId,
            @AuthenticationPrincipal EverUserPrincipal everUserPrincipal) {
        return ppHttpService.postWithoutBody(
            "공정 시작",
            uriBuilder -> uriBuilder
                .path("/scm-pp/pp/mes/{mesId}/operations/{logId}/starts")
                .queryParam("managerId", everUserPrincipal.getUserId())
                .build(mesId, operationId)
        );
    }

    @PostMapping("/mes/{mesId}/operations/{operationId}/completions")
    public ResponseEntity<Object> completeOperation(
            @PathVariable String mesId,
            @PathVariable String operationId) {
        return ppHttpService.postWithoutBody(
            "공정 완료",
            "/scm-pp/pp/mes/{mesId}/operations/{logId}/completions",
            mesId,
            operationId
        );
    }

    @PostMapping("/mes/{mesId}/completions")
    public ResponseEntity<Object> completeMes(
            @PathVariable String mesId,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        return ppHttpService.postWithoutBody(
            "MES 완료",
            uriBuilder -> uriBuilder
                .path("/scm-pp/pp/mes/{mesId}/completions")
                .queryParam("requesterId", principal.getUserId())
                .build(mesId)
        );
    }

    @PostMapping("/mrp-runs")
    @PreAuthorize("hasAnyAuthority('PP_USER', 'PP_ADMIN', 'ALL_ADMIN')")
    public ResponseEntity<Object> convertToMrpRun(@RequestBody MrpRunConvertRequestDto requestDto) {
        return ppHttpService.post("MRP RUN 생성", "/scm-pp/pp/mrp-runs", requestDto);
    }

    @GetMapping("/mrp-runs")
    public ResponseEntity<Object> getMrpRunList(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String quotationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ppHttpService.get(
            "MRP RUN 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder
                    .path("/scm-pp/pp/mrp-runs")
                    .queryParam("status", status)
                    .queryParam("page", page)
                    .queryParam("size", size);
                builder = addQueryParamIfPresent(builder, "quotationId", quotationId);
                return builder.build();
            }
        );
    }

    @GetMapping("/quotations")
    public ResponseEntity<Object> getQuotationList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(defaultValue = "ALL") String availableStatusCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ppHttpService.get(
            "견적 목록 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder
                    .path("/scm-pp/pp/quotations")
                    .queryParam("availableStatus", availableStatusCode)
                    .queryParam("statusCode", statusCode)
                    .queryParam("page", page)
                    .queryParam("size", size);
                builder = addQueryParamIfPresent(builder, "startDate", startDate);
                builder = addQueryParamIfPresent(builder, "endDate", endDate);
                return builder.build();
            }
        );
    }

    @PostMapping("/quotations/simulations/search")
    @PreAuthorize("hasAnyAuthority('PP_USER', 'PP_ADMIN', 'ALL_ADMIN')")
    public ResponseEntity<Object> simulateQuotations(
            @RequestBody QuotationSimulateRequestDto requestDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ppHttpService.post(
            "견적 시뮬레이션",
            uriBuilder -> uriBuilder
                .path("/scm-pp/pp/quotations/simulations/search")
                .queryParam("page", page)
                .queryParam("size", size)
                .build(),
            requestDto
        );
    }

    @PostMapping("/quotations/mps-previews")
    public ResponseEntity<Object> previewMps(@RequestBody List<String> quotationIds) {
        return ppHttpService.post("MPS 프리뷰 생성", "/scm-pp/pp/quotations/mps-previews", quotationIds);
    }

    @PostMapping("/quotations/reviews")
    @PreAuthorize("hasAnyAuthority('PP_USER', 'PP_ADMIN', 'ALL_ADMIN')")
    public ResponseEntity<Object> confirmQuotations(@RequestBody QuotationConfirmRequestDto requestDto) {
        return ppHttpService.post("견적 검토", "/scm-pp/pp/quotations/reviews", requestDto);
    }

    @GetMapping("/quotations/status-options")
    public ResponseEntity<Object> getQuotationsStatusToggle() {
        return ppHttpService.get("견적 상태 옵션 조회", "/scm-pp/pp/quotations/status-options");
    }

    @GetMapping("/quotations/mrp/available-status-options")
    public ResponseEntity<Object> getMrpStatusToggle() {
        return ppHttpService.get("MRP 가능 상태 옵션 조회", "/scm-pp/pp/quotations/mrp/available-status-options");
    }

    @GetMapping("/quotations/available-status-options")
    public ResponseEntity<Object> getQuotationAvailableStatusToggle() {
        return ppHttpService.get("견적 가능 상태 옵션 조회", "/scm-pp/pp/quotations/available-status-options");
    }

    @GetMapping("/quotations/mps")
    public ResponseEntity<Object> getMps(
            @RequestParam String bomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {
        return ppHttpService.get(
            "MPS 조회",
            uriBuilder -> uriBuilder
                .path("/scm-pp/pp/quotations/mps")
                .queryParam("bomId", bomId)
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
        );
    }

    @GetMapping("/quotations/bom-options")
    public ResponseEntity<Object> getMpsBomToggle() {
        return ppHttpService.get("MPS BOM 옵션 조회", "/scm-pp/pp/quotations/bom-options");
    }

    @GetMapping("/mrp-runs/quotation-options")
    public ResponseEntity<Object> getMpsQuotationToggle() {
        return ppHttpService.get("MRP RUN 견적 옵션 조회", "/scm-pp/pp/mrp-runs/quotation-options");
    }

    @GetMapping("/mrp-runs/status-options")
    public ResponseEntity<Object> getMpsRunsToggle() {
        return ppHttpService.get("MRP RUN 상태 옵션 조회", "/scm-pp/pp/mrp-runs/status-options");
    }

    @GetMapping("/quotations/mrp/quotation-options")
    public ResponseEntity<Object> getMpsRunsQuotationsToggle() {
        return ppHttpService.get("MRP 견적 옵션 조회", "/scm-pp/pp/quotations/mrp/quotation-options");
    }

    @GetMapping("/quotations/mrp")
    public ResponseEntity<Object> getMrp(
            @RequestParam(required = false) String bomId,
            @RequestParam(required = false) String quotationId,
            @RequestParam(required = false, defaultValue = "ALL") String availableStatusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ppHttpService.get(
            "MRP 조회",
            uriBuilder -> {
                UriBuilder builder = uriBuilder
                    .path("/scm-pp/pp/quotations/mrp")
                    .queryParam("availableStatusCode", availableStatusCode)
                    .queryParam("page", page)
                    .queryParam("size", size);
                builder = addQueryParamIfPresent(builder, "bomId", bomId);
                builder = addQueryParamIfPresent(builder, "quotationId", quotationId);
                return builder.build();
            }
        );
    }

    @GetMapping("/products")
    public ResponseEntity<Object> getProductMap() {
        return ppHttpService.get("제품 목록 조회", "/scm-pp/pp/boms/products");
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Object> getProductDetail(@PathVariable String productId) {
        return ppHttpService.get("제품 상세 조회", "/scm-pp/pp/boms/products/{productId}", productId);
    }

    @GetMapping("/operations")
    public ResponseEntity<Object> getOperationsMap() {
        return ppHttpService.get("공정 목록 조회", "/scm-pp/pp/boms/operations");
    }

    @GetMapping("/metrics")

    public ResponseEntity<Object> getPpStatistic() {
        return ppHttpService.get("PP 통계 조회", "/scm-pp/pp/metrics");
    }

    private UriBuilder addQueryParamIfPresent(UriBuilder builder, String name, Object value) {
        if (value == null) {
            return builder;
        }
        if (value instanceof String stringValue && stringValue.isBlank()) {
            return builder;
        }
        return builder.queryParam(name, value);
    }
}
