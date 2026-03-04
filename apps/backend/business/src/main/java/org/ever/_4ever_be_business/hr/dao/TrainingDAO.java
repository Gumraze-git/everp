package org.ever._4ever_be_business.hr.dao;

import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.EmployeeTraining;
import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TrainingDAO {

    /**
     * Training 프로그램 조회
     */
    Optional<Training> findTrainingById(String trainingId);

    /**
     * Training에 등록된 EmployeeTraining 목록 조회
     */
    List<EmployeeTraining> findEmployeeTrainingsByTrainingId(String trainingId);

    /**
     * 교육 프로그램 목록 검색 (동적 쿼리)
     */
    Page<Training> searchTrainingPrograms(TrainingSearchConditionVo condition, Pageable pageable);

    /**
     * Employee ID로 직원 조회 (InternelUser, Position, Department JOIN)
     */
    Optional<Employee> findEmployeeByIdWithDetails(String employeeId);

    /**
     * Employee ID로 교육 이력 조회
     */
    List<EmployeeTraining> findEmployeeTrainingsByEmployeeId(String employeeId);

    /**
     * 직원 교육 현황 목록 검색 (동적 쿼리)
     */
    Page<Employee> searchEmployeesWithTrainingInfo(EmployeeTrainingSearchConditionVo condition, Pageable pageable);
}
