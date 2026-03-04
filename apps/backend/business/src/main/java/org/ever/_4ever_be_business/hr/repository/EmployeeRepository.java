package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Employee Repository
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String>, EmployeeRepositoryCustom {
    /**
     * 특정 기간 동안 생성된 직원 수 조회
     *
     * @param start 시작 날짜
     * @param end   종료 날짜
     * @return 직원 수
     */
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    /**
     * 특정 날짜 이전에 생성된 직원 수 조회
     *
     * @param date 기준 날짜
     * @return 직원 수
     */
    long countByCreatedAtBefore(LocalDateTime date);

    /**
     * InternelUser로 Employee 조회
     *
     * @param internelUser InternelUser 엔티티
     * @return Employee
     */
    Optional<Employee> findByInternelUser(InternelUser internelUser);

    /**
     * InternelUser ID로 Employee 조회
     *
     * @param internelUserId InternelUser ID
     * @return Employee
     */
    Optional<Employee> findByInternelUserId(String internelUserId);
}
