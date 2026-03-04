package org.ever._4ever_be_business.company.repository;

import org.ever._4ever_be_business.sd.dto.response.CustomerDetailDto;
import org.ever._4ever_be_business.sd.dto.response.CustomerListItemDto;
import org.ever._4ever_be_business.sd.vo.CustomerSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomerCompanyRepositoryCustom {
    /**
     * 고객사 상세 정보 조회
     *
     * @param customerId 고객사 ID
     * @return 고객사 상세 정보
     */
    Optional<CustomerDetailDto> findCustomerDetailById(String customerId);

    /**
     * 고객사 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 고객사 목록
     */
    Page<CustomerListItemDto> findCustomerList(CustomerSearchConditionVo condition, Pageable pageable);
}
