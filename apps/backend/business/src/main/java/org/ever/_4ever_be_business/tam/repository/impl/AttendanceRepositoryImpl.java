package org.ever._4ever_be_business.tam.repository.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.enums.AttendanceStatus;
import org.ever._4ever_be_business.tam.repository.AttendanceRepositoryCustom;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.ever._4ever_be_business.tam.vo.AttendanceSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QDepartment.department;
import static org.ever._4ever_be_business.tam.entity.QAttendance.attendance;

@Repository
@RequiredArgsConstructor
public class AttendanceRepositoryImpl implements AttendanceRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Attendance> findByIdWithAllRelations(String attendanceId) {
        Attendance result = queryFactory
                .selectFrom(attendance)
                .join(attendance.employee, employee).fetchJoin()
                .join(employee.internelUser, internelUser).fetchJoin()
                .join(internelUser.position, position).fetchJoin()
                .join(position.department, department).fetchJoin()
                .where(attendance.id.eq(attendanceId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Attendance> searchAttendanceRecords(AttendanceSearchConditionVo condition, Pageable pageable) {
        // 1. Count 쿼리
        Long total = queryFactory
                .select(attendance.count())
                .from(attendance)
                .join(attendance.employee, employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .join(position.department, department)
                .where(
                        departmentIdEq(condition.getDepartment()),
                        positionIdEq(condition.getPosition()),
                        employeeNameContains(condition.getName()),
                        workDateEq(condition.getDate()),
                        statusCodeEq(condition.getStatusCode())
                )
                .fetchOne();

        // 2. Content 쿼리 (JOIN FETCH)
        List<Attendance> content = queryFactory
                .selectFrom(attendance)
                .join(attendance.employee, employee).fetchJoin()
                .join(employee.internelUser, internelUser).fetchJoin()
                .join(internelUser.position, position).fetchJoin()
                .join(position.department, department).fetchJoin()
                .where(
                        departmentIdEq(condition.getDepartment()),
                        positionIdEq(condition.getPosition()),
                        employeeNameContains(condition.getName()),
                        workDateEq(condition.getDate()),
                        statusCodeEq(condition.getStatusCode())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(attendance.workDate.desc(), attendance.id.desc())
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

    private BooleanExpression workDateEq(LocalDate date) {
        if (date == null) {
            return null;
        }

        // LocalDate를 LocalDateTime 범위로 변환
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return attendance.workDate.between(startOfDay, endOfDay);
    }

    private BooleanExpression statusCodeEq(String statusCode) {
        if (!StringUtils.hasText(statusCode)) {
            return null;
        }
        try {
            AttendanceStatus status = AttendanceStatus.valueOf(statusCode);
            return attendance.status.eq(status);
        } catch (IllegalArgumentException e) {
            // Invalid status code, ignore
            return null;
        }
    }

    @Override
    public Page<AttendanceListItemDto> findAttendanceList(AttendanceListSearchConditionVo condition, Pageable pageable) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // 동적 where 조건
        BooleanExpression whereClause = Expressions.asBoolean(true).isTrue();

        if (condition.getEmployeeId() != null && !condition.getEmployeeId().isEmpty()) {
            whereClause = whereClause.and(employee.id.eq(condition.getEmployeeId()));
        }

        if (condition.getStartDate() != null && !condition.getStartDate().isEmpty()) {
            LocalDate startDate = LocalDate.parse(condition.getStartDate(), dateFormatter);
            whereClause = whereClause.and(attendance.workDate.goe(startDate.atStartOfDay()));
        }

        if (condition.getEndDate() != null && !condition.getEndDate().isEmpty()) {
            LocalDate endDate = LocalDate.parse(condition.getEndDate(), dateFormatter);
            whereClause = whereClause.and(attendance.workDate.loe(endDate.atTime(23, 59, 59)));
        }

        if (condition.getStatus() != null && !condition.getStatus().isEmpty()) {
            try {
                AttendanceStatus status = AttendanceStatus.valueOf(condition.getStatus());
                whereClause = whereClause.and(attendance.status.eq(status));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }

        // Count query
        Long total = queryFactory
                .select(attendance.count())
                .from(attendance)
                .join(attendance.employee, employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .where(whereClause)
                .fetchOne();

        // Data query with projections
        List<AttendanceListItemDto> content = queryFactory
                .select(Projections.constructor(
                        AttendanceListItemDto.class,
                        attendance.id,                                      // attendanceId
                        employee.id,                                        // employeeId
                        internelUser.name,                                  // employeeName
                        internelUser.employeeCode,                         // employeeNumber
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                attendance.workDate
                        ),                                                  // attendanceDate
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'HH24:MI:SS')",
                                attendance.checkIn
                        ),                                                  // checkInTime
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'HH24:MI:SS')",
                                attendance.checkOut
                        ),                                                  // checkOutTime
                        attendance.status.stringValue(),                   // statusCode
                        Expressions.constant("OFFICE"),                    // workType (기본값)
                        Expressions.constant("본사"),                       // location (기본값)
                        Expressions.constant(""),                          // notes (기본값)
                        Expressions.numberTemplate(Integer.class,
                                "CAST({0} / 60 AS INTEGER)",
                                attendance.workMinutes),                   // workingHours
                        Expressions.numberTemplate(Integer.class,
                                "CAST({0} / 60 AS INTEGER)",
                                attendance.overtimeMinutes),               // overtimeHours
                        Expressions.constant("APPROVED"),                  // approvalStatus (기본값)
                        Expressions.constant(""),                          // approverName (기본값)
                        Expressions.constant(""),                          // approverId (기본값)
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                attendance.createdAt
                        ),                                                  // createdAt
                        Expressions.stringTemplate(
                                "TO_CHAR({0}, 'YYYY-MM-DD')",
                                attendance.updatedAt
                        ),                                                  // updatedAt
                        Expressions.constant("개발팀"),                     // department (기본값)
                        position.positionName,                             // position
                        Expressions.cases()
                                .when(attendance.status.eq(AttendanceStatus.LATE))
                                .then(true)
                                .otherwise(false),                         // isLate
                        Expressions.constant(false)                        // isEarlyLeave (더 이상 사용 안 함)
                ))
                .from(attendance)
                .join(attendance.employee, employee)
                .join(employee.internelUser, internelUser)
                .join(internelUser.position, position)
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(attendance.workDate.desc(), attendance.id.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<Attendance> findAttendanceEntities(AttendanceListSearchConditionVo condition, Pageable pageable) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 동적 where 조건
        BooleanExpression whereClause = Expressions.asBoolean(true).isTrue();

        if (condition.getEmployeeId() != null && !condition.getEmployeeId().isEmpty()) {
            whereClause = whereClause.and(employee.id.eq(condition.getEmployeeId()));
        }

        if (condition.getStartDate() != null && !condition.getStartDate().isEmpty()) {
            LocalDate startDate = LocalDate.parse(condition.getStartDate(), dateFormatter);
            whereClause = whereClause.and(attendance.workDate.goe(startDate.atStartOfDay()));
        }

        if (condition.getEndDate() != null && !condition.getEndDate().isEmpty()) {
            LocalDate endDate = LocalDate.parse(condition.getEndDate(), dateFormatter);
            whereClause = whereClause.and(attendance.workDate.loe(endDate.atTime(23, 59, 59)));
        }

        if (condition.getStatus() != null && !condition.getStatus().isEmpty()) {
            try {
                AttendanceStatus status = AttendanceStatus.valueOf(condition.getStatus());
                whereClause = whereClause.and(attendance.status.eq(status));
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }

        // Count query
        Long total = queryFactory
                .select(attendance.count())
                .from(attendance)
                .join(attendance.employee, employee)
                .where(whereClause)
                .fetchOne();

        // Data query with fetch join
        List<Attendance> content = queryFactory
                .selectFrom(attendance)
                .join(attendance.employee, employee).fetchJoin()
                .join(employee.internelUser, internelUser).fetchJoin()
                .join(internelUser.position, position).fetchJoin()
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(attendance.workDate.desc(), attendance.id.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Optional<Attendance> findTodayAttendanceByEmployeeId(String employeeId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        Attendance result = queryFactory
                .selectFrom(attendance)
                .join(attendance.employee, employee).fetchJoin()
                .where(
                        employee.id.eq(employeeId),
                        attendance.checkIn.between(startOfDay, endOfDay)  // checkIn 기준으로 변경
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
