package org.ever._4ever_be_business.tam.repository.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.hr.enums.VacationType;
import org.ever._4ever_be_business.tam.entity.VacationRequest;
import org.ever._4ever_be_business.tam.repository.VacationRequestRepositoryCustom;
import org.ever._4ever_be_business.tam.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QDepartment.department;
import static org.ever._4ever_be_business.tam.entity.QVacationRequest.vacationRequest;

@Repository
@RequiredArgsConstructor
public class VacationRequestRepositoryImpl implements VacationRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<VacationRequest> searchLeaveRequests(LeaveRequestSearchConditionVo condition, Pageable pageable) {
        // 1. Count 쿼리
        Long total = queryFactory
                .select(vacationRequest.count())
                .from(vacationRequest)
                .join(vacationRequest.employee, employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .where(
                        departmentNameEq(condition.getDepartment()),
                        positionNameEq(condition.getPosition()),
                        employeeNameContains(condition.getName()),
                        vacationTypeEq(condition.getType())
                )
                .fetchOne();

        // 2. Content 쿼리 (JOIN FETCH)
        List<VacationRequest> content = queryFactory
                .selectFrom(vacationRequest)
                .join(vacationRequest.employee, employee).fetchJoin()
                .join(employee.internelUser, internelUser).fetchJoin()
                .join(internelUser.position, position).fetchJoin()
                .join(position.department, department).fetchJoin()
                .where(
                        departmentNameEq(condition.getDepartment()),
                        positionNameEq(condition.getPosition()),
                        employeeNameContains(condition.getName()),
                        vacationTypeEq(condition.getType())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(vacationRequest.id.desc())
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

    private BooleanExpression vacationTypeEq(VacationType type) {
        return type != null ? vacationRequest.vacationType.eq(type) : null;
    }
}
