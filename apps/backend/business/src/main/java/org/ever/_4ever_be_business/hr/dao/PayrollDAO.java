package org.ever._4ever_be_business.hr.dao;

import org.ever._4ever_be_business.hr.dto.response.PayrollListItemDto;
import org.ever._4ever_be_business.hr.dto.response.PaystubDetailDto;
import org.ever._4ever_be_business.hr.vo.PayrollSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PayrollDAO {
    /**
     * 급여 명세서 상세 조회
     *
     * @param paystubId 급여 명세서 ID
     * @return 급여 명세서 상세 정보
     */
    Optional<PaystubDetailDto> findPaystubDetailById(String paystubId);

    /**
     * 급여 명세서 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 급여 명세서 목록
     */
    Page<PayrollListItemDto> findPayrollList(PayrollSearchConditionVo condition, Pageable pageable);
}
