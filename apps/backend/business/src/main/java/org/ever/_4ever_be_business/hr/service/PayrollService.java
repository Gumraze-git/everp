package org.ever._4ever_be_business.hr.service;

import org.ever._4ever_be_business.hr.dto.response.PayrollListItemDto;
import org.ever._4ever_be_business.hr.dto.response.PaystubDetailDto;
import org.ever._4ever_be_business.hr.vo.PayrollSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PayrollService {
    /**
     * 급여 명세서 상세 조회
     *
     * @param paystubId 급여 명세서 ID
     * @return 급여 명세서 상세 정보
     */
    PaystubDetailDto getPaystubDetail(String paystubId);

    /**
     * 급여 명세서 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return 급여 명세서 목록
     */
    Page<PayrollListItemDto> getPayrollList(PayrollSearchConditionVo condition, Pageable pageable);

    /**
     * 급여 지급 완료 처리
     *
     * @param payrollId 급여 ID
     */
    void completePayroll(String payrollId);

    /**
     * 모든 직원에 대한 당월 급여 생성
     */
    void generateMonthlyPayrollForAllEmployees();
}
