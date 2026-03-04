package org.ever._4ever_be_business.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.entity.Deducation;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.Payroll;
import org.ever._4ever_be_business.hr.entity.PayrollDeducation;
import org.ever._4ever_be_business.hr.enums.PayrollStatus;
import org.ever._4ever_be_business.hr.repository.DeducationRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.PayrollDeducationRepository;
import org.ever._4ever_be_business.hr.repository.PayrollRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * 급여 데이터 초기화
 * InternalUserInitializer에서 생성된 직원들의 9월, 10월 급여 데이터 생성
 */
@Slf4j
@Component
@Order(100) // InternalUserInitializer 이후에 실행
@RequiredArgsConstructor
public class PayrollInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final PayrollRepository payrollRepository;
    private final DeducationRepository deducationRepository;
    private final PayrollDeducationRepository payrollDeducationRepository;

    private static final Random RANDOM = new Random();

    // 공제 항목들 (전역으로 관리)
    private Deducation nationalPension;
    private Deducation healthInsurance;
    private Deducation employmentInsurance;
    private Deducation incomeTax;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            log.info("========================================");
            log.info("[PayrollInitializer] 급여 데이터 생성 시작");
            log.info("========================================");

            // 1. 공제 항목 생성 (이미 존재하면 건너뛰기)
            initializeDeductions();

            List<Employee> employees = employeeRepository.findAll();
            log.info("[PayrollInitializer] Employee 조회 결과: {} 명", employees.size());

            if (employees.isEmpty()) {
                log.warn("[PayrollInitializer] 직원 데이터가 없어 급여 데이터를 생성하지 않습니다.");
                return;
            }

            log.info("[PayrollInitializer] 총 {} 명의 직원에 대한 급여 데이터 생성", employees.size());

        // 현재 연도 기준
        int currentYear = LocalDateTime.now().getYear();

        // 9월 급여 (지급 완료)
        LocalDateTime septemberBaseDate = LocalDateTime.of(currentYear, 9, 1, 0, 0);
        LocalDateTime septemberPayDate = LocalDateTime.of(currentYear, 10, 5, 10, 0);

        // 10월 급여 (대부분 지급 완료, 20개 중 1개만 미지급)
        LocalDateTime octoberBaseDate = LocalDateTime.of(currentYear, 10, 1, 0, 0);
        LocalDateTime octoberPayDate = LocalDateTime.of(currentYear, 11, 5, 10, 0);

        int employeeCount = 0;
        for (Employee employee : employees) {
            employeeCount++;

            // 9월 급여 (이미 존재하면 건너뛰기)
            if (!payrollRepository.existsByEmployeeAndBaseDateBetween(
                    employee,
                    septemberBaseDate,
                    septemberBaseDate.plusMonths(1))) {
                Payroll septemberPayroll = createPayrollWithDeductions(
                        employee,
                        septemberBaseDate,
                        PayrollStatus.PAYROLL_PAID,
                        septemberPayDate
                );
                log.debug("[Initializer] 9월 급여 생성 - employeeId: {}, netSalary: {}",
                        employee.getId(), septemberPayroll.getNetSalary());
            }

            // 10월 급여 (20개 중 1개만 미지급)
            if (!payrollRepository.existsByEmployeeAndBaseDateBetween(
                    employee,
                    octoberBaseDate,
                    octoberBaseDate.plusMonths(1))) {
                // 20개 중 1개만 UNPAID (5% 확률)
                boolean isUnpaid = (employeeCount % 20 == 0);
                PayrollStatus status = isUnpaid ? PayrollStatus.PAYROLL_UNPAID : PayrollStatus.PAYROLL_PAID;
                LocalDateTime payDate = isUnpaid ? null : octoberPayDate;

                Payroll octoberPayroll = createPayrollWithDeductions(
                        employee,
                        octoberBaseDate,
                        status,
                        payDate
                );
                log.debug("[PayrollInitializer] 10월 급여 생성 - employeeId: {}, status: {}, netSalary: {}",
                        employee.getId(), status, octoberPayroll.getNetSalary());
            }
        }

            log.info("========================================");
            log.info("[PayrollInitializer] 급여 데이터 생성 완료");
            log.info("========================================");

        } catch (Exception e) {
            log.error("========================================");
            log.error("[PayrollInitializer] 급여 데이터 생성 중 오류 발생", e);
            log.error("========================================");
            throw e;
        }
    }

    /**
     * 공제 항목 초기화
     */
    private void initializeDeductions() {
        // 국민연금 (기본값 사용, 실제로는 급여에 따라 계산됨)
        nationalPension = deducationRepository.findAll().stream()
                .filter(d -> "국민연금".equals(d.getTitle()))
                .findFirst()
                .orElseGet(() -> {
                    Deducation deduction = new Deducation("국민연금", BigDecimal.valueOf(200_000));
                    return deducationRepository.save(deduction);
                });

        // 건강보험
        healthInsurance = deducationRepository.findAll().stream()
                .filter(d -> "건강보험".equals(d.getTitle()))
                .findFirst()
                .orElseGet(() -> {
                    Deducation deduction = new Deducation("건강보험", BigDecimal.valueOf(150_000));
                    return deducationRepository.save(deduction);
                });

        // 고용보험
        employmentInsurance = deducationRepository.findAll().stream()
                .filter(d -> "고용보험".equals(d.getTitle()))
                .findFirst()
                .orElseGet(() -> {
                    Deducation deduction = new Deducation("고용보험", BigDecimal.valueOf(50_000));
                    return deducationRepository.save(deduction);
                });

        // 소득세
        incomeTax = deducationRepository.findAll().stream()
                .filter(d -> "소득세".equals(d.getTitle()))
                .findFirst()
                .orElseGet(() -> {
                    Deducation deduction = new Deducation("소득세", BigDecimal.valueOf(300_000));
                    return deducationRepository.save(deduction);
                });

        log.info("[Initializer] 공제 항목 초기화 완료");
    }

    /**
     * 급여 데이터 생성 (공제 포함)
     */
    private Payroll createPayrollWithDeductions(
            Employee employee,
            LocalDateTime baseDate,
            PayrollStatus status,
            LocalDateTime payDate
    ) {
        // 직급에 따른 기본급 설정
        BigDecimal baseSalary = calculateBaseSalary(employee);

        // 초과근무 수당 (랜덤: 0 ~ 500,000)
        BigDecimal overtimeSalary = BigDecimal.valueOf(RANDOM.nextInt(6) * 100_000);

        // 총 지급액 = 기본급 + 초과근무 수당
        BigDecimal totalPay = baseSalary.add(overtimeSalary);

        // 공제액 계산 (급여 기반)
        BigDecimal pensionAmount = calculatePension(totalPay);
        BigDecimal healthAmount = calculateHealthInsurance(totalPay);
        BigDecimal employmentAmount = calculateEmploymentInsurance(totalPay);
        BigDecimal taxAmount = calculateIncomeTax(totalPay);
        BigDecimal totalDeduction = pensionAmount.add(healthAmount).add(employmentAmount).add(taxAmount);

        // 순급여 = 총 지급액 - 공제액
        BigDecimal netSalary = totalPay.subtract(totalDeduction);

        // Payroll 저장
        Payroll payroll = new Payroll(
                employee,
                baseSalary,
                overtimeSalary,
                status,
                netSalary,
                payDate,
                baseDate
        );
        payroll = payrollRepository.save(payroll);

        // PayrollDeducation 생성 (각 공제 항목마다)
        createPayrollDeduction(payroll, nationalPension, pensionAmount);
        createPayrollDeduction(payroll, healthInsurance, healthAmount);
        createPayrollDeduction(payroll, employmentInsurance, employmentAmount);
        createPayrollDeduction(payroll, incomeTax, taxAmount);

        return payroll;
    }

    /**
     * PayrollDeducation 생성
     */
    private void createPayrollDeduction(Payroll payroll, Deducation deducation, BigDecimal amount) {
        // amount를 가진 임시 Deducation 생성 (실제로는 같은 deducation이지만 금액만 다름)
        // 하지만 Deducation 엔티티 구조상 title과 amount를 가지므로,
        // 각 직원별로 다른 금액의 공제를 위해서는 새로운 Deducation을 만들어야 함
        Deducation specificDeduction = new Deducation(deducation.getTitle(), amount);
        specificDeduction = deducationRepository.save(specificDeduction);

        PayrollDeducation payrollDeducation = new PayrollDeducation(payroll, specificDeduction);
        payrollDeducationRepository.save(payrollDeducation);
    }

    /**
     * 국민연금 계산 (급여의 4.5%)
     */
    private BigDecimal calculatePension(BigDecimal salary) {
        return salary.multiply(BigDecimal.valueOf(0.045)).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 건강보험 계산 (급여의 3.545%)
     */
    private BigDecimal calculateHealthInsurance(BigDecimal salary) {
        return salary.multiply(BigDecimal.valueOf(0.03545)).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 고용보험 계산 (급여의 0.9%)
     */
    private BigDecimal calculateEmploymentInsurance(BigDecimal salary) {
        return salary.multiply(BigDecimal.valueOf(0.009)).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 소득세 계산 (급여의 약 6% - 간이세액표 기준 간단 계산)
     */
    private BigDecimal calculateIncomeTax(BigDecimal salary) {
        return salary.multiply(BigDecimal.valueOf(0.06)).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 직급에 따른 기본급 계산
     */
    private BigDecimal calculateBaseSalary(Employee employee) {
        String positionCode = employee.getInternelUser().getPosition().getPositionCode();

        // 관리자 (차장급 이상): 5,000,000 ~ 6,000,000
        if (positionCode != null && (
                positionCode.contains("ADMIN") ||
                positionCode.equals("POS-00505") || // 차장
                positionCode.equals("POS-00506") || // 부장
                positionCode.equals("POS-00507")    // 이사
        )) {
            return BigDecimal.valueOf(5_000_000 + RANDOM.nextInt(11) * 100_000);
        }

        // 일반 사원: 3,000,000 ~ 3,500,000
        return BigDecimal.valueOf(3_000_000 + RANDOM.nextInt(6) * 100_000);
    }
}
