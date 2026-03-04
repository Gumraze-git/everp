package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dto.request.CreateLeaveRequestDto;
import org.ever._4ever_be_business.hr.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.hr.dto.response.RemainingLeaveDaysDto;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.LeaveRequest;
import org.ever._4ever_be_business.hr.enums.LeaveRequestStatus;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.InternelUserRepository;
import org.ever._4ever_be_business.hr.repository.LeaveRequestRepository;
import org.ever._4ever_be_business.hr.service.LeaveRequestService;
import org.ever._4ever_be_business.hr.vo.LeaveRequestSearchConditionVo;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private static final int DEFAULT_DASHBOARD_SIZE = 5;
    private static final DateTimeFormatter DASHBOARD_DATE_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .appendFraction(ChronoField.MILLI_OF_SECOND, 0, 3, true)
                    .toFormatter();

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final InternelUserRepository internelUserRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DashboardWorkflowItemDto> getDashboardLeaveRequestList(String userId, int size) {
        int limit = size > 0 ? size : DEFAULT_DASHBOARD_SIZE;
        Pageable pageable = PageRequest.of(0, limit);

        if (userId != null && !userId.isBlank()) {
            log.info("[HRM][Dashboard][LV] userId={} supplied but 전체 데이터를 반환합니다.", userId);
        }

        Page<LeaveRequest> leaveRequests = leaveRequestRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<DashboardWorkflowItemDto> items = leaveRequests.getContent().stream()
                .map(this::toDashboardItem)
                .collect(Collectors.toList());

        if (items.isEmpty()) {
            log.info("[DASHBOARD][MOCK][HRM][LV] 실데이터 없음 - 휴가 신청 목업 데이터 반환");
            return buildMockLeaveRequests(limit);
        }

        return items;
    }

    private DashboardWorkflowItemDto toDashboardItem(LeaveRequest leaveRequest) {
        Employee employee = leaveRequest.getEmployee();
        InternelUser internelUser = employee != null ? employee.getInternelUser() : null;

        String employeeName = internelUser != null ? internelUser.getName() : "미상";
        String employeeCode = internelUser != null ? internelUser.getEmployeeCode() : null;
        String leaveType = leaveRequest.getLeaveType() != null ? leaveRequest.getLeaveType().name() : "UNKNOWN";
        String title = employeeName + " · " + mapLeaveTypeLabel(leaveType);

        return DashboardWorkflowItemDto.builder()
                .itemId(leaveRequest.getId())
                .itemTitle(title)
                .itemNumber(employeeCode)
                .name(employeeName)
                .statusCode(
                        leaveRequest.getStatus() != null
                                ? leaveRequest.getStatus().name()
                                : LeaveRequestStatus.PENDING.name()
                )
                .date(formatDashboardDate(leaveRequest.getStartDate()))
                .build();
    }

    private List<DashboardWorkflowItemDto> buildMockLeaveRequests(int size) {
        int itemCount = Math.min(size > 0 ? size : DEFAULT_DASHBOARD_SIZE, DEFAULT_DASHBOARD_SIZE);

        return IntStream.range(0, itemCount)
                .mapToObj(i -> DashboardWorkflowItemDto.builder()
                        .itemId(UUID.randomUUID().toString())
                        .itemTitle("휴가 신청 " + (i + 1))
                        .itemNumber(String.format("LV-MOCK-%04d", i + 1))
                        .name("사원" + (i + 1))
                        .statusCode(i % 2 == 0 ? LeaveRequestStatus.PENDING.name() : LeaveRequestStatus.APPROVED.name())
                        .date(LocalDate.now().minusDays(i).toString())
                        .build())
                .collect(Collectors.toList());
    }

    private String mapLeaveTypeLabel(String leaveType) {
        return switch (leaveType) {
            case "ANNUAL" -> "연차";
            case "SICK" -> "병가";
            default -> leaveType;
        };
    }

    private String formatDashboardDate(LocalDateTime startDate) {
        return startDate != null ? startDate.format(DASHBOARD_DATE_FORMATTER) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveRequestListItemDto> getLeaveRequestList(LeaveRequestSearchConditionVo condition, Pageable pageable) {
        log.info("휴가 신청 목록 조회 요청 - departmentId: {}, positionId: {}, name: {}, type: {}, sortOrder: {}",
                condition.getDepartmentId(), condition.getPositionId(), condition.getName(),
                condition.getType(), condition.getSortOrder());

        // 1. Repository에서 기본 데이터 조회 (remainingLeaveDays는 임시 값)
        Page<LeaveRequestListItemDto> rawResult = leaveRequestRepository.findLeaveRequestList(condition, pageable);

        // 2. 각 직원별로 실제 remainingLeaveDays 계산
        List<LeaveRequestListItemDto> updatedContent = rawResult.getContent().stream()
                .map(item -> {
                    String employeeId = item.getEmployee().getEmployeeId();

                    // 승인된 휴가 일수 합계 조회
                    Integer approvedDays = leaveRequestRepository.sumApprovedLeaveDaysByEmployeeId(
                            employeeId, LeaveRequestStatus.APPROVED);

                    // 남은 연차 = 18 - 승인된 휴가 일수
                    int remainingLeaveDays = 18 - (approvedDays != null ? approvedDays : 0);

                    // 새로운 DTO 생성 (remainingLeaveDays만 업데이트)
                    return new LeaveRequestListItemDto(
                            item.getLeaveRequestId(),
                            item.getEmployee(),
                            item.getLeaveType(),
                            item.getStartDate(),
                            item.getEndDate(),
                            item.getNumberOfLeaveDays(),
                            remainingLeaveDays,  // 계산된 값으로 대체
                            item.getStatus()     // status 추가
                    );
                })
                .collect(Collectors.toList());

        // 3. 새로운 Page 객체 생성
        Page<LeaveRequestListItemDto> result = new PageImpl<>(
                updatedContent,
                rawResult.getPageable(),
                rawResult.getTotalElements()
        );

        log.info("휴가 신청 목록 조회 성공 - totalElements: {}, totalPages: {}",
                result.getTotalElements(), result.getTotalPages());

        return result;
    }

    @Override
    @Transactional
    public void createLeaveRequest(CreateLeaveRequestDto requestDto, String InternelUserId) {
        log.info("휴가 신청 요청 - internelUserId: {}, leaveType: {}, startDate: {}, endDate: {}",
                InternelUserId, requestDto.getLeaveType(),
                requestDto.getStartDate(), requestDto.getEndDate());

        // 1. InternelUser로 Employee 조회
        InternelUser internelUser = internelUserRepository.findById(InternelUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "내부 직원 정보를 찾을 수 없습니다."));

        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 2. 날짜 파싱 (YYYY-MM-DD 형식)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate startDate = LocalDate.parse(requestDto.getStartDate(), formatter);
        LocalDate endDate = LocalDate.parse(requestDto.getEndDate(), formatter);

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atStartOfDay();

        // 3. 휴가 일수 계산 (시작일과 종료일 포함)
        int numberOfLeaveDays = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

        // 4. LeaveRequest 생성
        LeaveRequest leaveRequest = new LeaveRequest(
                employee,
                requestDto.getLeaveType(),
                startDateTime,
                endDateTime,
                numberOfLeaveDays,
                null  // reason은 요청에 없으므로 null
        );

        // 5. 저장
        leaveRequestRepository.save(leaveRequest);

        log.info("휴가 신청 성공 - internelUserId: {}, leaveRequestId: {}, numberOfLeaveDays: {}",
                InternelUserId, leaveRequest.getId(), numberOfLeaveDays);
    }

    @Override
    @Transactional
    public void approveLeaveRequest(String requestId) {
        log.info("휴가 신청 승인 요청 - requestId: {}", requestId);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "휴가 신청을 찾을 수 없습니다."));

        leaveRequest.approve();

        log.info("휴가 신청 승인 완료 - requestId: {}", requestId);
    }

    @Override
    @Transactional
    public void rejectLeaveRequest(String requestId) {
        log.info("휴가 신청 반려 요청 - requestId: {}", requestId);

        LeaveRequest leaveRequest = leaveRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "휴가 신청을 찾을 수 없습니다."));

        leaveRequest.reject();

        log.info("휴가 신청 반려 완료 - requestId: {}", requestId);
    }

    @Override
    @Transactional(readOnly = true)
    public RemainingLeaveDaysDto getRemainingLeaveDays(String userId) {
        log.info("잔여 연차 조회 요청 - userId: {}", userId);

        // 1. InternelUser 조회
        InternelUser internelUser = internelUserRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 2. Employee 조회
        Employee employee = employeeRepository.findByInternelUser(internelUser)
                .orElseThrow(() -> new BusinessException(ErrorCode.CLIENT_NOT_FOUND, "직원 정보를 찾을 수 없습니다."));

        // 3. 1년 전 날짜 계산
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);

        // 4. 1년 이내 승인된 휴가 일수 합계 조회
        Integer usedLeaveDays = leaveRequestRepository.sumApprovedLeaveDaysInLastYear(
                employee.getId(),
                LeaveRequestStatus.APPROVED,
                oneYearAgo
        );

        // 5. 1년 이내 휴가 신청 목록 조회 (모든 상태 포함)
        List<org.ever._4ever_be_business.hr.entity.LeaveRequest> leaveRequests =
                leaveRequestRepository.findByEmployeeIdAndStartDateAfter(employee.getId(), oneYearAgo);

        // 6. LeaveItemDto 변환
        java.time.format.DateTimeFormatter dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<org.ever._4ever_be_business.hr.dto.response.LeaveItemDto> leaveItems = leaveRequests.stream()
                .map(lr -> new org.ever._4ever_be_business.hr.dto.response.LeaveItemDto(
                        lr.getId(),
                        lr.getLeaveType().name(),
                        lr.getStartDate() != null ? lr.getStartDate().format(dateFormatter) : null,
                        lr.getEndDate() != null ? lr.getEndDate().format(dateFormatter) : null,
                        lr.getNumberOfLeaveDays(),
                        lr.getStatus().name(),
                        lr.getReason()
                ))
                .collect(java.util.stream.Collectors.toList());

        // 7. 잔여 연차 계산 (기본 연차 18일 - 사용 일수)
        int used = (usedLeaveDays != null) ? usedLeaveDays : 0;
        int remaining = 18 - used;

        RemainingLeaveDaysDto result = new RemainingLeaveDaysDto(
                userId,
                18,
                used,
                remaining,
                leaveItems
        );

        log.info("잔여 연차 조회 성공 - userId: {}, usedLeaveDays: {}, remainingLeaveDays: {}, leaveRequestCount: {}",
                userId, used, remaining, leaveItems.size());

        return result;
    }
}
