package org.ever._4ever_be_business.tam.dao;

import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.vo.AttendanceSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TimeRecordDAO {
    /**
     * Attendance ID로 상세 정보 조회 (모든 연관 엔티티 포함)
     *
     * @param attendanceId Attendance ID
     * @return Optional<Attendance>
     */
    Optional<Attendance> findAttendanceByIdWithDetails(String attendanceId);

    /**
     * 근태 기록 목록 검색 (동적 쿼리)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<Attendance>
     */
    Page<Attendance> searchAttendanceRecords(AttendanceSearchConditionVo condition, Pageable pageable);
}
