package org.ever._4ever_be_business.tam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.tam.dao.LeaveRequestDAO;
import org.ever._4ever_be_business.tam.dto.response.LeaveRequestEmployeeDto;
import org.ever._4ever_be_business.tam.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.tam.entity.VacationRequest;
import org.ever._4ever_be_business.tam.service.LeaveRequestService;
import org.ever._4ever_be_business.tam.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("tamLeaveRequestService")
@RequiredArgsConstructor
public class LeaveRequestServiceImpl implements LeaveRequestService {

    private final LeaveRequestDAO leaveRequestDAO;

    @Override
    @Transactional(readOnly = true)
    public Page<LeaveRequestListItemDto> getLeaveRequestList(LeaveRequestSearchConditionVo condition, Pageable pageable) {
        // 1. DAO를 통해 휴가 신청 목록 조회
        Page<VacationRequest> vacationRequestPage = leaveRequestDAO.searchLeaveRequests(condition, pageable);

        // 2. VacationRequest 엔티티를 LeaveRequestListItemDto로 변환
        List<LeaveRequestListItemDto> content = vacationRequestPage.getContent().stream()
                .map(vacationRequest -> {
                    Employee employee = vacationRequest.getEmployee();
                    InternelUser internelUser = employee.getInternelUser();
                    Position position = internelUser != null ? internelUser.getPosition() : null;

                    // LeaveRequestEmployeeDto 생성
                    LeaveRequestEmployeeDto employeeDto = new LeaveRequestEmployeeDto(
                            employee.getId(),
                            internelUser != null ? internelUser.getName() : "알 수 없음",
                            position != null && position.getDepartment() != null
                                    ? position.getDepartment().getDepartmentName()
                                    : "미지정",
                            position != null ? position.getPositionName() : "미지정"
                    );

                    // 날짜 변환 (LocalDateTime -> LocalDate)
                    LocalDate startDate = vacationRequest.getRequestedStartDate() != null
                            ? vacationRequest.getRequestedStartDate().toLocalDate()
                            : null;
                    LocalDate endDate = vacationRequest.getRequestedEndDate() != null
                            ? vacationRequest.getRequestedEndDate().toLocalDate()
                            : null;

                    // numberOfLeaveDays 계산 (시작일과 종료일 포함)
                    Integer numberOfLeaveDays = 0;
                    if (startDate != null && endDate != null) {
                        numberOfLeaveDays = (int) (ChronoUnit.DAYS.between(startDate, endDate) + 1);
                    }

                    // remainingLeaveDays는 Employee의 remainingVacation 사용
                    Long remainingLeaveDays = employee.getRemainingVacation() != null
                            ? employee.getRemainingVacation()
                            : 0L;

                    return new LeaveRequestListItemDto(
                            vacationRequest.getId(),
                            employeeDto,
                            vacationRequest.getVacationType(),
                            startDate,
                            endDate,
                            numberOfLeaveDays,
                            remainingLeaveDays
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, vacationRequestPage.getTotalElements());
    }
}
