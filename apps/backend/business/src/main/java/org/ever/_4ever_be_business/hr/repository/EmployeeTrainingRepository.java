package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.EmployeeTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * EmployeeTraining Repository
 * QueryDSL을 사용한 동적 쿼리는 EmployeeTrainingRepositoryCustom에 정의
 */
@Repository
public interface EmployeeTrainingRepository extends JpaRepository<EmployeeTraining, String>, EmployeeTrainingRepositoryCustom {
    /**
     * 특정 교육 프로그램의 수강생 수 조회
     *
     * @param trainingId 교육 프로그램 ID
     * @return 수강생 수
     */
    long countByTrainingId(String trainingId);

    /**
     * 특정 직원의 교육 이력 조회
     *
     * @param employeeId 직원 ID
     * @return 교육 이력 목록
     */
    List<EmployeeTraining> findByEmployeeId(String employeeId);
}
