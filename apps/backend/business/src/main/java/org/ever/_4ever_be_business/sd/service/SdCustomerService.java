package org.ever._4ever_be_business.sd.service;

import org.ever._4ever_be_business.sd.dto.request.CreateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.request.UpdateCustomerRequestDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerDetailDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListResponseDto;
import org.ever._4ever_be_business.sd.vo.CustomerDetailVo;
import org.ever._4ever_be_business.sd.vo.CustomerSearchConditionVo;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public interface SdCustomerService {
    /**
     * 고객사 상세 정보 조회
     *
     * @param vo 고객사 ID
     * @return 고객사 상세 정보
     */
    CustomerDetailDto getCustomerDetail(CustomerDetailVo vo);

    /**
     * 고객사 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 고객사 목록 및 페이징 정보
     */
    CustomerListResponseDto getCustomerList(CustomerSearchConditionVo condition, Pageable pageable);

    /**
     * 고객사 등록
     *
     * @param dto 고객사 등록 요청 정보
     */
    void createCustomer(
        CreateCustomerRequestDto dto,
        DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> deferredResult
    );

    /**
     * 고객사 정보 수정
     *
     * @param customerId 고객사 ID
     * @param dto 수정할 고객사 정보
     */
    void updateCustomer(String customerId, UpdateCustomerRequestDto dto);

    /**
     * 고객사 삭제 (Soft Delete)
     *
     * @param customerId 고객사 ID
     */
    void deleteCustomer(String customerId);
}
