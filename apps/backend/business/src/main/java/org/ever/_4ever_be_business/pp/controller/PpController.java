package org.ever._4ever_be_business.pp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.pp.dto.request.PpQuotationRequestDto;
import org.ever._4ever_be_business.pp.dto.response.PpQuotationResponseDto;
import org.ever._4ever_be_business.pp.service.PpQuotationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/pp")
@RequiredArgsConstructor
public class PpController {

    private final PpQuotationService ppQuotationService;

    /**
     * PP 견적 목록 조회
     */
    @PostMapping("/quotations")
    public ApiResponse<PpQuotationResponseDto> getQuotations(
            @RequestBody PpQuotationRequestDto request) {
        log.info("PP 견적 목록 조회 API 호출 - quotationId: {}", request.getQuotationId());

        PpQuotationResponseDto result = ppQuotationService.getQuotations(request);

        log.info("PP 견적 목록 조회 성공");
        return ApiResponse.success(result, "견적 목록 조회에 성공했습니다.", HttpStatus.OK);
    }
}
