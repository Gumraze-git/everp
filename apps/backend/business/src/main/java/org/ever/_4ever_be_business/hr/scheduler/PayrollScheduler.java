package org.ever._4ever_be_business.hr.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.service.PayrollService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 급여 관련 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PayrollScheduler {

    private final PayrollService payrollService;

    /**
     * 매월 1일 00:00:00에 모든 직원의 급여 생성
     * cron 표현식: "초 분 시 일 월 요일"
     * "0 0 0 1 * ?" = 매월 1일 자정
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void generateMonthlyPayroll() {
        log.info("========================================");
        log.info("월간 급여 자동 생성 스케줄러 실행");
        log.info("========================================");

        try {
            payrollService.generateMonthlyPayrollForAllEmployees();
            log.info("월간 급여 자동 생성 완료");
        } catch (Exception e) {
            log.error("월간 급여 자동 생성 중 오류 발생", e);
        }

        log.info("========================================");
    }
}
