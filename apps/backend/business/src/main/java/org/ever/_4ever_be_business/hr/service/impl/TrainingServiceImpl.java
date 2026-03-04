package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dao.TrainingDAO;
import org.ever._4ever_be_business.hr.dto.request.CreateTrainingProgramDto;
import org.ever._4ever_be_business.hr.dto.request.UpdateTrainingProgramDto;
import org.ever._4ever_be_business.hr.dto.response.*;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.EmployeeTraining;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeTrainingRepository;
import org.ever._4ever_be_business.hr.repository.TrainingRepository;
import org.ever._4ever_be_business.hr.service.TrainingService;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingHistoryVo;
import org.ever._4ever_be_business.hr.vo.EmployeeTrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingDetailVo;
import org.ever._4ever_be_business.hr.vo.TrainingSearchConditionVo;
import org.ever._4ever_be_business.hr.vo.TrainingStatusSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDAO trainingDAO;
    private final EmployeeTrainingRepository employeeTrainingRepository;
    private final TrainingRepository trainingRepository;
    private final EmployeeRepository employeeRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    @Transactional(readOnly = true)
    public CompletableFuture<TrainingResponseDto> getTrainingDetail(TrainingDetailVo vo) {
        String programId = vo.getProgramId();

        // 1. Training 정보 조회
        Training training = trainingDAO.findTrainingById(programId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 교육 프로그램입니다. ID: " + programId));

        // 2. Training에 등록된 EmployeeTraining 목록 조회 (InternelUser, Position, Department 포함)
        List<EmployeeTraining> employeeTrainings = trainingDAO.findEmployeeTrainingsByTrainingId(programId);

        // 3. EmployeeTraining을 DesignatedEmployee로 변환
        List<DesignatedEmployee> designatedEmployees = mapToDesignatedEmployees(employeeTrainings);

        // 4. 최종 Response DTO 생성
        TrainingResponseDto response = new TrainingResponseDto(
                "ONLINE".equalsIgnoreCase(training.getDeliveryMethod()),
                training.getId(),
                training.getTrainingName(),
                training.getDescription(),
                training.getCategory(),
                training.getDurationHours(),
                training.getCreatedAt() != null ? training.getCreatedAt().format(DATE_FORMATTER) : null,
                training.getTrainingStatus() != null ? training.getTrainingStatus().name() : "IN_PROGRESS",
                designatedEmployees
        );

        // 5. 비동기 응답 반환
        return CompletableFuture.completedFuture(response);
    }

    /**
     * EmployeeTraining을 DesignatedEmployee로 변환
     * 로컬 InternelUser, Position, Department 엔티티 사용
     */
    private List<DesignatedEmployee> mapToDesignatedEmployees(List<EmployeeTraining> employeeTrainings) {
        return employeeTrainings.stream()
                .map(employeeTraining -> {
                    InternelUser internelUser = employeeTraining.getEmployee().getInternelUser();
                    Position position = internelUser != null ? internelUser.getPosition() : null;

                    return new DesignatedEmployee(
                            internelUser != null ? internelUser.getId() : null,
                            internelUser != null ? internelUser.getName() : "알 수 없음",
                            position != null && position.getDepartment() != null
                                    ? position.getDepartment().getDepartmentName()
                                    : "미지정",
                            position != null ? position.getPositionName() : "미지정",
                            employeeTraining.getCompletionStatus() == TrainingCompletionStatus.COMPLETED ? "COMPLETED" : "INCOMPLETED",
                            employeeTraining.getUpdatedAt() != null && employeeTraining.getCompletionStatus() == TrainingCompletionStatus.COMPLETED
                                    ? employeeTraining.getUpdatedAt().format(DATE_FORMATTER)
                                    : null
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TrainingListItemDto> getTrainingList(TrainingSearchConditionVo condition, Pageable pageable) {
        // 1. DAO를 통해 교육 프로그램 목록 조회
        Page<Training> trainingPage = trainingDAO.searchTrainingPrograms(condition, pageable);

        // 2. Training 엔티티를 TrainingListItemDto로 변환
        // capacity는 해당 교육을 듣고 있는 실제 수강생 수
        List<TrainingListItemDto> content = trainingPage.getContent().stream()
                .map(training -> {
                    // 각 교육 프로그램별 실제 수강생 수 조회
                    long enrolledCount = employeeTrainingRepository.countByTrainingId(training.getId());

                    return new TrainingListItemDto(
                            training.getId(),
                            training.getTrainingName(),
                            training.getTrainingStatus(),
                            training.getCategory(),
                            training.getDurationHours() != null ? training.getDurationHours().intValue() : 0,
                            "ONLINE".equalsIgnoreCase(training.getDeliveryMethod()),
                            (int) enrolledCount  // 실제 수강생 수
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, trainingPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeTrainingHistoryDto getEmployeeTrainingHistory(EmployeeTrainingHistoryVo vo) {
        String employeeId = vo.getEmployeeId();

        // 1. Employee 정보 조회 (InternelUser, Position, Department 포함)
        Employee employee = trainingDAO.findEmployeeByIdWithDetails(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 직원입니다. ID: " + employeeId));

        InternelUser internelUser = employee.getInternelUser();
        Position position = internelUser != null ? internelUser.getPosition() : null;

        // 2. 교육 이력 조회
        List<EmployeeTraining> trainingHistory = trainingDAO.findEmployeeTrainingsByEmployeeId(employeeId);

        // 3. 통계 계산
        long completedCount = trainingHistory.stream()
                .filter(et -> et.getCompletionStatus() == TrainingCompletionStatus.COMPLETED)
                .count();

        long requiredMissingCount = trainingHistory.stream()
                .filter(et -> et.getCompletionStatus() == TrainingCompletionStatus.IN_PROGRESS)
                .count();

        // 4. 프로그램 이력 변환
        List<ProgramHistoryItemDto> programHistory = trainingHistory.stream()
                .map(et -> new ProgramHistoryItemDto(
                        et.getTraining().getId(),
                        et.getTraining().getTrainingName(),
                        et.getCompletionStatus() == TrainingCompletionStatus.COMPLETED ? "COMPLETED" : "INCOMPLETED",
                        et.getCompletionStatus() == TrainingCompletionStatus.COMPLETED ? et.getUpdatedAt() : null
                ))
                .collect(Collectors.toList());

        // 5. DTO 생성 및 반환
        return new EmployeeTrainingHistoryDto(
                employeeId,
                internelUser != null ? internelUser.getName() : "알 수 없음",
                position != null && position.getDepartment() != null
                        ? position.getDepartment().getDepartmentName()
                        : "미지정",
                position != null ? position.getPositionName() : "미지정",
                (int) completedCount,
                (int) requiredMissingCount,
                employee.getLastTrainingDate(),
                programHistory
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeTrainingListResponseDto getEmployeeTrainingList(EmployeeTrainingSearchConditionVo condition, Pageable pageable) {
        // 1. DAO를 통해 직원 목록 조회
        Page<Employee> employeePage = trainingDAO.searchEmployeesWithTrainingInfo(condition, pageable);

        // 2. 각 직원의 교육 통계 조회 및 DTO 변환
        List<EmployeeTrainingListItemDto> items = employeePage.getContent().stream()
                .map(employee -> {
                    InternelUser internelUser = employee.getInternelUser();
                    Position position = internelUser != null ? internelUser.getPosition() : null;

                    // 해당 직원의 교육 이력 조회
                    List<EmployeeTraining> trainingHistory = employeeTrainingRepository.findByEmployeeIdWithTraining(employee.getId());

                    // 통계 계산
                    long completedCount = trainingHistory.stream()
                            .filter(et -> et.getCompletionStatus() == TrainingCompletionStatus.COMPLETED)
                            .count();

                    long inProgressCount = trainingHistory.stream()
                            .filter(et -> et.getCompletionStatus() == TrainingCompletionStatus.IN_PROGRESS)
                            .count();

                    return new EmployeeTrainingListItemDto(
                            employee.getId(),
                            internelUser != null ? internelUser.getName() : "알 수 없음",
                            position != null && position.getDepartment() != null
                                    ? position.getDepartment().getDepartmentName()
                                    : "미지정",
                            position != null ? position.getPositionName() : "미지정",
                            (int) completedCount,
                            (int) inProgressCount,
                            (int) inProgressCount, // requiredMissingCount는 inProgressCount와 동일하게 처리
                            employee.getLastTrainingDate()
                    );
                })
                .collect(Collectors.toList());

        // 3. PageInfo 생성
        PageInfoDto pageInfo = new PageInfoDto(
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.hasNext()
        );

        // 4. EmployeeTrainingListResponseDto 생성 및 반환
        return new EmployeeTrainingListResponseDto(items, pageInfo);
    }

    @Override
    @Transactional
    public void createTrainingProgram(CreateTrainingProgramDto requestDto) {
        log.info("교육 프로그램 생성 요청 - programName: {}, category: {}",
                requestDto.getProgramName(), requestDto.getCategory());

        // 1. Training 엔티티 생성
        Training training = new Training(
                requestDto.getProgramName(),
                requestDto.getCategory(),
                requestDto.getTrainingHour() != null ? requestDto.getTrainingHour().longValue() : 0L,
                requestDto.getIsOnline() ? "ONLINE" : "OFFLINE",
                0L, // enrolled - initially 0
                requestDto.getCapacity(),
                requestDto.getDescription(),
                true, // status - active
                TrainingStatus.RECRUITING // default to RECRUITING for new programs
        );

        // 2. Training 저장
        trainingRepository.save(training);

        log.info("교육 프로그램 생성 성공 - programId: {}, programName: {}",
                training.getId(), training.getTrainingName());
    }

    @Override
    @Transactional(readOnly = true)
    public TrainingStatusResponseDto getTrainingStatusList(TrainingStatusSearchConditionVo condition, Pageable pageable) {
        log.info("직원 교육 현황 통계 조회 - department: {}, position: {}, name: {}",
                condition.getDepartment(), condition.getPosition(), condition.getName());

        // Repository를 통해 교육 현황 통계 조회
        Page<TrainingStatusItemDto> page = employeeRepository.findTrainingStatusList(condition, pageable);

        // PageInfoDto 생성
        PageInfoDto pageInfo = new PageInfoDto(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext()
        );

        TrainingStatusResponseDto response = new TrainingStatusResponseDto(page.getContent(), pageInfo);

        log.info("직원 교육 현황 통계 조회 성공 - totalElements: {}", page.getTotalElements());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeTrainingSummaryDto getEmployeeTrainingSummary(String employeeId) {
        log.info("직원별 교육 요약 정보 조회 - employeeId: {}", employeeId);

        // Repository를 통해 직원 교육 요약 조회
        EmployeeTrainingSummaryDto result = employeeRepository.findEmployeeTrainingSummary(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 직원입니다."));

        log.info("직원별 교육 요약 정보 조회 성공 - employeeId: {}, employeeName: {}",
                employeeId, result.getEmployeeName());

        return result;
    }

    @Override
    @Transactional
    public void updateTrainingProgram(String programId, UpdateTrainingProgramDto requestDto) {
        log.info("교육 프로그램 수정 요청 - programId: {}, programName: {}, statusCode: {}",
                programId, requestDto.getProgramName(), requestDto.getStatusCode());

        // 1. Training 조회
        Training training = trainingRepository.findById(programId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "교육 프로그램을 찾을 수 없습니다."));

        // 2. TrainingStatus 파싱 (statusCode가 제공된 경우)
        TrainingStatus trainingStatus = null;
        if (requestDto.getStatusCode() != null && !requestDto.getStatusCode().isEmpty()) {
            try {
                trainingStatus = TrainingStatus.valueOf(requestDto.getStatusCode());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "유효하지 않은 상태 코드입니다: " + requestDto.getStatusCode());
            }
        }

        // 3. Training 정보 업데이트
        training.updateTrainingProgram(requestDto.getProgramName(), trainingStatus);

        // 4. 저장 (Dirty Checking으로 자동 저장)
        log.info("교육 프로그램 수정 성공 - programId: {}, programName: {}, trainingStatus: {}",
                programId, training.getTrainingName(), training.getTrainingStatus());
    }
}
