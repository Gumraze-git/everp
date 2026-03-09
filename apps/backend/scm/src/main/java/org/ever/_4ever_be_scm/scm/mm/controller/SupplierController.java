package org.ever._4ever_be_scm.scm.mm.controller;

import org.ever._4ever_be_scm.api.scm.mm.SupplierApi;
import lombok.RequiredArgsConstructor;
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


@RestController
@RequestMapping("/scm-pp/mm/supplier")
@RequiredArgsConstructor
public class SupplierController implements SupplierApi {

    private final SupplierService supplierService;

    @GetMapping

    public ResponseEntity<PagedResponseDto<SupplierListResponseDto>> getSupplierList(

            @RequestParam(defaultValue = "ALL") String statusCode,

            @RequestParam(defaultValue = "ALL") String category,

            @RequestParam(required = false) String type,

            @RequestParam(required = false) String keyword,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size) {

        SupplierSearchVo searchVo = SupplierSearchVo.builder()
                .statusCode(statusCode)
                .category(category)
                .type(type)
                .keyword(keyword)
                .page(page)
                .size(size)
                .build();

        Page<SupplierListResponseDto> suppliers = supplierService.getSupplierList(searchVo);
        PagedResponseDto<SupplierListResponseDto> response = PagedResponseDto.from(suppliers);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{supplierId}")
    public ResponseEntity<SupplierDetailResponseDto> getSupplierDetail(
            @PathVariable String supplierId) {

        SupplierDetailResponseDto detail = supplierService.getSupplierDetail(supplierId);

        return ResponseEntity.ok(detail);
    }

    @PostMapping()
    public DeferredResult<ResponseEntity<?>> createSupplier(
        @RequestBody SupplierCreateRequestDto requestDto
    ) {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<>(30000L);
        deferredResult.onTimeout(() -> deferredResult.setResult(
            ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ProblemDetailFactory.of(
                    HttpStatus.REQUEST_TIMEOUT,
                    "처리 시간이 초과되었습니다.",
                    "[SAGA][FAIL] 처리 시간이 초과되었습니다.",
                    null,
                    null,
                    ErrorCode.INTERNAL_SERVER_ERROR.getCode()
                ))
        ));

        supplierService.createSupplier(requestDto, deferredResult);
        return deferredResult;
    }

    @PatchMapping("/{supplierId}")

    public ResponseEntity<Void> updateSupplier(
            @PathVariable String supplierId,
            @RequestBody SupplierUpdateRequestDto requestDto) {

        supplierService.updateSupplier(supplierId, requestDto);

        return ResponseEntity.noContent().build();
    }
}
