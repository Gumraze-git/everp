package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Payroll Repository
 */
@Repository
public interface PayrollRepository extends JpaRepository<Payroll, String> {

    /**
     * 특정 직원의 특정 기간 내 급여가 존재하는지 확인
     */
    boolean existsByEmployeeAndBaseDateBetween(Employee employee, LocalDateTime start, LocalDateTime end);
}
