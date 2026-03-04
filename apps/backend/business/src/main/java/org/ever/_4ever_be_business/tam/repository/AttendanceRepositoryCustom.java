package org.ever._4ever_be_business.tam.repository;

import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.ever._4ever_be_business.tam.vo.AttendanceSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AttendanceRepositoryCustom {
    /**
     * Attendance ID로 상세 정보 조회
     *
     * @param attendanceId Attendance ID
     * @return Optional<Attendance>
     */
    Optional<Attendance> findByIdWithAllRelations(String attendanceId);

    /**
     * 근태 기록 목록 검색
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<Attendance>
     */
    Page<Attendance> searchAttendanceRecords(AttendanceSearchConditionVo condition, Pageable pageable);

    /**
     * 출퇴근 기록 목록 조회 (API용)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<AttendanceListItemDto>
     */
    Page<AttendanceListItemDto> findAttendanceList(AttendanceListSearchConditionVo condition, Pageable pageable);

    /**
     * 출퇴근 기록 Entity 목록 조회 (실시간 계산용)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<Attendance>
     */
    Page<Attendance> findAttendanceEntities(AttendanceListSearchConditionVo condition, Pageable pageable);

    /**
     * 오늘 날짜의 특정 직원 출퇴근 기록 조회
     *
     * @param employeeId 직원 ID
     * @return Optional<Attendance>
     */
    Optional<Attendance> findTodayAttendanceByEmployeeId(String employeeId);
}
