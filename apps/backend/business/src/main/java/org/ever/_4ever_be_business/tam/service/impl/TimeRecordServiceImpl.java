package org.ever._4ever_be_business.tam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.tam.dao.TimeRecordDAO;
import org.ever._4ever_be_business.tam.dto.request.UpdateTimeRecordDto;
import org.ever._4ever_be_business.tam.dto.response.EmployeeBasicInfoDto;
import org.ever._4ever_be_business.tam.dto.response.EmployeeInfoDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordDetailDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordListItemDto;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.repository.AttendanceRepository;
import org.ever._4ever_be_business.tam.service.TimeRecordService;
import org.ever._4ever_be_business.tam.vo.AttendanceSearchConditionVo;
import org.ever._4ever_be_business.tam.vo.TimeRecordDetailVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeRecordServiceImpl implements TimeRecordService {

    private final TimeRecordDAO timeRecordDAO;
    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public TimeRecordDetailDto getTimeRecordDetail(TimeRecordDetailVo vo) {
        String timerecordId = vo.getTimerecordId();

        // 1. Attendance 조회 (Employee, InternelUser, Position, Department JOIN FETCH)
        Attendance attendance = timeRecordDAO.findAttendanceByIdWithDetails(timerecordId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 근태 기록입니다. ID: " + timerecordId));

        // 2. Employee 정보 추출
        Employee employee = attendance.getEmployee();
        InternelUser internelUser = employee.getInternelUser();
        Position position = internelUser != null ? internelUser.getPosition() : null;

        // 3. EmployeeInfoDto 생성
        EmployeeInfoDto employeeInfo = new EmployeeInfoDto(
                employee.getId(),
                internelUser != null ? internelUser.getEmployeeCode() : null,
                internelUser != null ? internelUser.getName() : "알 수 없음",
                position != null && position.getDepartment() != null
                        ? position.getDepartment().getId()
                        : null,
                position != null && position.getDepartment() != null
                        ? position.getDepartment().getDepartmentName()
                        : "미지정",
                position != null ? position.getId() : null,
                position != null ? position.getPositionName() : "미지정"
        );

        // 4. workDate를 LocalDate로 변환
        LocalDate workDate = attendance.getWorkDate() != null
                ? attendance.getWorkDate().toLocalDate()
                : null;

        // 5. TimeRecordDetailDto 생성 및 반환
        return new TimeRecordDetailDto(
                attendance.getId(),
                employeeInfo,
                workDate,
                attendance.getCheckIn(),
                attendance.getCheckOut(),
                attendance.getWorkMinutes(),
                attendance.getOvertimeMinutes(),
                attendance.getStatus()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TimeRecordListItemDto> getAttendanceList(AttendanceSearchConditionVo condition, Pageable pageable) {
        // 1. DAO를 통해 근태 기록 목록 조회
        Page<Attendance> attendancePage = timeRecordDAO.searchAttendanceRecords(condition, pageable);

        // 2. Attendance 엔티티를 TimeRecordListItemDto로 변환
        List<TimeRecordListItemDto> content = attendancePage.getContent().stream()
                .map(attendance -> {
                    Employee employee = attendance.getEmployee();
                    InternelUser internelUser = employee.getInternelUser();
                    Position position = internelUser != null ? internelUser.getPosition() : null;

                    // EmployeeBasicInfoDto 생성
                    EmployeeBasicInfoDto employeeBasicInfo = new EmployeeBasicInfoDto(
                            employee.getId(),
                            internelUser != null ? internelUser.getName() : "알 수 없음",
                            position != null && position.getDepartment() != null
                                    ? position.getDepartment().getId()
                                    : null,
                            position != null && position.getDepartment() != null
                                    ? position.getDepartment().getDepartmentName()
                                    : "미지정",
                            position != null ? position.getId() : null,
                            position != null ? position.getPositionName() : "미지정"
                    );

                    // workDate를 LocalDate로 변환
                    LocalDate workDate = attendance.getWorkDate() != null
                            ? attendance.getWorkDate().toLocalDate()
                            : null;

                    return new TimeRecordListItemDto(
                            attendance.getId(),
                            employeeBasicInfo,
                            workDate,
                            attendance.getCheckIn(),
                            attendance.getCheckOut(),
                            attendance.getWorkMinutes(),
                            attendance.getOvertimeMinutes(),
                            attendance.getStatus()
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, attendancePage.getTotalElements());
    }

    @Override
    @Transactional
    public void updateTimeRecord(String timerecordId, UpdateTimeRecordDto requestDto) {
        log.info("근태 기록 수정 요청 - timerecordId: {}, employeeId: {}", timerecordId, requestDto.getEmployeeId());

        // 1. Attendance 조회
        Attendance attendance = attendanceRepository.findById(timerecordId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 근태 기록입니다."));

        // 2. Employee 조회
        Employee employee = employeeRepository.findById(requestDto.getEmployeeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "존재하지 않는 직원입니다."));

        // 3. Attendance 수정
        attendance.updateTimeRecord(
                requestDto.getInTime(),
                requestDto.getOutTime(),
                requestDto.getAttendanceStatus(),
                employee
        );

        // 4. 저장 (dirty checking에 의해 자동 저장)
        log.info("근태 기록 수정 완료 - timerecordId: {}", timerecordId);
    }
}
