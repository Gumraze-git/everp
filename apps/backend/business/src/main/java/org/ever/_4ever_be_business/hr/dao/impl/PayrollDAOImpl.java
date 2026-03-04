package org.ever._4ever_be_business.hr.dao.impl;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.dao.PayrollDAO;
import org.ever._4ever_be_business.hr.dto.response.*;
import org.ever._4ever_be_business.hr.vo.PayrollSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.ever._4ever_be_business.hr.entity.QPayroll.payroll;
import static org.ever._4ever_be_business.hr.entity.QEmployee.employee;
import static org.ever._4ever_be_business.hr.entity.QInternelUser.internelUser;
import static org.ever._4ever_be_business.hr.entity.QPosition.position;
import static org.ever._4ever_be_business.hr.entity.QDepartment.department;
import static org.ever._4ever_be_business.hr.entity.QPayrollDeducation.payrollDeducation;
import static org.ever._4ever_be_business.hr.entity.QDeducation.deducation;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PayrollDAOImpl implements PayrollDAO {

    private final JPAQueryFactory queryFactory;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Optional<PaystubDetailDto> findPaystubDetailById(String paystubId) {
        log.debug("급여 명세서 상세 조회 시작 - paystubId: {}", paystubId);

        // 1. 기본 Payroll 정보 + Employee 정보 조회
        var payrollInfo = queryFactory
                .select(Projections.constructor(
                        PayrollInfoProjection.class,
                        payroll.id,
                        employee.id,
                        internelUser.employeeCode,
                        internelUser.name,
                        department.departmentName,
                        position.positionName,
                        payroll.baseSalary,
                        payroll.overtimeSalary,
                        payroll.netSalary,
                        payroll.status,
                        payroll.payDate
                ))
                .from(payroll)
                .innerJoin(payroll.employee, employee)
                .innerJoin(employee.internelUser, internelUser)
                .innerJoin(internelUser.position, position)
                .innerJoin(position.department, department)
                .where(payroll.id.eq(paystubId))
                .fetchOne();

        if (payrollInfo == null) {
            log.debug("급여 명세서를 찾을 수 없음 - paystubId: {}", paystubId);
            return Optional.empty();
        }

        // 2. 공제 항목 조회
        List<PayItemDto> deductionItems = queryFactory
                .select(Projections.constructor(
                        PayItemDto.class,
                        deducation.title,
                        deducation.amount.negate() // 음수로 변환
                ))
                .from(payrollDeducation)
                .innerJoin(payrollDeducation.deduction, deducation)
                .where(payrollDeducation.payroll.id.eq(paystubId))
                .fetch();

        // 3. 총 공제액 계산
        BigDecimal totalDeduction = deductionItems.stream()
                .map(PayItemDto::getItemSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. basePay 세부 항목 구성 (현재 엔티티에 세부 항목이 없으므로 임시로 구성)
        List<PayItemDto> basePayItems = new ArrayList<>();
        // Position의 salary를 정기 급여로 사용
        BigDecimal regularPay = payrollInfo.getBaseSalary().multiply(BigDecimal.valueOf(0.93)); // 93%를 정기급여로
        BigDecimal allowance = payrollInfo.getBaseSalary().subtract(regularPay); // 나머지를 수당으로

        basePayItems.add(new PayItemDto("정기 급여", regularPay));
        basePayItems.add(new PayItemDto("직책 수당", allowance));

        // 5. overtimePay 세부 항목 구성 (임시)
        List<PayItemDto> overtimePayItems = new ArrayList<>();
        if (payrollInfo.getOvertimeSalary() != null && payrollInfo.getOvertimeSalary().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal nightPay = payrollInfo.getOvertimeSalary().multiply(BigDecimal.valueOf(0.67)); // 67%를 야간근무로
            BigDecimal holidayPay = payrollInfo.getOvertimeSalary().subtract(nightPay); // 나머지를 휴일근무로

            overtimePayItems.add(new PayItemDto("야간 근무 수당", nightPay));
            overtimePayItems.add(new PayItemDto("휴일 근무 수당", holidayPay));
        }

        // 6. DTO 조립
        PaystubEmployeeDto employeeDto = new PaystubEmployeeDto(
                payrollInfo.getEmployeeId(),
                payrollInfo.getEmployeeNumber(),
                payrollInfo.getEmployeeName(),
                payrollInfo.getDepartment(),
                payrollInfo.getPosition()
        );

        PaystubPayDto payDto = new PaystubPayDto(
                payrollInfo.getBaseSalary(),
                basePayItems,
                payrollInfo.getOvertimeSalary() != null ? payrollInfo.getOvertimeSalary() : BigDecimal.ZERO,
                overtimePayItems,
                totalDeduction,
                deductionItems,
                payrollInfo.getNetSalary()
        );

        PaystubDetailDto result = new PaystubDetailDto(
                payrollInfo.getPayrollId(),
                employeeDto,
                payDto,
                payrollInfo.getStatus().name(),
                payrollInfo.getPayDate() != null ? payrollInfo.getPayDate().format(DATE_FORMATTER) : null
        );

        log.debug("급여 명세서 상세 조회 완료 - paystubId: {}, employeeName: {}",
                paystubId, payrollInfo.getEmployeeName());

        return Optional.of(result);
    }

    @Override
    public Page<PayrollListItemDto> findPayrollList(PayrollSearchConditionVo condition, Pageable pageable) {
        log.debug("급여 명세서 목록 조회 시작 - year: {}, month: {}, name: {}, department: {}, position: {}, statusCode: {}",
                condition.getYear(), condition.getMonth(), condition.getName(),
                condition.getDepartment(), condition.getPosition(), condition.getStatusCode());

        // 1. 동적 쿼리 조건 생성
        var query = queryFactory
                .select(Projections.constructor(
                        PayrollListProjection.class,
                        payroll.id,
                        employee.id,
                        internelUser.name,
                        department.id,
                        department.departmentName,
                        position.id,
                        position.positionName,
                        payroll.baseSalary,
                        payroll.overtimeSalary,
                        payroll.netSalary,
                        payroll.status
                ))
                .from(payroll)
                .innerJoin(payroll.employee, employee)
                .innerJoin(employee.internelUser, internelUser)
                .innerJoin(internelUser.position, position)
                .innerJoin(position.department, department)
                .where(
                        yearEq(condition.getYear()),
                        monthEq(condition.getMonth()),
                        nameContains(condition.getName()),
                        departmentEq(condition.getDepartment()),
                        positionEq(condition.getPosition()),
                        statusCodeEq(condition.getStatusCode())
                );

        // 2. 전체 카운트 조회
        long total = query.fetchCount();

        // 3. 페이징 적용하여 데이터 조회
        List<PayrollListProjection> projections = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(payroll.baseDate.desc(), payroll.id.desc())
                .fetch();

        // 4. 각 Payroll의 공제액 계산
        List<PayrollListItemDto> content = projections.stream()
                .map(proj -> {
                    // 공제액 조회
                    BigDecimal totalDeduction = queryFactory
                            .select(deducation.amount.sum().coalesce(BigDecimal.ZERO))
                            .from(payrollDeducation)
                            .innerJoin(payrollDeducation.deduction, deducation)
                            .where(payrollDeducation.payroll.id.eq(proj.getPayrollId()))
                            .fetchOne();

                    if (totalDeduction == null) {
                        totalDeduction = BigDecimal.ZERO;
                    }

                    PayrollListEmployeeDto employeeDto = new PayrollListEmployeeDto(
                            proj.getEmployeeId(),
                            proj.getEmployeeName(),
                            proj.getDepartmentId(),
                            proj.getDepartment(),
                            proj.getPositionId(),
                            proj.getPosition()
                    );

                    PayrollListPayDto payDto = new PayrollListPayDto(
                            proj.getBasePay(),
                            proj.getOvertimePay() != null ? proj.getOvertimePay() : BigDecimal.ZERO,
                            totalDeduction,
                            proj.getNetPay(),
                            proj.getStatus().name()
                    );

                    return new PayrollListItemDto(proj.getPayrollId(), employeeDto, payDto);
                })
                .toList();

        log.debug("급여 명세서 목록 조회 완료 - totalElements: {}, pageSize: {}",
                total, content.size());

        return new PageImpl<>(content, pageable, total);
    }

    // 동적 쿼리 조건 메서드들
    private com.querydsl.core.types.dsl.BooleanExpression yearEq(Integer year) {
        if (year == null) return null;
        return com.querydsl.core.types.dsl.Expressions.numberTemplate(Integer.class,
                "EXTRACT(YEAR FROM {0})", payroll.baseDate).eq(year);
    }

    private com.querydsl.core.types.dsl.BooleanExpression monthEq(Integer month) {
        if (month == null) return null;
        return com.querydsl.core.types.dsl.Expressions.numberTemplate(Integer.class,
                "EXTRACT(MONTH FROM {0})", payroll.baseDate).eq(month);
    }

    private com.querydsl.core.types.dsl.BooleanExpression nameContains(String name) {
        return name != null ? internelUser.name.contains(name) : null;
    }

    private com.querydsl.core.types.dsl.BooleanExpression departmentEq(String departmentId) {
        return departmentId != null ? department.id.eq(departmentId) : null;
    }

    private com.querydsl.core.types.dsl.BooleanExpression positionEq(String positionId) {
        return positionId != null ? position.id.eq(positionId) : null;
    }

    private com.querydsl.core.types.dsl.BooleanExpression statusCodeEq(String statusCode) {
        if (statusCode == null) return null;
        try {
            org.ever._4ever_be_business.hr.enums.PayrollStatus status =
                    org.ever._4ever_be_business.hr.enums.PayrollStatus.valueOf(statusCode);
            return payroll.status.eq(status);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid payroll status code: {}", statusCode);
            return null;
        }
    }

    /**
     * QueryDSL Projection용 내부 클래스들
     */
    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class PayrollInfoProjection {
        private String payrollId;
        private String employeeId;
        private String employeeNumber;
        private String employeeName;
        private String department;
        private String position;
        private BigDecimal baseSalary;
        private BigDecimal overtimeSalary;
        private BigDecimal netSalary;
        private org.ever._4ever_be_business.hr.enums.PayrollStatus status;
        private java.time.LocalDateTime payDate;
    }

    @lombok.Getter
    @lombok.AllArgsConstructor
    public static class PayrollListProjection {
        private String payrollId;
        private String employeeId;
        private String employeeName;
        private String departmentId;
        private String department;
        private String positionId;
        private String position;
        private BigDecimal basePay;
        private BigDecimal overtimePay;
        private BigDecimal netPay;
        private org.ever._4ever_be_business.hr.enums.PayrollStatus status;
    }
}
