package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.EmployeeTraining;

import java.util.List;

/**
 * EmployeeTraining Custom Repository Interface
 * QueryDSL을 사용한 동적 쿼리 정의
 */
public interface EmployeeTrainingRepositoryCustom {

    /**
     * Training ID로 해당 교육에 등록된 EmployeeTraining 목록 조회
     * Employee, InternelUser, Position, Department를 JOIN FETCH
     *
     * @param trainingId Training ID
     * @return EmployeeTraining 목록
     */
    List<EmployeeTraining> findByTrainingIdWithAllRelations(String trainingId);

    /**
     * Employee ID로 해당 직원의 모든 교육 이력 조회
     * Training 정보를 JOIN FETCH
     *
     * @param employeeId Employee ID
     * @return EmployeeTraining 목록
     */
    List<EmployeeTraining> findByEmployeeIdWithTraining(String employeeId);
}
