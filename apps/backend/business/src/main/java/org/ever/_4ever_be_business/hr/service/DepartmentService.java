package org.ever._4ever_be_business.hr.service;

import org.ever._4ever_be_business.hr.dto.response.DepartmentDetailDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListItemDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentMemberDto;
import org.ever._4ever_be_business.hr.dto.response.InventoryDepartmentEmployeeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {
    /**
     * 부서 상세 정보 조회
     *
     * @param departmentId 부서 ID
     * @return 부서 상세 정보
     */
    DepartmentDetailDto getDepartmentDetail(String departmentId);

    /**
     * 부서 목록 조회
     *
     * @param status 부서 상태
     * @param pageable 페이징 정보
     * @return 부서 목록
     */
    Page<DepartmentListItemDto> getDepartmentList(String status, Pageable pageable);

    /**
     * 부서 구성원 목록 조회
     *
     * @param departmentId 부서 ID
     * @return 구성원 목록
     */
    List<DepartmentMemberDto> getDepartmentMembers(String departmentId);

    /**
     * 부서 이름으로 해당 부서의 모든 InternelUser userId 목록 조회
     *
     * @param departmentName 부서 이름 (예: "영업")
     * @return userId 리스트
     */
    List<String> getInternalUserIdsByDepartmentName(String departmentName);

    /**
     * 부서 정보 수정 (부서장과 설명만 수정 가능)
     *
     * @param departmentId 부서 ID
     * @param employeeId   부서장으로 지정할 직원 ID (Employee ID)
     * @param description  설명
     */
    void updateDepartment(String departmentId, String employeeId, String description);

    /**
     * 재고 부서 직원 목록 조회
     *
     * @return 재고 부서 직원 목록 (userId, name)
     */
    List<InventoryDepartmentEmployeeDto> getInventoryDepartmentEmployees();

}
