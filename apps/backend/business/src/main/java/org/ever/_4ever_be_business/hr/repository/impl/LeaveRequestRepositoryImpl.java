package org.ever._4ever_be_business.hr.repository.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.hr.dto.response.LeaveRequestEmployeeDto;
import org.ever._4ever_be_business.hr.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.hr.enums.LeaveType;
import org.ever._4ever_be_business.hr.repository.LeaveRequestRepositoryCustom;
import org.ever._4ever_be_business.hr.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.ever._4ever_be_business.hr.entity.QLeaveRequest.leaveRequest;
import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QDepartment.department;

@Repository
@RequiredArgsConstructor
public class LeaveRequestRepositoryImpl implements LeaveRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<LeaveRequestListItemDto> findLeaveRequestList(LeaveRequestSearchConditionVo condition, Pageable pageable) {
        // 1. Count 쿼리
        Long total = queryFactory
                .select(leaveRequest.count())
                .from(leaveRequest)
                .join(leaveRequest.employee, employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .where(
                        departmentIdEq(condition.getDepartmentId()),
                        positionIdEq(condition.getPositionId()),
                        employeeNameContains(condition.getName()),
                        leaveTypeEq(condition.getType())
                )
                .fetchOne();

        // 2. Content 쿼리 (Nested Projection)
        List<LeaveRequestListItemDto> content = queryFactory
                .select(Projections.constructor(
                        LeaveRequestListItemDto.class,
                        leaveRequest.id,                            // leaveRequestId
                        Projections.constructor(
                                LeaveRequestEmployeeDto.class,
                                employee.id,                        // employeeId
                                internelUser.name,                  // employeeName
                                department.departmentName,          // department
                                position.positionName               // position
                        ),                                          // employee (nested)
                        leaveRequest.leaveType.stringValue(),      // leaveType
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD\"T\"HH24:MI:SS')",
                                leaveRequest.startDate
                        ),                                          // startDate
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD\"T\"HH24:MI:SS')",
                                leaveRequest.endDate
                        ),                                          // endDate
                        leaveRequest.numberOfLeaveDays,            // numberOfLeaveDays
                        employee.remainingVacation.intValue(),     // remainingLeaveDays
                        leaveRequest.status.stringValue()          // status
                ))
                .from(leaveRequest)
                .join(leaveRequest.employee, employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .where(
                        departmentIdEq(condition.getDepartmentId()),
                        positionIdEq(condition.getPositionId()),
                        employeeNameContains(condition.getName()),
                        leaveTypeEq(condition.getType())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getSortOrder(condition.getSortOrder()))
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    // 동적 쿼리 조건 메서드들
    private BooleanExpression departmentIdEq(String departmentId) {
        return StringUtils.hasText(departmentId) ? department.id.eq(departmentId) : null;
    }

    private BooleanExpression positionIdEq(String positionId) {
        return StringUtils.hasText(positionId) ? position.id.eq(positionId) : null;
    }

    private BooleanExpression employeeNameContains(String name) {
        return StringUtils.hasText(name) ? internelUser.name.contains(name) : null;
    }

    private BooleanExpression leaveTypeEq(LeaveType type) {
        return type != null ? leaveRequest.leaveType.eq(type) : null;
    }

    // 정렬 순서
    private OrderSpecifier<?> getSortOrder(String sortOrder) {
        if ("ASC".equalsIgnoreCase(sortOrder)) {
            return new OrderSpecifier<>(Order.ASC, leaveRequest.startDate);
        }
        return new OrderSpecifier<>(Order.DESC, leaveRequest.startDate);  // 기본값 DESC
    }
}
