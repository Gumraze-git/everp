package org.ever._4ever_be_gw.api.business;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.Map;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.business.dto.analytics.SalesAnalyticsResponseDto;
import org.ever._4ever_be_gw.business.dto.customer.CustomerCreateRequestDto;
import org.ever._4ever_be_gw.business.dto.hrm.CreateAuthUserResultDto;
import org.ever._4ever_be_gw.business.service.SdHttpService;
import org.ever._4ever_be_gw.business.service.SdService;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "영업관리(SD)", description = "영업관리(SD) API")
@ApiServerErrorResponse
public interface SdApi {

    @Operation(summary = "SD 통계 조회", description = "주간/월간/분기/연간 영업 통계를 조회합니다.")
    public ResponseEntity<Object> getMetrics();

    @Operation(summary = "견적 품목 재고 확인", description = "요청한 품목들의 현재 재고를 확인하고 부족 여부를 반환합니다.")
    public ResponseEntity<Object> checkInventory(
            
            @RequestBody Map<String, Object> requestBody
    );

    @Operation(summary = "견적 옵션 조회", description = "사용 가능한 견적 ID/코드 목록을 조회합니다.")
    public ResponseEntity<Object> getQuotationOptions();

    @Operation(summary = "견적 상세 조회", description = "견적 단건 상세 정보를 조회합니다.")
    public ResponseEntity<Object> getQuotationDetail(@PathVariable String quotationId);

    @Operation(summary = "견적 검토 요청", description = "선택한 견적의 검토 요청을 수행합니다.")
    public ResponseEntity<Object> createQuotationReview(@PathVariable String quotationId);

    @Operation(summary = "견적 거부", description = "견적서를 거부하고 거부 사유를 기록합니다.")
    public ResponseEntity<Object> rejectQuotation(
            @PathVariable String quotationId,
            @RequestBody Map<String, Object> requestBody
    );

    @Operation(summary = "고객사 등록", description = "고객사 정보를 신규 등록합니다.")
    public Mono<ResponseEntity<CreateAuthUserResultDto>> createCustomer(@Valid @RequestBody CustomerCreateRequestDto requestDto);

    @Operation(summary = "고객사 상세 조회", description = "고객사 상세 정보를 조회합니다.")
    public ResponseEntity<Object> getCustomerDetail(@PathVariable String customerId);

    @Operation(summary = "고객사 정보 수정", description = "고객사 기본/연락/담당자 정보를 수정합니다.")
    public ResponseEntity<Object> updateCustomer(
            @PathVariable String customerId,
            @RequestBody Map<String, Object> requestBody
    );

    @Operation(summary = "고객사 삭제", description = "고객사 정보를 삭제합니다.")
    public ResponseEntity<Object> deleteCustomer(@PathVariable String customerId);

    @Operation(summary = "주문서 상세 조회", description = "주문서 상세 정보를 조회합니다.")
    public ResponseEntity<Object> getSalesOrderDetail(@PathVariable String salesOrderId);

    @Operation(summary = "견적 승인 및 주문 전환", description = "견적서를 승인하고 주문서를 생성합니다.")
    public ResponseEntity<Object> approveQuotationAndCreateOrder(
            @PathVariable String quotationId,
            @RequestBody(required = false) Map<String, Object> requestBody
    );

    @Operation(summary = "내 견적 건수 조회", description = "현재 로그인한 고객사 사용자의 견적 건수를 조회합니다.")
    public ResponseEntity<Object> getCurrentCustomerQuotationCount(
            @AuthenticationPrincipal EverUserPrincipal principal
    );

    @Operation(summary = "SCM용 견적 목록 조회", description = "SCM 내부 소비자가 사용하는 견적 목록을 조회합니다.")
    public ResponseEntity<Object> getScmQuotations(
            @RequestParam(name = "statusCode", required = false) String statusCode,
            @RequestParam(name = "availableStatus", required = false) String availableStatus,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    );

}
