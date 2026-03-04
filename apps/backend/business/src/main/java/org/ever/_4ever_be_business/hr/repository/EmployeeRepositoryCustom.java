package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeTrainingSummaryDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingStatusItemDto;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingStatusSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmployeeRepositoryCustom {
    /**
     * 직원 교육 현황 목록 검색 (동적 쿼리)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<Employee>
     */
    Page<Employee> searchEmployeesWithTrainingInfo(EmployeeTrainingSearchConditionVo condition, Pageable pageable);

    /**
     * 직원 목록 조회 (동적 쿼리)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<EmployeeListItemDto>
     */
    Page<EmployeeListItemDto> findEmployeeList(EmployeeListSearchConditionVo condition, Pageable pageable);

    /**
     * 직원 교육 현황 통계 조회
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<TrainingStatusItemDto>
     */
    Page<TrainingStatusItemDto> findTrainingStatusList(TrainingStatusSearchConditionVo condition, Pageable pageable);

    /**
     * 직원별 교육 요약 정보 조회
     *
     * @param employeeId 직원 ID
     * @return Optional<EmployeeTrainingSummaryDto>
     */
    Optional<EmployeeTrainingSummaryDto> findEmployeeTrainingSummary(String employeeId);
}
