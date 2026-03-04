package org.ever._4ever_be_business.tam.dao;

import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttendanceDAO {
    /**
     * 출퇴근 기록 목록 조회
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
}
