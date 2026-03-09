package org.ever._4ever_be_scm.api.scm.mm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_scm.common.exception.ErrorCode;
import org.ever._4ever_be_scm.common.exception.handler.ProblemDetailFactory;
import org.ever._4ever_be_scm.scm.iv.dto.PagedResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.SupplierDetailResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.SupplierListResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.supplier.SupplierCreateRequestDto;
import org.ever._4ever_be_scm.scm.mm.dto.supplier.SupplierUpdateRequestDto;
import org.ever._4ever_be_scm.scm.mm.service.SupplierService;
import org.ever._4ever_be_scm.scm.mm.vo.SupplierSearchVo;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

@Tag(name = "구매관리", description = "구매 관리 API")
@ApiServerErrorResponse
public interface SupplierApi {

    @Operation(summary = "공급업체 목록 조회", description = "공급업체 목록을 조회합니다. 상태코드, 카테고리, 검색타입(SupplierCompanyNumber/SupplierCompanyName)과 키워드로 필터링 가능")
    public ResponseEntity<PagedResponseDto<SupplierListResponseDto>> getSupplierList(
            
            @RequestParam(defaultValue = "ALL") String statusCode,
            
            @RequestParam(defaultValue = "ALL") String category,
            
            @RequestParam(required = false) String type,
            
            @RequestParam(required = false) String keyword,
            
            @RequestParam(defaultValue = "0") int page,
            
            @RequestParam(defaultValue = "10") int size);

    public ResponseEntity<SupplierDetailResponseDto> getSupplierDetail(
            @PathVariable String supplierId);

    public DeferredResult<ResponseEntity<?>> createSupplier(
        @RequestBody SupplierCreateRequestDto requestDto
    );

    @Operation(summary = "공급업체 수정", description = "공급업체 정보를 부분 수정합니다. null이 아닌 필드만 업데이트됩니다.")
    public ResponseEntity<Void> updateSupplier(
            @PathVariable String supplierId,
            @RequestBody SupplierUpdateRequestDto requestDto);

}
