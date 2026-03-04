package org.ever._4ever_be_business.hr.dao;

import org.ever._4ever_be_business.hr.dto.response.DepartmentDetailDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListItemDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentMemberDto;
import org.ever._4ever_be_business.hr.dto.response.InventoryDepartmentEmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DepartmentDAO {
    /**
     * 부서 상세 정보 조회
     *
     * @param departmentId 부서 ID
     * @return 부서 상세 정보
     */
    Optional<DepartmentDetailDto> findDepartmentDetailById(String departmentId);

    /**
     * 부서 목록 조회 (페이징)
     *
     * @param status 부서 상태 (ACTIVE/INACTIVE)
     * @param pageable 페이징 정보
     * @return 부서 목록
     */
    Page<DepartmentListItemDto> findDepartmentList(String status, Pageable pageable);

    /**
     * 부서 구성원 목록 조회 (ID, Name만)
     *
     * @param departmentId 부서 ID
     * @return 구성원 목록
     */
    List<DepartmentMemberDto> findDepartmentMembers(String departmentId);

    /**
     * 부서 이름으로 해당 부서의 InternelUser userId 목록 조회
     *
     * @param departmentName 부서 이름 (예: "영업")
     * @return userId 리스트
     */
    List<String> findInternalUserIdsByDepartmentName(String departmentName);

    /**
     * 재고 부서 직원 목록 조회 (userId, name)
     *
     * @return 재고 부서 직원 목록
     */
    List<InventoryDepartmentEmployeeDto> findInventoryDepartmentEmployees();
}
