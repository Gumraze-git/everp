package org.ever._4ever_be_business.hr.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeTrainingSummaryDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingStatusItemDto;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;
import org.ever._4ever_be_business.hr.repository.EmployeeRepositoryCustom;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingStatusSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QEmployeeTraining.employeeTraining;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QDepartment.department;
import static org.ever._4ever_be_business.hr.entity.QTraining.training;

@Repository
@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Employee> searchEmployeesWithTrainingInfo(EmployeeTrainingSearchConditionVo condition, Pageable pageable) {
        // 1. Count 쿼리
        Long total = queryFactory
                .select(employee.count())
                .from(employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .where(
                        departmentNameEq(condition.getDepartment()),
                        positionNameEq(condition.getPosition()),
                        employeeNameContains(condition.getName())
                )
                .fetchOne();

        // 2. Content 쿼리 (JOIN FETCH)
        List<Employee> content = queryFactory
                .selectFrom(employee)
                .join(employee.internelUser, internelUser).fetchJoin()
                .join(internelUser.position, position).fetchJoin()
                .join(position.department, department).fetchJoin()
                .where(
                        departmentNameEq(condition.getDepartment()),
                        positionNameEq(condition.getPosition()),
                        employeeNameContains(condition.getName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(employee.id.asc())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    // 동적 쿼리 조건 메서드들
    private BooleanExpression departmentNameEq(String departmentName) {
        return StringUtils.hasText(departmentName) ? department.departmentName.eq(departmentName) : null;
    }

    private BooleanExpression positionNameEq(String positionName) {
        return StringUtils.hasText(positionName) ? position.positionName.eq(positionName) : null;
    }

    private BooleanExpression employeeNameContains(String name) {
        return StringUtils.hasText(name) ? internelUser.name.contains(name) : null;
    }

    @Override
    public Page<EmployeeListItemDto> findEmployeeList(EmployeeListSearchConditionVo condition, Pageable pageable) {
        // 1. Count 쿼리
        Long total = queryFactory
                .select(employee.count())
                .from(employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .where(
                        departmentIdEq(condition.getDepartmentId()),
                        positionIdEq(condition.getPositionId()),
                        employeeNameContains(condition.getName())
                )
                .fetchOne();

        // 2. Content 쿼리 (Projection)
        List<EmployeeListItemDto> content = queryFactory
                .select(Projections.constructor(
                        EmployeeListItemDto.class,
                        employee.id,                                    // employeeId
                        internelUser.employeeCode,                     // employeeNumber
                        internelUser.name,                             // name
                        Expressions.constant("employee@company.com"),   // email - 기본값
                        Expressions.constant("010-0000-0000"),         // phone - 기본값
                        position.positionName,                         // position
                        department.departmentName,                     // department
                        Expressions.constant("ACTIVE"),                // statusCode - 기본값
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                internelUser.hireDate
                        ),                                             // hireDate
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                internelUser.birthDate
                        ),                                             // birthDate
                        internelUser.address,                          // address
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                employee.createdAt
                        ),                                             // createdAt
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                employee.updatedAt
                        )                                              // updatedAt
                ))
                .from(employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .where(
                        departmentIdEq(condition.getDepartmentId()),
                        positionIdEq(condition.getPositionId()),
                        employeeNameContains(condition.getName())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(employee.id.asc())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<TrainingStatusItemDto> findTrainingStatusList(TrainingStatusSearchConditionVo condition, Pageable pageable) {
        // 1. Count query
        Long total = queryFactory
                .select(employee.countDistinct())
                .from(employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .where(
                        departmentIdEq(condition.getDepartment()),
                        positionIdEq(condition.getPosition()),
                        employeeNameContains(condition.getName())
                )
                .fetchOne();

        // 2. Content query with aggregations
        List<TrainingStatusItemDto> content = queryFactory
                .select(Projections.constructor(
                        TrainingStatusItemDto.class,
                        employee.id,                                        // employeeId
                        internelUser.name,                                  // name
                        department.departmentName,                          // department
                        position.positionName,                              // position
                        new CaseBuilder()
                                .when(employeeTraining.completionStatus.eq(org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus.COMPLETED)
                                        .and(training.trainingStatus.eq(TrainingStatus.COMPLETED)))
                                .then(1L)
                                .otherwise(0L)
                                .sum(),                                     // completedCount
                        new CaseBuilder()
                                .when(employeeTraining.completionStatus.eq(org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus.IN_PROGRESS)
                                        .and(training.trainingStatus.eq(TrainingStatus.IN_PROGRESS)))
                                .then(1L)
                                .otherwise(0L)
                                .sum(),                                     // inProgressCount
                        Expressions.constant(0L),                           // requiredMissingCount (placeholder)
                        employeeTraining.updatedAt.max()                    // lastTrainingDate
                ))
                .from(employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .leftJoin(employeeTraining).on(employeeTraining.employee.id.eq(employee.id))
                .leftJoin(training).on(employeeTraining.training.id.eq(training.id))
                .where(
                        departmentIdEq(condition.getDepartment()),
                        positionIdEq(condition.getPosition()),
                        employeeNameContains(condition.getName())
                )
                .groupBy(employee.id, internelUser.name, department.departmentName, position.positionName)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(employee.id.asc())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Optional<EmployeeTrainingSummaryDto> findEmployeeTrainingSummary(String employeeId) {
        EmployeeTrainingSummaryDto result = queryFactory
                .select(Projections.constructor(
                        EmployeeTrainingSummaryDto.class,
                        employee.id,                                        // id
                        internelUser.employeeCode,                          // employeeNumber
                        internelUser.name,                                  // employeeName
                        department.departmentName,                          // department
                        position.positionName,                              // position
                        new CaseBuilder()
                                .when(employeeTraining.completionStatus.eq(org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus.COMPLETED)
                                        .and(training.trainingStatus.eq(TrainingStatus.COMPLETED)))
                                .then(1L)
                                .otherwise(0L)
                                .sum(),                                     // completedCount
                        new CaseBuilder()
                                .when(employeeTraining.completionStatus.eq(org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus.IN_PROGRESS)
                                        .and(training.trainingStatus.eq(TrainingStatus.IN_PROGRESS)))
                                .then(1L)
                                .otherwise(0L)
                                .sum(),                                     // inProgressCount
                        Expressions.constant(0L),                           // requiredMissingCount (placeholder)
                        employeeTraining.updatedAt.max()                    // lastTrainingDate
                ))
                .from(employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .leftJoin(employeeTraining).on(employeeTraining.employee.id.eq(employee.id))
                .leftJoin(training).on(employeeTraining.training.id.eq(training.id))
                .where(employee.id.eq(employeeId))
                .groupBy(employee.id, internelUser.employeeCode, internelUser.name, department.departmentName, position.positionName)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    // Dynamic query helper methods
    private BooleanExpression departmentIdEq(String departmentId) {
        return StringUtils.hasText(departmentId) ? department.id.eq(departmentId) : null;
    }

    private BooleanExpression positionIdEq(String positionId) {
        return StringUtils.hasText(positionId) ? position.id.eq(positionId) : null;
    }
}
