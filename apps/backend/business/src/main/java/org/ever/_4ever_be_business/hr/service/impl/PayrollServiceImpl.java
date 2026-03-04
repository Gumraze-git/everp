package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dao.PayrollDAO;
import org.ever._4ever_be_business.hr.dto.response.PayrollListItemDto;
import org.ever._4ever_be_business.hr.dto.response.PaystubDetailDto;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.Payroll;
import org.ever._4ever_be_business.hr.enums.PayrollStatus;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.PayrollRepository;
import org.ever._4ever_be_business.hr.service.PayrollService;
import org.ever._4ever_be_business.hr.vo.PayrollSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

    private final PayrollDAO payrollDAO;
    private final PayrollRepository payrollRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public PaystubDetailDto getPaystubDetail(String paystubId) {
        log.info("급여 명세서 상세 조회 요청 - paystubId: {}", paystubId);

        PaystubDetailDto result = payrollDAO.findPaystubDetailById(paystubId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "급여 명세서를 찾을 수 없습니다."));

        log.info("급여 명세서 상세 조회 성공 - paystubId: {}, employeeName: {}",
                paystubId, result.getEmployee().getEmployeeName());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PayrollListItemDto> getPayrollList(PayrollSearchConditionVo condition, Pageable pageable) {
        log.info("급여 명세서 목록 조회 요청 - year: {}, month: {}, name: {}, department: {}, position: {}, page: {}, size: {}",
                condition.getYear(), condition.getMonth(), condition.getName(),
                condition.getDepartment(), condition.getPosition(),
                pageable.getPageNumber(), pageable.getPageSize());

        Page<PayrollListItemDto> result = payrollDAO.findPayrollList(condition, pageable);

        log.info("급여 명세서 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional
    public void completePayroll(String payrollId) {
        log.info("급여 지급 완료 처리 요청 - payrollId: {}", payrollId);

        // 1. Payroll 조회
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "급여 정보를 찾을 수 없습니다."));

        // 2. 지급 완료 처리
        payroll.markAsPaid();

        // 3. 저장 (Dirty Checking으로 자동 저장)
        log.info("급여 지급 완료 처리 성공 - payrollId: {}, payDate: {}", payrollId, payroll.getPayDate());
    }

    @Override
    @Transactional
    public void generateMonthlyPayrollForAllEmployees() {
        log.info("모든 직원 당월 급여 생성 시작");

        // 1. 모든 직원 조회
        var employees = employeeRepository.findAll();

        if (employees.isEmpty()) {
            log.warn("급여 생성 대상 직원이 없습니다.");
            return;
        }

        log.info("급여 생성 대상 직원 수: {}", employees.size());

        // 2. 현재 날짜 기준으로 급여 생성
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime baseDate = LocalDateTime.of(now.getYear(), now.getMonthValue(), 1, 0, 0);

        // 당월 시작일과 종료일 계산 (idempotency 체크용)
        LocalDateTime monthStart = baseDate;
        LocalDateTime monthEnd = baseDate.plusMonths(1);

        int successCount = 0;
        int skipCount = 0;
        for (Employee employee : employees) {
            try {
                // 3. 이미 당월 급여가 존재하는지 확인 (idempotency)
                boolean exists = payrollRepository.existsByEmployeeAndBaseDateBetween(
                        employee, monthStart, monthEnd);

                if (exists) {
                    log.debug("급여 이미 존재하므로 건너뜀 - employeeId: {}, employeeName: {}",
                            employee.getId(),
                            employee.getInternelUser().getName());
                    skipCount++;
                    continue;
                }

                // 4. 각 직원의 기본급 조회 (Position의 salary 사용)
                BigDecimal baseSalary = employee.getInternelUser().getPosition() != null
                        ? employee.getInternelUser().getPosition().getSalary()
                        : BigDecimal.ZERO;

                // 5. 급여 계산 (기본급 + 초과근무수당 - 공제액)
                // 여기서는 단순히 기본급만 사용하고 초과근무수당은 0으로 설정
                BigDecimal overtimeSalary = BigDecimal.ZERO;
                BigDecimal netSalary = baseSalary.add(overtimeSalary);

                // 6. Payroll 엔티티 생성
                Payroll payroll = new Payroll(
                        employee,
                        baseSalary,
                        overtimeSalary,
                        PayrollStatus.PAYROLL_UNPAID,  // 초기 상태: 미지급
                        netSalary,
                        null,  // payDate: 아직 지급 전
                        baseDate  // baseDate: 급여 기준일 (당월 1일)
                );

                payrollRepository.save(payroll);
                successCount++;

                log.debug("급여 생성 완료 - employeeId: {}, employeeName: {}, baseSalary: {}, netSalary: {}",
                        employee.getId(),
                        employee.getInternelUser().getName(),
                        baseSalary,
                        netSalary);

            } catch (Exception e) {
                log.error("급여 생성 실패 - employeeId: {}, employeeName: {}",
                        employee.getId(),
                        employee.getInternelUser().getName(),
                        e);
            }
        }

        log.info("모든 직원 당월 급여 생성 완료 - 성공: {}, 건너뜀: {}, 전체: {}",
                successCount, skipCount, employees.size());
    }
}
