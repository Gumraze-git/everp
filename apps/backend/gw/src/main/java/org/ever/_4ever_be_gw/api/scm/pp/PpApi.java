package org.ever._4ever_be_gw.api.scm.pp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
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

@Tag(name = "생산관리(PP)", description = "생산 관리 API")
@ApiServerErrorResponse
public interface PpApi {

    public ResponseEntity<Object> createBom(@RequestBody BomCreateRequestDto requestDto);

    public ResponseEntity<Object> getBomDetail(@PathVariable String bomId);

    public ResponseEntity<Object> updateBom(@PathVariable String bomId, @RequestBody BomCreateRequestDto requestDto);

    public ResponseEntity<Object> getMesList(
            @RequestParam(required = false) String quotationId,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size);

    public ResponseEntity<Object> getMesStatusDetail();

    public ResponseEntity<Object> getMesDetail(@PathVariable String mesId);

    public ResponseEntity<Object> startMes(
            @PathVariable String mesId,
            @AuthenticationPrincipal EverUserPrincipal principal
    );

    public ResponseEntity<Object> startOperation(
            @PathVariable String mesId,
            @PathVariable String operationId,
            @AuthenticationPrincipal EverUserPrincipal everUserPrincipal);

    public ResponseEntity<Object> completeOperation(
            @PathVariable String mesId,
            @PathVariable String operationId);

    public ResponseEntity<Object> completeMes(
            @PathVariable String mesId,
            @AuthenticationPrincipal EverUserPrincipal principal
    );

    public ResponseEntity<Object> convertToMrpRun(@RequestBody MrpRunConvertRequestDto requestDto);

    public ResponseEntity<Object> getQuotationList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(defaultValue = "ALL") String availableStatusCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<Object> simulateQuotations(
            @RequestBody QuotationSimulateRequestDto requestDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<Object> previewMps(@RequestBody List<String> quotationIds);

    public ResponseEntity<Object> getQuotationsStatusToggle();

    public ResponseEntity<Object> getQuotationAvailableStatusToggle();

    public ResponseEntity<Object> getMpsBomToggle();

    public ResponseEntity<Object> getMpsRunsToggle();

    public ResponseEntity<Object> getMrp(
            @RequestParam(required = false) String bomId,
            @RequestParam(required = false) String quotationId,
            @RequestParam(required = false, defaultValue = "ALL") String availableStatusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<Object> getProductMap();

    public ResponseEntity<Object> getProductDetail(@PathVariable String productId);

    public ResponseEntity<Object> getOperationsMap();

}
