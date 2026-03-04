package org.ever._4ever_be_business.hr.service;

import org.ever._4ever_be_business.hr.dto.request.CreateTrainingProgramDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeTrainingHistoryDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeTrainingListResponseDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeTrainingSummaryDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingListItemDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingResponseDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingStatusResponseDto;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingHistoryVo;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingDetailVo;
import org.ever._4ever_be_business.hr.vo.TrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingStatusSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.concurrent.CompletableFuture;

public interface TrainingService {

    /**
     * 교육 프로그램 상세 정보 조회 (비동기)
     *
     * @param vo TrainingDetailVo
     * @return CompletableFuture<TrainingResponseDto>
     */
    CompletableFuture<TrainingResponseDto> getTrainingDetail(TrainingDetailVo vo);

    /**
     * 교육 프로그램 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<TrainingListItemDto>
     */
    Page<TrainingListItemDto> getTrainingList(TrainingSearchConditionVo condition, Pageable pageable);

    /**
     * 직원 교육 이력 조회
     *
     * @param vo EmployeeTrainingHistoryVo
     * @return EmployeeTrainingHistoryDto
     */
    EmployeeTrainingHistoryDto getEmployeeTrainingHistory(EmployeeTrainingHistoryVo vo);

    /**
     * 직원 교육 현황 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return EmployeeTrainingListResponseDto
     */
    EmployeeTrainingListResponseDto getEmployeeTrainingList(EmployeeTrainingSearchConditionVo condition, Pageable pageable);

    /**
     * 교육 프로그램 생성
     *
     * @param requestDto 교육 프로그램 생성 정보
     */
    void createTrainingProgram(CreateTrainingProgramDto requestDto);

    /**
     * 직원 교육 현황 통계 조회
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return TrainingStatusResponseDto
     */
    TrainingStatusResponseDto getTrainingStatusList(TrainingStatusSearchConditionVo condition, Pageable pageable);

    /**
     * 직원별 교육 요약 정보 조회
     *
     * @param employeeId 직원 ID
     * @return EmployeeTrainingSummaryDto
     */
    EmployeeTrainingSummaryDto getEmployeeTrainingSummary(String employeeId);

    /**
     * 교육 프로그램 수정
     *
     * @param programId  교육 프로그램 ID
     * @param requestDto 수정 정보
     */
    void updateTrainingProgram(String programId, org.ever._4ever_be_business.hr.dto.request.UpdateTrainingProgramDto requestDto);
}
