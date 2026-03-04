package org.ever._4ever_be_business.hr.dao;

import org.ever._4ever_be_business.hr.dto.response.EmployeeDetailDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmployeeDAO {
    /**
     * 직원 상세 정보 조회
     *
     * @param employeeId 직원 ID
     * @return 직원 상세 정보
     */
    Optional<EmployeeDetailDto> findEmployeeDetailById(String employeeId);

    /**
     * 직원 목록 조회
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<EmployeeListItemDto>
     */
    Page<EmployeeListItemDto> findEmployeeList(EmployeeListSearchConditionVo condition, Pageable pageable);
}
