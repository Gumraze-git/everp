package org.ever._4ever_be_business.hr.dao.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.dao.EmployeeDAO;
import org.ever._4ever_be_business.hr.dto.response.EmployeeDetailDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QDepartment.department;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EmployeeDAOImpl implements EmployeeDAO {

    private final JPAQueryFactory queryFactory;
    private final EmployeeRepository employeeRepository;

    @Override
    public Optional<EmployeeDetailDto> findEmployeeDetailById(String employeeId) {
        log.debug("직원 상세 정보 조회 시작 - employeeId: {}", employeeId);

        // Employee → InternelUser → Position → Department 조인
        var employeeInfo = queryFactory
                .select(Projections.constructor(
                        EmployeeInfoProjection.class,
                        employee.id,
                        internelUser.employeeCode,
                        internelUser.name,
                        internelUser.email,
                        internelUser.phoneNumber,
                        position.positionName,
                        department.departmentName,
                        internelUser.hireDate,
                        internelUser.birthDate,
                        internelUser.address,
                        employee.createdAt,
                        employee.updatedAt
                ))
                .from(employee)
                .innerJoin(employee.internelUser, internelUser)
                .innerJoin(internelUser.position, position)
                .innerJoin(position.department, department)
                .where(employee.id.eq(employeeId))
                .fetchOne();

        if (employeeInfo == null) {
            log.debug("직원을 찾을 수 없음 - employeeId: {}", employeeId);
            return Optional.empty();
        }

        // 날짜 포맷팅
        String hireDate = employeeInfo.getHireDate() != null
                ? employeeInfo.getHireDate().toString().substring(0, 10)
                : null;
        String birthDate = employeeInfo.getBirthDate() != null
                ? employeeInfo.getBirthDate().toString().substring(0, 10)
                : null;
        String createdAt = employeeInfo.getCreatedAt() != null
                ? employeeInfo.getCreatedAt().toString().substring(0, 10)
                : null;
        String updatedAt = employeeInfo.getUpdatedAt() != null
                ? employeeInfo.getUpdatedAt().toString().substring(0, 10)
                : null;

        // DTO 조립 (trainings는 Service에서 채움)
        EmployeeDetailDto result = new EmployeeDetailDto(
                employeeInfo.getEmployeeId(),
                employeeInfo.getEmployeeNumber(),
                employeeInfo.getName(),
                employeeInfo.getEmail(),
                employeeInfo.getPhone(),
                employeeInfo.getPosition(),
                employeeInfo.getDepartment(),
                "ACTIVE", // statusCode - 기본값
                hireDate,
                birthDate,
                employeeInfo.getAddress(),
                createdAt,
                updatedAt,
                null // trainings - Service에서 채움
        );

        log.debug("직원 상세 정보 조회 완료 - employeeId: {}, employeeName: {}",
                employeeId, employeeInfo.getName());

        return Optional.of(result);
    }

    @Override
    public Page<EmployeeListItemDto> findEmployeeList(EmployeeListSearchConditionVo condition, Pageable pageable) {
        log.debug("직원 목록 조회 시작 - departmentId: {}, positionId: {}, name: {}",
                condition.getDepartmentId(), condition.getPositionId(), condition.getName());

        Page<EmployeeListItemDto> result = employeeRepository.findEmployeeList(condition, pageable);

        log.debug("직원 목록 조회 완료 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    /**
     * QueryDSL Projection용 내부 클래스
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class EmployeeInfoProjection {
        private String employeeId;
        private String employeeNumber;
        private String name;
        private String email;
        private String phone;
        private String position;
        private String department;
        private LocalDateTime hireDate;
        private LocalDateTime birthDate;
        private String address;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
