package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dao.DepartmentDAO;
import org.ever._4ever_be_business.hr.dto.response.DepartmentDetailDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListItemDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentMemberDto;
import org.ever._4ever_be_business.hr.dto.response.InventoryDepartmentEmployeeDto;
import org.ever._4ever_be_business.hr.entity.Department;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.repository.DepartmentRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.service.DepartmentService;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentDAO departmentDAO;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public DepartmentDetailDto getDepartmentDetail(String departmentId) {
        log.info("부서 상세 정보 조회 요청 - departmentId: {}", departmentId);

        DepartmentDetailDto result = departmentDAO.findDepartmentDetailById(departmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "부서 정보를 찾을 수 없습니다."));

        log.info("부서 상세 정보 조회 성공 - departmentId: {}, departmentName: {}, headcount: {}",
                departmentId, result.getDepartmentName(), result.getHeadcount());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentListItemDto> getDepartmentList(String status, Pageable pageable) {
        log.info("부서 목록 조회 요청 - status: {}, page: {}, size: {}", status, pageable.getPageNumber(), pageable.getPageSize());

        Page<DepartmentListItemDto> result = departmentDAO.findDepartmentList(status, pageable);

        log.info("부서 목록 조회 성공 - total: {}, size: {}", result.getTotalElements(), result.getContent().size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentMemberDto> getDepartmentMembers(String departmentId) {
        log.info("부서 구성원 목록 조회 요청 - departmentId: {}", departmentId);

        List<DepartmentMemberDto> result = departmentDAO.findDepartmentMembers(departmentId);

        log.info("부서 구성원 목록 조회 성공 - count: {}", result.size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getInternalUserIdsByDepartmentName(String departmentName) {
        log.info("부서명으로 InternelUser userId 목록 조회 요청 - departmentName: {}", departmentName);
        List<String> userIds = departmentDAO.findInternalUserIdsByDepartmentName(departmentName);
        log.info("부서명으로 InternelUser userId 목록 조회 성공 - count: {}", userIds.size());
        return userIds;
    }

    @Override
    @Transactional
    public void updateDepartment(String departmentId, String employeeId, String description) {
        log.info("부서 정보 수정 요청 - departmentId: {}, employeeId: {}, description: {}", departmentId, employeeId, description);

        // 1. Department 조회
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "부서를 찾을 수 없습니다."));

        String userIdToSet = null;

        // 2. employeeId가 제공된 경우, 해당 Employee가 존재하는지 확인
        if (employeeId != null && !employeeId.isEmpty()) {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "부서장으로 지정할 직원을 찾을 수 없습니다."));

            // 3. 해당 부서에 속하는 사람인지 확인
            if (employee.getInternelUser() != null
                    && employee.getInternelUser().getPosition() != null
                    && employee.getInternelUser().getPosition().getDepartment() != null) {
                if (!department.getId().equals(employee.getInternelUser().getPosition().getDepartment().getId())) {
                    throw new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "해당 부서의 직원만 부서장이 가능합니다.");
                }
                userIdToSet = employee.getId();
            } else {
                throw new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원의 부서 정보를 찾을 수 없습니다.");
            }
        }

        // 4. Department 업데이트
        department.updateDepartmentInfo(userIdToSet, description);

        // 5. 저장 (Dirty Checking으로 자동 저장)
        log.info("부서 정보 수정 완료 - departmentId: {}", departmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryDepartmentEmployeeDto> getInventoryDepartmentEmployees() {
        log.info("재고 부서 직원 목록 조회 요청");
        List<InventoryDepartmentEmployeeDto> result = departmentDAO.findInventoryDepartmentEmployees();
        log.info("재고 부서 직원 목록 조회 성공 - count: {}", result.size());
        return result;
    }
}
