package org.ever._4ever_be_business.tam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.ever._4ever_be_business.tam.dao.AttendanceDAO;
import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceRecordDto;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.enums.AttendanceStatus;
import org.ever._4ever_be_business.tam.repository.AttendanceRepository;
import org.ever._4ever_be_business.tam.service.AttendanceService;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private static final int DEFAULT_DASHBOARD_SIZE = 5;
    private static final DateTimeFormatter DASHBOARD_DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final AttendanceDAO attendanceDAO;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DashboardWorkflowItemDto> getDashboardAttendanceList(String userId, int size) {
        int limit = size > 0 ? size : DEFAULT_DASHBOARD_SIZE;
        Pageable pageable = PageRequest.of(0, limit);

        if (userId != null && !userId.isBlank()) {
            log.info("[HRM][Dashboard][ATT] userId={} supplied but 전체 데이터를 반환합니다.", userId);
        }

        Page<Attendance> attendancePage = attendanceRepository.findAllByOrderByWorkDateDesc(pageable);

        List<DashboardWorkflowItemDto> items = attendancePage.getContent().stream()
                .map(this::toDashboardWorkflowItem)
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            log.info("[DASHBOARD][MOCK][HRM][ATT] 실데이터 없음 - 근태 목업 데이터 반환");
            return buildMockAttendanceItems(limit);
        }

        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceListItemDto> getAttendanceList(AttendanceListSearchConditionVo condition, Pageable pageable) {
        log.info("출퇴근 기록 조회 요청 - employeeId: {}, startDate: {}, endDate: {}, status: {}",
                condition.getEmployeeId(), condition.getStartDate(), condition.getEndDate(), condition.getStatus());

        // Entity 조회
        Page<Attendance> entityPage = attendanceDAO.findAttendanceEntities(condition, pageable);

        // DTO로 변환 (실시간 계산 적용)
        List<AttendanceListItemDto> dtoList = entityPage.getContent().stream()
                .map(this::convertToAttendanceListItemDto)
                .collect(Collectors.toList());

        Page<AttendanceListItemDto> result = new PageImpl<>(dtoList, pageable, entityPage.getTotalElements());

        log.info("출퇴근 기록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    /**
     * Attendance Entity를 AttendanceListItemDto로 변환 (실시간 계산 적용)
     */
    private AttendanceListItemDto convertToAttendanceListItemDto(Attendance attendance) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // 근무 시간 계산 (실시간)
        Integer workingHours = 0;
        if (attendance.getCheckIn() != null) {
            LocalDateTime endTime = attendance.getCheckOut() != null ? attendance.getCheckOut() : now;
            long totalMinutes = Duration.between(attendance.getCheckIn(), endTime).toMinutes();
            workingHours = (int) (totalMinutes / 60);
        }

        // 초과 근무 시간 계산 (18시 이후, 실시간)
        Integer overtimeHours = 0;
        if (attendance.getCheckIn() != null) {
            LocalDate workDateLocal = attendance.getCheckIn().toLocalDate();
            LocalDateTime endOfWorkDay = LocalDateTime.of(workDateLocal, LocalTime.of(18, 0));
            LocalDateTime endTime = attendance.getCheckOut() != null ? attendance.getCheckOut() : now;

            if (endTime.isAfter(endOfWorkDay)) {
                long overtimeMinutes = Duration.between(endOfWorkDay, endTime).toMinutes();
                overtimeHours = (int) (overtimeMinutes / 60);
            }
        }

        return new AttendanceListItemDto(
                attendance.getId(),
                attendance.getEmployee().getId(),
                attendance.getEmployee().getInternelUser().getName(),
                attendance.getEmployee().getInternelUser().getEmployeeCode(),
                attendance.getWorkDate() != null ? attendance.getWorkDate().format(dateFormatter) : null,
                attendance.getCheckIn() != null ? attendance.getCheckIn().format(timeFormatter) : null,
                attendance.getCheckOut() != null ? attendance.getCheckOut().format(timeFormatter) : null,
                attendance.getStatus() != null ? attendance.getStatus().name() : "NORMAL",
                "OFFICE",
                "본사",
                "",
                workingHours,
                overtimeHours,
                "APPROVED",
                "",
                "",
                attendance.getCreatedAt() != null ? attendance.getCreatedAt().format(dateFormatter) : null,
                attendance.getUpdatedAt() != null ? attendance.getUpdatedAt().format(dateFormatter) : null,
                "개발팀",
                attendance.getEmployee().getInternelUser().getPosition().getPositionName(),
                attendance.getStatus() == AttendanceStatus.LATE,
                false  // earlyLeave는 더 이상 사용하지 않음
        );
    }

    @Override
    @Transactional
    public void checkIn(String employeeId) {
        log.info("출근 처리 요청 - employeeId: {}", employeeId);

        // 1. Employee 조회
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR));

        // 2. 오늘 날짜의 출퇴근 기록 조회
        Optional<Attendance> existingAttendance = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId);

        Attendance attendance;
        if (existingAttendance.isPresent()) {
            // 이미 오늘 기록이 있으면 출근 시간 업데이트
            attendance = existingAttendance.get();
            attendance.checkIn();
            log.info("기존 출퇴근 기록 출근 시간 업데이트 - attendanceId: {}", attendance.getId());
        } else {
            // 오늘 기록이 없으면 새로 생성
            attendance = Attendance.createForCheckIn(employee);
            log.info("새로운 출퇴근 기록 생성");
        }

        // 3. 저장
        attendanceRepository.save(attendance);

        log.info("출근 처리 성공 - employeeId: {}, attendanceId: {}", employeeId, attendance.getId());
    }

    @Override
    @Transactional
    public void checkOut(String employeeId) {
        log.info("퇴근 처리 요청 - employeeId: {}", employeeId);

        // 1. 오늘 날짜의 출퇴근 기록 조회
        Attendance attendance = attendanceRepository.findTodayAttendanceByEmployeeId(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR));

        // 2. 퇴근 처리
        attendance.checkOut();

        // 3. 저장
        attendanceRepository.save(attendance);

        log.info("퇴근 처리 성공 - employeeId: {}, attendanceId: {}, workMinutes: {}, overtimeMinutes: {}",
                employeeId, attendance.getId(), attendance.getWorkMinutes(), attendance.getOvertimeMinutes());
    }

    @Override
    @Transactional
    public void checkInByInternelUserId(String internelUserId) {
        log.info("InternelUser ID로 출근 처리 요청 - internelUserId: {}", internelUserId);

        // 1. InternelUser ID로 Employee 조회
        Employee employee = employeeRepository.findByInternelUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. Employee ID로 checkIn 처리
        checkIn(employee.getId());

        log.info("InternelUser ID로 출근 처리 성공 - internelUserId: {}, employeeId: {}", internelUserId, employee.getId());
    }

    @Override
    @Transactional
    public void checkOutByInternelUserId(String internelUserId) {
        log.info("InternelUser ID로 퇴근 처리 요청 - internelUserId: {}", internelUserId);

        // 1. InternelUser ID로 Employee 조회
        Employee employee = employeeRepository.findByInternelUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. Employee ID로 checkOut 처리
        checkOut(employee.getId());

        log.info("InternelUser ID로 퇴근 처리 성공 - internelUserId: {}, employeeId: {}", internelUserId, employee.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecordDto> getAttendanceRecordsByInternelUserId(String internelUserId) {
        log.info("InternelUser ID로 출퇴근 기록 목록 조회 요청 - internelUserId: {}", internelUserId);

        // 1. InternelUser ID로 Employee 조회
        Employee employee = employeeRepository.findByInternelUserId(internelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. Employee의 모든 Attendance 조회
        List<Attendance> attendances = attendanceRepository.findAllByEmployeeIdOrderByWorkDateDesc(employee.getId());

        // 3. AttendanceRecordDto로 변환
        List<AttendanceRecordDto> result = attendances.stream()
                .map(this::convertToAttendanceRecordDto)
                .collect(Collectors.toList());

        log.info("InternelUser ID로 출퇴근 기록 목록 조회 성공 - internelUserId: {}, recordCount: {}", internelUserId, result.size());

        return result;
    }

    private DashboardWorkflowItemDto toDashboardWorkflowItem(Attendance attendance) {
        Employee employee = attendance.getEmployee();
        InternelUser internalUser = employee != null ? employee.getInternelUser() : null;

        String employeeName = internalUser != null ? internalUser.getName() : "미상";
        String employeeCode = internalUser != null ? internalUser.getEmployeeCode() : null;
        String statusLabel = mapStatusLabel(attendance.getStatus());
        String itemTitle = employeeName + " · " + statusLabel;

        return DashboardWorkflowItemDto.builder()
                .itemId(attendance.getId())
                .itemTitle(itemTitle)
                .itemNumber(employeeCode)
                .name(employeeName)
                .statusCode(attendance.getStatus() != null ? attendance.getStatus().name() : "UNKNOWN")
                .date(formatDashboardDate(attendance.getCheckIn(), attendance.getWorkDate()))
                .build();
    }

    private List<DashboardWorkflowItemDto> buildMockAttendanceItems(int size) {
        int itemCount = Math.min(size > 0 ? size : DEFAULT_DASHBOARD_SIZE, DEFAULT_DASHBOARD_SIZE);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> DashboardWorkflowItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemTitle("근태 기록 " + (i + 1))
                        .itemNumber(String.format("ATT-MOCK-%04d", i + 1))
                        .name("사원" + (i + 1))
                        .statusCode(i % 2 == 0 ? AttendanceStatus.NORMAL.name() : AttendanceStatus.LATE.name())
                        .date(LocalDate.now().minusDays(i).toString())
                        .build())
                .collect(Collectors.toList());
    }

    private String mapStatusLabel(AttendanceStatus status) {
        if (status == AttendanceStatus.LATE) {
            return "지각";
        }
        return "정상 출근";
    }

    private String formatDashboardDate(LocalDateTime checkIn, LocalDateTime workDate) {
        LocalDateTime target = checkIn != null ? checkIn : workDate;
        return target != null ? target.format(DASHBOARD_DATE_FORMATTER) : null;
    }

    private AttendanceRecordDto convertToAttendanceRecordDto(Attendance attendance) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        // workDate
        String workDate = attendance.getWorkDate() != null
                ? attendance.getWorkDate().format(dateFormatter)
                : null;

        // checkInTime
        String checkInTime = attendance.getCheckIn() != null
                ? attendance.getCheckIn().format(dateTimeFormatter)
                : null;

        // checkOutTime
        String checkOutTime = attendance.getCheckOut() != null
                ? attendance.getCheckOut().format(dateTimeFormatter)
                : null;

        // totalWorkMinutes 계산
        Long totalWorkMinutes = 0L;
        if (attendance.getCheckIn() != null) {
            LocalDateTime endTime = attendance.getCheckOut() != null ? attendance.getCheckOut() : now;
            totalWorkMinutes = Duration.between(attendance.getCheckIn(), endTime).toMinutes();
        }

        // overtimeMinutes 계산 (18시 이후)
        Long overtimeMinutes = 0L;
        if (attendance.getCheckIn() != null) {
            LocalDate workDateLocal = attendance.getCheckIn().toLocalDate();
            LocalDateTime endOfWorkDay = LocalDateTime.of(workDateLocal, LocalTime.of(18, 0)); // 18:00

            LocalDateTime endTime = attendance.getCheckOut() != null ? attendance.getCheckOut() : now;

            if (endTime.isAfter(endOfWorkDay)) {
                overtimeMinutes = Duration.between(endOfWorkDay, endTime).toMinutes();
            }
        }

        // statusCode 필터링 (NORMAL, LATE만)
        String statusCode = "NORMAL";
        if (attendance.getStatus() != null) {
            AttendanceStatus status = attendance.getStatus();
            if (status == AttendanceStatus.NORMAL || status == AttendanceStatus.LATE) {
                statusCode = status.name();
            }
        }

        return new AttendanceRecordDto(
                workDate,
                checkInTime,
                checkOutTime,
                totalWorkMinutes,
                overtimeMinutes,
                statusCode
        );
    }
}
