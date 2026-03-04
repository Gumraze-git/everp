package org.ever._4ever_be_business.hr.dao.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.dao.DepartmentDAO;
import org.ever._4ever_be_business.hr.dto.response.DepartmentDetailDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentEmployeeDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentListItemDto;
import org.ever._4ever_be_business.hr.dto.response.DepartmentMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.hr.entity.QDepartment.department;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DepartmentDAOImpl implements DepartmentDAO {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<DepartmentDetailDto> findDepartmentDetailById(String departmentId) {
        log.debug("부서 상세 정보 조회 시작 - departmentId: {}", departmentId);

        // 1. Department 기본 정보 조회
        var departmentInfo = queryFactory
                .select(Projections.constructor(
                        DepartmentInfoProjection.class,
                        department.id,
                        department.departmentCode,
                        department.departmentName,
                        department.description,
                        department.createdAt
                ))
                .from(department)
                .where(department.id.eq(departmentId))
                .fetchOne();

        if (departmentInfo == null) {
            log.debug("부서를 찾을 수 없음 - departmentId: {}", departmentId);
            return Optional.empty();
        }

        // 2. 해당 부서에 속한 직원 목록 조회 (INACTIVE 제외)
        List<DepartmentEmployeeDto> employees = queryFactory
                .select(Projections.constructor(
                        DepartmentEmployeeDto.class,
                        internelUser.userId,
                        internelUser.name,
                        position.positionName,
                        internelUser.hireDate.stringValue()
                ))
                .from(employee)
                .innerJoin(employee.internelUser, internelUser)
                .innerJoin(internelUser.position, position)
                .where(position.department.id.eq(departmentId)
                        .and(internelUser.status.ne(org.ever._4ever_be_business.hr.enums.UserStatus.INACTIVE)))
                .orderBy(internelUser.hireDate.asc())
                .fetch();

        // hireDate 포맷팅
        List<DepartmentEmployeeDto> formattedEmployees = employees.stream()
                .map(emp -> {
                    try {
                        String formattedDate = emp.getHireDate();
                        if (formattedDate != null && formattedDate.length() >= 10) {
                            formattedDate = formattedDate.substring(0, 10);
                        }
                        return new DepartmentEmployeeDto(
                                emp.getEmployeeId(),
                                emp.getEmployeeName(),
                                emp.getPosition(),
                                formattedDate
                        );
                    } catch (Exception e) {
                        log.warn("날짜 포맷팅 실패 - employeeId: {}", emp.getEmployeeId(), e);
                        return emp;
                    }
                })
                .toList();

        // 3. headName 조회 (부서장 - 가장 높은 직급 또는 가장 오래된 직원)
        String headName = formattedEmployees.isEmpty() ? null : formattedEmployees.get(0).getEmployeeName();

        // 4. headcount 계산
        Long headcount = (long) formattedEmployees.size();

        // 5. createdAt 포맷팅
        String createdAt = departmentInfo.getCreatedAt() != null
                ? departmentInfo.getCreatedAt().toString().substring(0, 10)
                : null;

        // 6. DTO 조립
        DepartmentDetailDto result = new DepartmentDetailDto(
                departmentInfo.getDepartmentId(),
                departmentInfo.getDepartmentCode(),
                departmentInfo.getDepartmentName(),
                headName,
                headcount,
                createdAt,
                departmentInfo.getDescription(),
                formattedEmployees
        );

        log.debug("부서 상세 정보 조회 완료 - departmentId: {}, departmentName: {}, headcount: {}",
                departmentId, departmentInfo.getDepartmentName(), headcount);

        return Optional.of(result);
    }

    @Override
    public Page<DepartmentListItemDto> findDepartmentList(String status, Pageable pageable) {
        log.debug("부서 목록 조회 시작 - status: {}", status);

        // 1. 모든 Department 조회 (페이징)
        var query = queryFactory
                .select(Projections.constructor(
                        DepartmentListProjection.class,
                        department.id,
                        department.departmentCode,
                        department.departmentName,
                        department.description,
                        department.establishmentDate,
                        department.createdAt,
                        department.updatedAt
                ))
                .from(department);

        // status 필터링은 Department 엔티티에 해당 필드가 없으므로 생략
        // 실제 DB에 status 필드가 있다면 추가 필요

        // Total count
        long total = query.fetchCount();

        // 페이징된 데이터 조회
        List<DepartmentListProjection> departments = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(department.id.asc())
                .fetch();

        // 2. 각 부서별로 직원 수와 manager 조회
        List<DepartmentListItemDto> content = departments.stream()
                .map(dept -> {
                    // 해당 부서의 직원 목록 조회 (INACTIVE 제외)
                    List<DepartmentEmployeeDto> employees = queryFactory
                            .select(Projections.constructor(
                                    DepartmentEmployeeDto.class,
                                    employee.id,
                                    internelUser.name,
                                    position.positionName,
                                    internelUser.hireDate.stringValue()
                            ))
                            .from(employee)
                            .innerJoin(employee.internelUser, internelUser)
                            .innerJoin(internelUser.position, position)
                            .where(position.department.id.eq(dept.getDepartmentId())
                                    .and(internelUser.status.ne(org.ever._4ever_be_business.hr.enums.UserStatus.INACTIVE)))
                            .orderBy(internelUser.hireDate.asc())
                            .fetch();

                    // hireDate 포맷팅
                    List<DepartmentEmployeeDto> formattedEmployees = employees.stream()
                            .map(emp -> {
                                try {
                                    String formattedDate = emp.getHireDate();
                                    if (formattedDate != null && formattedDate.length() >= 10) {
                                        formattedDate = formattedDate.substring(0, 10);
                                    }
                                    return new DepartmentEmployeeDto(
                                            emp.getEmployeeId(),
                                            emp.getEmployeeName(),
                                            emp.getPosition(),
                                            formattedDate
                                    );
                                } catch (Exception e) {
                                    log.warn("날짜 포맷팅 실패 - employeeId: {}", emp.getEmployeeId(), e);
                                    return emp;
                                }
                            })
                            .toList();

                    Integer employeeCount = formattedEmployees.size();

                    // Department의 managerId를 사용하여 실제 manager 정보 조회
                    String managerName = null;
                    String managerEmployeeId = null;

                    // 실제 Department 엔티티를 조회하여 managerId 확인
                    var actualDept = queryFactory
                            .selectFrom(department)
                            .where(department.id.eq(dept.getDepartmentId()))
                            .fetchOne();

                    if (actualDept != null && actualDept.getManagerId() != null) {
                        // managerId(employee.id)로 Employee 조회
                        var managerInfo = queryFactory
                                .select(Projections.constructor(
                                        ManagerInfoProjection.class,
                                        employee.id,
                                        internelUser.name
                                ))
                                .from(employee)
                                .innerJoin(employee.internelUser, internelUser)
                                .where(employee.id.eq(actualDept.getManagerId()))
                                .fetchOne();

                        if (managerInfo != null) {
                            managerEmployeeId = managerInfo.getManagerId();
                            managerName = managerInfo.getManagerName();
                        }
                    }

                    // 날짜 포맷팅
                    String establishedDate = dept.getEstablishedDate() != null
                            ? dept.getEstablishedDate().toString().substring(0, 10)
                            : null;
                    String createdAt = dept.getCreatedAt() != null
                            ? dept.getCreatedAt().toString().substring(0, 10)
                            : null;
                    String updatedAt = dept.getUpdatedAt() != null
                            ? dept.getUpdatedAt().toString().substring(0, 10)
                            : null;

                    // 엔티티에 없는 필드는 기본값 설정
                    String statusCode = "ACTIVE"; // 기본값
                    Long budget = 0L; // 기본값
                    String budgetCurrency = "KRW"; // 기본값
                    List<String> responsibilities = new ArrayList<>(); // 빈 리스트

                    return new DepartmentListItemDto(
                            dept.getDepartmentId(),
                            dept.getDepartmentCode(), // departmentNumber
                            dept.getDepartmentName(),
                            dept.getDescription(),
                            managerName,
                            managerEmployeeId,
                            null, // location - 엔티티에 없음
                            statusCode,
                            employeeCount,
                            budget,
                            budgetCurrency,
                            establishedDate,
                            createdAt,
                            updatedAt,
                            responsibilities,
                            formattedEmployees
                    );
                })
                .toList();

        log.debug("부서 목록 조회 완료 - total: {}, size: {}", total, content.size());

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<DepartmentMemberDto> findDepartmentMembers(String departmentId) {
        log.debug("부서 구성원 목록 조회 시작 - departmentId: {}", departmentId);

        // INACTIVE 직원 제외
        List<DepartmentMemberDto> members = queryFactory
                .select(Projections.constructor(
                        DepartmentMemberDto.class,
                        employee.id,
                        internelUser.name
                ))
                .from(employee)
                .innerJoin(employee.internelUser, internelUser)
                .innerJoin(internelUser.position, position)
                .where(position.department.id.eq(departmentId)
                        .and(internelUser.status.ne(org.ever._4ever_be_business.hr.enums.UserStatus.INACTIVE)))
                .orderBy(internelUser.hireDate.asc())
                .fetch();

        log.debug("부서 구성원 목록 조회 완료 - size: {}", members.size());

        return members;
    }

    @Override
    public List<String> findInternalUserIdsByDepartmentName(String departmentName) {
        log.debug("부서명으로 InternelUser userId 목록 조회 시작 - departmentName: {}", departmentName);

        List<String> userIds = queryFactory
            .select(internelUser.userId)
            .from(internelUser)
            .innerJoin(internelUser.position, position)
            .innerJoin(position.department, department)
            .where(department.departmentName.eq(departmentName))
            .fetch();

        log.debug("부서명으로 InternelUser userId 목록 조회 완료 - count: {}", userIds.size());
        return userIds;
    }

    @Override
    public List<org.ever._4ever_be_business.hr.dto.response.InventoryDepartmentEmployeeDto> findInventoryDepartmentEmployees() {
        log.debug("재고 부서 직원 목록 조회 시작");

        List<org.ever._4ever_be_business.hr.dto.response.InventoryDepartmentEmployeeDto> employees = queryFactory
            .select(Projections.constructor(
                org.ever._4ever_be_business.hr.dto.response.InventoryDepartmentEmployeeDto.class,
                internelUser.userId,
                internelUser.name,
                    internelUser.email,
                    internelUser.phoneNumber
            ))
            .from(internelUser)
            .innerJoin(internelUser.position, position)
            .innerJoin(position.department, department)
            .where(department.departmentName.eq("재고"))
            .fetch();

        log.debug("재고 부서 직원 목록 조회 완료 - count: {}", employees.size());
        return employees;
    }

    /**
     * QueryDSL Projection용 내부 클래스
     */
    @Getter
    @AllArgsConstructor
    public static class DepartmentInfoProjection {
        private String departmentId;
        private String departmentCode;
        private String departmentName;
        private String description;
        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    public static class DepartmentListProjection {
        private String departmentId;
        private String departmentCode;
        private String departmentName;
        private String description;
        private LocalDateTime establishedDate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @AllArgsConstructor
    public static class EmployeeInfoProjection {
        private String employeeId;
        private String employeeName;
        private String positionName;
    }

    @Getter
    @AllArgsConstructor
    public static class ManagerInfoProjection {
        private String managerId;
        private String managerName;
    }
}
