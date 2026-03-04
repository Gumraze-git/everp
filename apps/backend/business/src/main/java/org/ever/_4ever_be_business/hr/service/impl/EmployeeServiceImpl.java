package org.ever._4ever_be_business.hr.service.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.async.AsyncResultManager;
import org.ever._4ever_be_business.common.dto.response.ApiResponse;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.common.saga.CompensationHandler;
import org.ever._4ever_be_business.common.saga.SagaCompensationService;
import org.ever._4ever_be_business.common.saga.SagaTransactionManager;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.hr.dao.EmployeeDAO;
import org.ever._4ever_be_business.hr.dto.request.EmployeeCreateRequestDto;
import org.ever._4ever_be_business.hr.dto.request.TrainingRequestDto;
import org.ever._4ever_be_business.hr.dto.request.UpdateEmployeeRequestDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeDetailDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeListItemDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeTrainingItemDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeWithTrainingDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingProgramSimpleDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeProfileDto;
import org.ever._4ever_be_business.hr.dto.response.EmployeeAttendanceRecordDto;
import org.ever._4ever_be_business.hr.dto.response.TodayAttendanceDto;
import org.ever._4ever_be_business.hr.dto.response.TrainingItemDto;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.repository.AttendanceRepository;
import org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.EmployeeTraining;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeTrainingRepository;
import org.ever._4ever_be_business.hr.repository.PositionRepository;
import org.ever._4ever_be_business.hr.repository.TrainingRepository;
import org.ever._4ever_be_business.hr.entity.*;
import org.ever._4ever_be_business.hr.enums.UserStatus;
import org.ever._4ever_be_business.hr.integration.port.UserServicePort;
import org.ever._4ever_be_business.hr.repository.*;
import org.ever._4ever_be_business.hr.service.EmployeeService;
import org.ever._4ever_be_business.hr.vo.EmployeeListSearchConditionVo;
import org.ever.event.CreateAuthUserEvent;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeDAO employeeDAO;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final TrainingRepository trainingRepository;
    private final EmployeeTrainingRepository employeeTrainingRepository;
    private final DepartmentRepository departmentRepository;
    private final InternelUserRepository internalUserRepository;
    private final CustomerUserRepository customerUserRepository;
    private final AttendanceRepository attendanceRepository;
    private final AsyncResultManager asyncResultManager;
    private final SagaTransactionManager sagaManager;
    private final UserServicePort userServicePort;
    private final SagaCompensationService compensationService;

    @PostConstruct
    public void init() {
        // 엔티티에 대한 보상 핸들러 등록
        compensationService.registerCompensationHandler("internelUser", new CompensationHandler() {
            @Override
            public void restore(Object entity) {
                if (entity instanceof InternelUser) {
                    InternelUser internelUser = (InternelUser) entity;
                    log.info("엔티티 복원: id={}", internelUser.getId());
                    internalUserRepository.save(internelUser);
                }
            }

            @Override
            public void delete(String entityId) {
                log.info("엔티티 삭제: id={}", entityId);
                internalUserRepository.findById(entityId)
                    .ifPresent(internalUserRepository::delete);
            }

            @Override
            public Class<?> getEntityClass() {
                return InternelUser.class;
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDetailDto getEmployeeDetail(String employeeId) {
        log.info("직원 상세 정보 조회 요청 - employeeId: {}", employeeId);

        EmployeeDetailDto employeeDetail = employeeDAO.findEmployeeDetailById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 교육 이력 조회
        List<EmployeeTraining> employeeTrainings = employeeTrainingRepository.findByEmployeeId(employeeId);

        List<EmployeeTrainingItemDto> trainingItems = employeeTrainings.stream()
                .map(et -> {
                    Training training = et.getTraining();
                    return new EmployeeTrainingItemDto(
                            training.getId(),
                            training.getTrainingName(),
                            training.getCategory() != null ? training.getCategory().name() : null,
                            training.getDurationHours(),
                            et.getCompletionStatus() != null ? et.getCompletionStatus().name() : null
                    );
                })
                .collect(Collectors.toList());

        // 교육 이력을 포함한 DTO 재생성
        EmployeeDetailDto result = new EmployeeDetailDto(
                employeeDetail.getEmployeeId(),
                employeeDetail.getEmployeeNumber(),
                employeeDetail.getName(),
                employeeDetail.getEmail(),
                employeeDetail.getPhone(),
                employeeDetail.getPosition(),
                employeeDetail.getDepartment(),
                employeeDetail.getStatusCode(),
                employeeDetail.getHireDate(),
                employeeDetail.getBirthDate(),
                employeeDetail.getAddress(),
                employeeDetail.getCreatedAt(),
                employeeDetail.getUpdatedAt(),
                trainingItems
        );

        log.info("직원 상세 정보 조회 성공 - employeeId: {}, employeeName: {}, trainingCount: {}",
                employeeId, result.getName(), trainingItems.size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeListItemDto> getEmployeeList(EmployeeListSearchConditionVo condition, Pageable pageable) {
        log.info("직원 목록 조회 요청 - departmentId: {}, positionId: {}, name: {}",
                condition.getDepartmentId(), condition.getPositionId(), condition.getName());

        Page<EmployeeListItemDto> result = employeeDAO.findEmployeeList(condition, pageable);

        log.info("직원 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional
    public void updateEmployee(String employeeId, UpdateEmployeeRequestDto requestDto) {
        log.info("직원 정보 수정 요청 - employeeId: {}", employeeId);

        // 1. Employee 조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        InternelUser internelUser = employee.getInternelUser();

        // 2. Position 조회 (positionId가 제공된 경우)
        Position position = null;
        if (requestDto.getPositionId() != null) {
            position = positionRepository.findById(requestDto.getPositionId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "직급 정보를 찾을 수 없습니다."));
        }

        // 3. InternelUser 정보 업데이트
        internelUser.updateEmployeeInfo(
                requestDto.getEmployeeName(),
                position
        );

        // 4. 저장 (Dirty Checking으로 자동 저장)
        log.info("직원 정보 수정 성공 - employeeId: {}, employeeName: {}",
                employeeId, requestDto.getEmployeeName());
    }

    @Override
    @Transactional
    public void requestTraining(TrainingRequestDto requestDto) {
        log.info("교육 프로그램 신청 요청 - employeeId: {}, programId: {}",
                requestDto.getEmployeeId(), requestDto.getProgramId());

        // 1. Employee 조회
        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. Training (Program) 조회
        Training training = trainingRepository.findById(requestDto.getProgramId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "교육 프로그램 정보를 찾을 수 없습니다."));

        // 3. EmployeeTraining 생성 및 저장
        EmployeeTraining employeeTraining = new EmployeeTraining(
                employee,
                training,
                org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus.IN_PROGRESS  // completionStatus: 신청 시점에는 진행중
        );

        employeeTrainingRepository.save(employeeTraining);

        log.info("교육 프로그램 신청 성공 - employeeId: {}, trainingId: {}, trainingName: {}",
                requestDto.getEmployeeId(), training.getId(), training.getTrainingName());
    }

    @Override
    @Transactional
    public void InternelUserrequestTraining(String internelUserId, String programId) {
        log.info("교육 프로그램 신청 요청 - internelUserId: {}, programId: {}",
                internelUserId, programId);

        // InternelUser 조회
        InternelUser internelUser = internalUserRepository.findByUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 1. Employee 조회
        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. Training (Program) 조회
        Training training = trainingRepository.findById(programId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "교육 프로그램 정보를 찾을 수 없습니다."));

        // 3. EmployeeTraining 생성 및 저장
        EmployeeTraining employeeTraining = new EmployeeTraining(
                employee,
                training,
                org.ever._4ever_be_business.hr.enums.TrainingCompletionStatus.IN_PROGRESS  // completionStatus: 신청 시점에는 진행중
        );

        employeeTrainingRepository.save(employeeTraining);

        log.info("교육 프로그램 신청 성공 - internelUserId: {}, trainingId: {}, trainingName: {}",
                internelUserId, training.getId(), training.getTrainingName());
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeWithTrainingDto getEmployeeWithTrainingByInternelUserId(String internelUserId) {
        log.info("InternelUser ID로 직원 정보 및 교육 이력 조회 요청 - internelUserId: {}", internelUserId);

        // 1. InternelUser ID로 Employee 조회
        Employee employee = employeeRepository.findByInternelUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. Employee 상세 정보 조회 (기존 메서드 재사용)
        EmployeeDetailDto employeeDetail = employeeDAO.findEmployeeDetailById(employee.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 상세 정보를 찾을 수 없습니다."));

        // 3. EmployeeTraining 목록 조회
        List<EmployeeTraining> employeeTrainings = employeeTrainingRepository.findByEmployeeId(employee.getId());

        // 4. EmployeeTrainingItemDto 리스트 생성
        List<EmployeeTrainingItemDto> trainingItems = employeeTrainings.stream()
                .map(et -> {
                    Training training = et.getTraining();
                    return new EmployeeTrainingItemDto(
                            training.getId(),
                            training.getTrainingName(),
                            training.getCategory() != null ? training.getCategory().name() : null,
                            training.getDurationHours(),
                            et.getCompletionStatus() != null ? et.getCompletionStatus().name() : null
                    );
                })
                .collect(Collectors.toList());

        // 5. EmployeeWithTrainingDto 생성 및 반환
        EmployeeWithTrainingDto result = new EmployeeWithTrainingDto(
                employeeDetail.getEmployeeId(),
                employeeDetail.getEmployeeNumber(),
                employeeDetail.getName(),
                employeeDetail.getEmail(),
                employeeDetail.getPhone(),
                employeeDetail.getPosition(),
                employeeDetail.getDepartment(),
                employeeDetail.getStatusCode(),
                employeeDetail.getHireDate(),
                employeeDetail.getBirthDate(),
                employeeDetail.getAddress(),
                employeeDetail.getCreatedAt(),
                employeeDetail.getUpdatedAt(),
                trainingItems
        );

        log.info("InternelUser ID로 직원 정보 및 교육 이력 조회 성공 - internelUserId: {}, employeeId: {}, trainingCount: {}",
                internelUserId, employee.getId(), trainingItems.size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingProgramSimpleDto> getAvailableTrainingsByInternelUserId(String internelUserId) {
        log.info("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 요청 - internelUserId: {}", internelUserId);

        // 1. InternelUser ID로 Employee 조회
        Employee employee = employeeRepository.findByInternelUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. 해당 직원이 수강 중인 Training ID 목록 조회
        List<EmployeeTraining> employeeTrainings = employeeTrainingRepository.findByEmployeeId(employee.getId());
        List<String> enrolledTrainingIds = employeeTrainings.stream()
                .map(et -> et.getTraining().getId())
                .collect(Collectors.toList());

        log.debug("수강 중인 교육 프로그램 수: {}", enrolledTrainingIds.size());

        // 3. 전체 Training 중에서 수강 중이지 않고, RECRUITING 상태가 아닌 것만 조회
        List<TrainingProgramSimpleDto> availableTrainings = trainingRepository.findAll().stream()
                .filter(training -> !enrolledTrainingIds.contains(training.getId())) // 수강 중이지 않은 것
                .filter(training -> training.getTrainingStatus() != org.ever._4ever_be_business.hr.enums.TrainingStatus.RECRUITING) // RECRUITING 제외
                .map(training -> new TrainingProgramSimpleDto(training.getId(), training.getTrainingName()))
                .collect(Collectors.toList());

        log.info("InternelUser ID로 수강 가능한 교육 프로그램 목록 조회 성공 - internelUserId: {}, availableCount: {}",
                internelUserId, availableTrainings.size());

        return availableTrainings;
    }

    @Override
    public void createEmployee(
            EmployeeCreateRequestDto requestDto,
            DeferredResult<ResponseEntity<ApiResponse<CreateAuthUserResultEvent>>> deferredResult
    ) {
        // 트랜잭션 id 생성
        String transactionId = UuidV7Generator.generate();

        asyncResultManager.registerResult(transactionId, deferredResult);
        log.info("[INFO] 내부 사용자 등록 시작 - name: {}, email: {}", requestDto.getName(), requestDto.getEmail());

        sagaManager.executeSagaWithId(transactionId, () -> {
            try {
                // 1. Department 먼저 조회
                Department requestedDepartment = departmentRepository.findById(requestDto.getDepartmentId())
                        .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "[ERROR] 부서 정보를 찾을 수 없습니다."));

                // 2. 해당 Department에 속한 Position 목록 조회
                List<Position> departmentPositions = positionRepository.findByDepartmentId(requestDto.getDepartmentId());

                // 3. 목록에서 positionId에 해당하는 Position 찾기 (해당 부서에 속한 직급인지 검증)
                Position position = departmentPositions.stream()
                        .filter(p -> p.getId().equals(requestDto.getPositionId()))
                        .findFirst()
                        .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "[ERROR] 해당 부서에 속한 직급이 아닙니다. departmentId: " + requestDto.getDepartmentId() + ", positionId: " + requestDto.getPositionId()));

                String userId = UuidV7Generator.generate();
                String employeeCode = "EMP-" + generateNumberByUuidLast7(userId);
                String address = requestDto.getBaseAddress() + " " + requestDto.getDetailAddress();

                InternelUser internalUser = InternelUser.createNewInternalUser(
                    userId,
                    requestDto.getName(),
                    employeeCode,
                    position,
                    requestDto.getBirthDate(),
                    requestDto.getHireDate(),
                    address,
                    requestDto.getEmail(),
                    requestDto.getPhoneNumber(),
                    UserStatus.ACTIVE
                );
                InternelUser savedInternalUser = internalUserRepository.save(internalUser);

                Employee employee = new Employee(savedInternalUser.getId(), savedInternalUser, 15L, null);
                employeeRepository.save(employee);

                CreateAuthUserEvent event = CreateAuthUserEvent.builder()
                    .eventId(UuidV7Generator.generate())
                    .transactionId(transactionId)
                    .success(true)      // 여기는 성공한 경우임.
                    .userId(userId)
                    .email(requestDto.getEmail())
                    .departmentCode(requestedDepartment.getDepartmentCode())
                    .positionCode(position.getPositionCode())
                    .build();

                userServicePort.createAuthUserPort(event)
                    .exceptionally(error -> {
                        asyncResultManager.setErrorResult(
                            transactionId,
                            "[SAGA][FAIL] 사용자 계정 생성 요청 발행 실패: " + error.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                        );
                        return null;
                    });
                return null;
            } catch (Exception error) {
                asyncResultManager.setErrorResult(
                    transactionId,
                    "[SAGA][FAIL] 내부 사용자 생성 처리에 실패했습니다.: " + error.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
                );
                throw error;
            }
        });
    }

    private String generateNumberByUuidLast7(String uuidId) {
        if (uuidId == null || uuidId.isEmpty()) {
            throw new IllegalArgumentException("[ERROR] UUID가 null 이거나 비어있습니다.");
        }
        String compactUuid = uuidId.replaceAll("-", "");
        log.info("[INFO] Number로 사용될 생성된 uuid: {}", compactUuid);
        if (compactUuid.length() < 7) {
            throw new IllegalArgumentException("패딩된 uuid가 7자리 이하 이므로 Number를 생성할 수 없습니다. uuidId를 점검해주세요");
        }
        return compactUuid.substring(compactUuid.length() - 7);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeProfileDto getProfileByInternelUserId(String internelUserId) {
        log.info("프로필 조회 요청 - internelUserId: {}", internelUserId);

        // InternelUser 조회
        InternelUser internelUser = internalUserRepository.findByUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Employee 조회
        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Position 정보
        Position position = internelUser.getPosition();
        String departmentName = position != null && position.getDepartment() != null
                ? position.getDepartment().getDepartmentName() : "";
        String positionName = position != null ? position.getPositionName() : "";

        // 근속 기간 계산
        LocalDateTime hireDate = internelUser.getHireDate();
        String hireDateStr = hireDate != null ? hireDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
        String serviceYears = calculateServiceYears(hireDate);

        return new EmployeeProfileDto(
                internelUser.getName(),
                internelUser.getEmployeeCode(),
                departmentName,
                positionName,
                hireDateStr,
                serviceYears,
                internelUser.getEmail(),
                internelUser.getPhoneNumber(),
                internelUser.getAddress()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeAttendanceRecordDto> getAttendanceRecordsByInternelUserId(String internelUserId) {
        log.info("근태 기록 조회 요청 (오늘 제외) - internelUserId: {}", internelUserId);

        // InternelUser 조회
        InternelUser internelUser = internalUserRepository.findByUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Employee 조회
        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 오늘 날짜
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();

        // Attendance 조회 (오늘 제외, 최신순)
        List<Attendance> attendances = attendanceRepository.findAll().stream()
                .filter(a -> a.getEmployee().getId().equals(employee.getId()))
                .filter(a -> a.getWorkDate() != null && a.getWorkDate().toLocalDate().isBefore(today.toLocalDate()))
                .sorted((a, b) -> b.getWorkDate().compareTo(a.getWorkDate()))
                .collect(Collectors.toList());

        return attendances.stream()
                .map(attendance -> {
                    String date = attendance.getWorkDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    String status = attendance.getStatus() != null ? attendance.getStatus().name() : "";
                    String startTime = attendance.getCheckIn() != null
                            ? attendance.getCheckIn().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
                    String endTime = attendance.getCheckOut() != null
                            ? attendance.getCheckOut().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
                    String workHours = formatWorkHours(attendance.getWorkMinutes());

                    return new EmployeeAttendanceRecordDto(date, status, startTime, endTime, workHours);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TodayAttendanceDto getTodayAttendanceByInternelUserId(String internelUserId) {
        log.info("오늘 근태 기록 조회 요청 - internelUserId: {}", internelUserId);

        // InternelUser 조회
        InternelUser internelUser = internalUserRepository.findByUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Employee 조회
        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 오늘 날짜
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);

        // 오늘 Attendance 조회 (checkIn 기준으로 필터링)
        Attendance todayAttendance = attendanceRepository.findAll().stream()
                .filter(a -> a.getEmployee().getId().equals(employee.getId()))
                .filter(a -> a.getCheckIn() != null
                        && !a.getCheckIn().isBefore(todayStart)
                        && a.getCheckIn().isBefore(todayEnd))
                .findFirst()
                .orElse(null);

        if (todayAttendance == null) {
            // 오늘 출근 기록이 없음
            return new TodayAttendanceDto(null, null, null, "출근전");
        }

        String checkInTime = todayAttendance.getCheckIn() != null
                ? todayAttendance.getCheckIn().format(DateTimeFormatter.ofPattern("HH:mm")) : null;
        String checkOutTime = todayAttendance.getCheckOut() != null
                ? todayAttendance.getCheckOut().format(DateTimeFormatter.ofPattern("HH:mm")) : null;
        String workHours = formatWorkHours(todayAttendance.getWorkMinutes());

        // 상태 결정
        String status;
        if (checkInTime != null && checkOutTime == null) {
            status = "근무중";
        } else if (checkInTime != null && checkOutTime != null) {
            status = "퇴근완료";
        } else {
            status = "출근전";
        }

        return new TodayAttendanceDto(checkInTime, checkOutTime, workHours, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingItemDto> getInProgressTrainingsByInternelUserId(String internelUserId) {
        log.info("수강중인 교육 목록 조회 요청 - internelUserId: {}", internelUserId);

        // InternelUser 조회
        InternelUser internelUser = internalUserRepository.findByUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Employee 조회
        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // EmployeeTraining에서 IN_PROGRESS인 것들 조회
        List<EmployeeTraining> inProgressTrainings = employeeTrainingRepository.findAll().stream()
                .filter(et -> et.getEmployee().getId().equals(employee.getId()))
                .filter(et -> et.getCompletionStatus() == TrainingCompletionStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        return inProgressTrainings.stream()
                .map(et -> {
                    Training training = et.getTraining();
                    return new TrainingItemDto(
                            training.getId(),
                            training.getTrainingName(),
                            training.getTrainingStatus() != null ? training.getTrainingStatus().name() : "",
                            training.getDurationHours(),
                            training.getDeliveryMethod() != null ? training.getDeliveryMethod() : "",
                            et.getCompletionStatus().name(),
                            training.getCategory() != null ? training.getCategory().name() : "",
                            training.getDescription() != null ? training.getDescription() : "",
                            null  // 수강중인 교육은 수료일 없음
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingItemDto> getAvailableTrainingsForApplyByInternelUserId(String internelUserId) {
        log.info("신청가능한 교육 목록 조회 요청 - internelUserId: {}", internelUserId);

        // InternelUser 조회
        InternelUser internelUser = internalUserRepository.findByUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Employee 조회
        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이미 신청한 교육 ID 목록
        List<String> appliedTrainingIds = employeeTrainingRepository.findAll().stream()
                .filter(et -> et.getEmployee().getId().equals(employee.getId()))
                .map(et -> et.getTraining().getId())
                .collect(Collectors.toList());

        // Training 중 신청하지 않은 것들 (IN_PROGRESS 상태만)
        List<Training> availableTrainings = trainingRepository.findAll().stream()
                .filter(training -> !appliedTrainingIds.contains(training.getId()))
                .filter(training -> training.getTrainingStatus() == TrainingStatus.IN_PROGRESS)
                .collect(Collectors.toList());

        return availableTrainings.stream()
                .map(training -> new TrainingItemDto(
                        training.getId(),
                        training.getTrainingName(),
                        training.getTrainingStatus() != null ? training.getTrainingStatus().name() : "",
                        training.getDurationHours(),
                        training.getDeliveryMethod() != null ? training.getDeliveryMethod() : "",
                        null,  // 신청가능한 교육이므로 completionStatus는 null
                        training.getCategory() != null ? training.getCategory().name() : "",
                        training.getDescription() != null ? training.getDescription() : "",
                        null  // 신청가능한 교육은 수료일 없음
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingItemDto> getCompletedTrainingsByInternelUserId(String internelUserId) {
        log.info("수료한 교육 목록 조회 요청 - internelUserId: {}", internelUserId);

        // InternelUser 조회
        InternelUser internelUser = internalUserRepository.findByUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // Employee 조회
        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // EmployeeTraining에서 COMPLETED인 것들 조회
        List<EmployeeTraining> completedTrainings = employeeTrainingRepository.findAll().stream()
                .filter(et -> et.getEmployee().getId().equals(employee.getId()))
                .filter(et -> et.getCompletionStatus() == TrainingCompletionStatus.COMPLETED)
                .collect(Collectors.toList());

        return completedTrainings.stream()
                .map(et -> {
                    Training training = et.getTraining();
                    // 수료일: employeeTraining의 updatedAt을 yyyy-MM-dd 형식으로 변환
                    String complementationDate = et.getUpdatedAt() != null
                            ? et.getUpdatedAt().toLocalDate().toString()
                            : null;

                    return new TrainingItemDto(
                            training.getId(),
                            training.getTrainingName(),
                            training.getTrainingStatus() != null ? training.getTrainingStatus().name() : "",
                            training.getDurationHours(),
                            training.getDeliveryMethod() != null ? training.getDeliveryMethod(): "",
                            et.getCompletionStatus().name(),
                            training.getCategory() != null ? training.getCategory().name() : "",
                            training.getDescription() != null ? training.getDescription() : "",
                            complementationDate
                    );
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateProfileByInternelUserId(String internelUserId, org.ever._4ever_be_business.hr.dto.request.UpdateProfileRequestDto requestDto) {
        log.info("프로필 수정 요청 - internelUserId: {}", internelUserId);

        // InternelUser 조회
        InternelUser internelUser = internalUserRepository.findByUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 프로필 수정 (전화번호, 주소)
        internelUser.updateProfile(requestDto.getPhoneNumber(), requestDto.getAddress());

        // 저장 (변경 감지로 자동 저장)
        log.info("프로필 수정 완료 - internelUserId: {}", internelUserId);
    }

    /**
     * 근속 기간 계산 (1년 2개월 10일 형식)
     */
    private String calculateServiceYears(LocalDateTime hireDate) {
        if (hireDate == null) {
            return "0일";
        }

        LocalDateTime now = LocalDateTime.now();
        long totalDays = java.time.Duration.between(hireDate, now).toDays();

        long years = totalDays / 365;
        long months = (totalDays % 365) / 30;
        long days = (totalDays % 365) % 30;

        StringBuilder result = new StringBuilder();
        if (years > 0) {
            result.append(years).append("년 ");
        }
        if (months > 0) {
            result.append(months).append("개월");
            if (days > 0) {
                result.append(" ");
            }
        }
        if (days > 0 || result.length() == 0) {
            result.append(days).append("일");
        }

        return result.toString().trim();
    }

    /**
     * 근무 시간 포맷팅 (8시간 30분 형식)
     */
    private String formatWorkHours(Long workMinutes) {
        if (workMinutes == null || workMinutes == 0) {
            return "0시간 0분";
        }

        long hours = workMinutes / 60;
        long minutes = workMinutes % 60;

        return hours + "시간 " + minutes + "분";
    }

    @Override
    @Transactional(readOnly = true)
    public org.ever._4ever_be_business.hr.dto.response.CustomerInfoDto getCustomerInfoByUserId(String customerUserId) {
        log.info("고객 정보 조회 요청 - customerUserId: {}", customerUserId);

        // CustomerUser 조회
        CustomerUser customerUser = customerUserRepository.findByUserId(customerUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // CustomerCompany 조회
        org.ever._4ever_be_business.company.entity.CustomerCompany customerCompany = customerUser.getCustomerCompany();
        if (customerCompany == null) {
            throw new BusinessException(ErrorCode.CUSTOMER_COMPANY_NOT_FOUND);
        }

        log.info("고객 정보 조회 성공 - customerUserId: {}, companyName: {}", customerUserId, customerCompany.getCompanyName());

        return new org.ever._4ever_be_business.hr.dto.response.CustomerInfoDto(
                customerCompany.getCompanyName(),
                customerCompany.getBaseAddress(),
                customerCompany.getDetailAddress(),
                customerCompany.getOfficePhone(),
                customerCompany.getBusinessNumber(),
                customerUser.getCustomerName(),
                customerUser.getPhoneNumber(),
                customerUser.getEmail()
        );
    }
}
