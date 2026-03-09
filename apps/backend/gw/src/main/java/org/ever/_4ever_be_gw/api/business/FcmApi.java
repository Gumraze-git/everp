package org.ever._4ever_be_gw.api.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.business.dto.InvoiceIdsRequestDto;
import org.ever._4ever_be_gw.business.dto.InvoiceUpdateRequestDto;
import org.ever._4ever_be_gw.business.service.FcmHttpService;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "재무관리(FCM)", description = "재무 관리 API")
@ApiServerErrorResponse
public interface FcmApi {

    @Operation(summary = "FCM 통계 조회", description = "기간별 재무 관리 통계를 조회합니다.")
    public ResponseEntity<?> getStatistics(
        
        @RequestParam(name = "periods", required = false) String periods
    );

    @Operation(summary = "매입 전표 목록 조회", description = "매입(AP) 전표 목록을 조회합니다. JWT 토큰이 있고 userType이 SUPPLIER인 경우 해당 공급사의 매입(AP) 전표를 조회합니다.")
    public ResponseEntity<?> getApInvoices(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @RequestParam(name = "company", required = false) String company,
        @RequestParam(name = "startDate", required = false) String startDate,
        @RequestParam(name = "endDate", required = false) String endDate,
        @RequestParam(required = false) String status,
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size
    );

    @Operation(summary = "매출 전표 목록 조회", description = "매출(AR) 전표 목록을 조회합니다. JWT 토큰이 있고 userType이 CUSTOMER인 경우 해당 고객사의 매출(AR) 전표를 조회합니다.")
    public ResponseEntity<?> getArInvoices(
        @AuthenticationPrincipal EverUserPrincipal principal,
        @RequestParam(name = "company", required = false) String company,
        @RequestParam(name = "startDate", required = false) String startDate,
        @RequestParam(name = "endDate", required = false) String endDate,
        @RequestParam(required = false) String status,
        @RequestParam(name = "page", required = false) Integer page,
        @RequestParam(name = "size", required = false) Integer size
    );

    @Operation(summary = "매입 전표 상세 조회", description = "매입(AP) 전표 상세 정보를 조회합니다.")
    public ResponseEntity<?> getApInvoiceDetail(
        
        @PathVariable("invoiceId") String invoiceId
    );

    @Operation(summary = "매출 전표 상세 조회", description = "매출(AR) 전표 상세 정보를 조회합니다.")
    public ResponseEntity<?> getArInvoiceDetail(
        
        @PathVariable("invoiceId") String invoiceId
    );

    @Operation(summary = "매입 전표 수정", description = "매입(AP) 전표를 수정합니다.")
    public ResponseEntity<?> patchApInvoice(
        
        @PathVariable("invoiceId") String invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    );

    @Operation(summary = "매출 전표 수정", description = "매출(AR) 전표를 수정합니다.")
    public ResponseEntity<?> patchArInvoice(
        
        @PathVariable("invoiceId") String invoiceId,
        @Valid @RequestBody InvoiceUpdateRequestDto request
    );

    @Operation(summary = "매출 전표 미수 처리 완료", description = "미납/확인요청 상태의 매출(AR) 전표에 대해 미수 처리를 완료합니다.")
    public ResponseEntity<?> completeReceivable(
        
        @PathVariable("invoiceId") String invoiceId
    );

    @Operation(summary = "매입 전표 미지급 처리 완료", description = "미납/확인요청 상태의 매입(AP) 전표에 대해 미지급 처리를 완료합니다.")
    public ResponseEntity<?> completePayable(
        
        @PathVariable("invoiceId") String invoiceId
    );

    @Operation(summary = "매입 전표 미수 처리 요청", description = "매입(AP) 전표에 대해 공급사에 미수 처리 요청을 발송합니다.")
    public ResponseEntity<?> requestApReceivable(
        
        @RequestParam("invoiceId") String invoiceId
    );

    @Operation(summary = "매출 전표 상태 일괄 변경", description = "매출(AR) 전표들의 상태를 RESPONSE_PENDING으로 일괄 변경합니다.")
    public ResponseEntity<?> updateArInvoicesResponsePending(@Valid @RequestBody InvoiceIdsRequestDto request);

    @Operation(summary = "매입 전표 상태 일괄 변경", description = "매입(AP) 전표들의 상태를 RESPONSE_PENDING으로 일괄 변경합니다.")
    public ResponseEntity<?> updateApInvoicesResponsePending(@Valid @RequestBody InvoiceIdsRequestDto request);

    @Operation(summary = "공급사 매출 전표 통계")
    public ResponseEntity<?> getTotalPurchaseAmountBySupplierUserId(
        @AuthenticationPrincipal EverUserPrincipal everUserPrincipal
    );

    @Operation(summary = "고객사 매입 전표 통계")
    public ResponseEntity<?> getTotalSalesAmountByCustomerUserId(
        @AuthenticationPrincipal EverUserPrincipal everUserPrincipal
    );

}
