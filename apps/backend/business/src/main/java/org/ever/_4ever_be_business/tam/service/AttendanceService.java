package org.ever._4ever_be_business.tam.service;

import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.dto.response.AttendanceRecordDto;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AttendanceService {
    /**
     * 대시보드 근태 목록 조회
     *
     * @param userId 사용자 ID (null 허용)
     * @param size   조회 건수
     * @return 대시보드 항목 리스트
     */
    List<DashboardWorkflowItemDto> getDashboardAttendanceList(String userId, int size);

    /**
     * 출퇴근 기록 목록 조회
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<AttendanceListItemDto>
     */
    Page<AttendanceListItemDto> getAttendanceList(AttendanceListSearchConditionVo condition, Pageable pageable);

    /**
     * 출근 처리
     *
     * @param employeeId 직원 ID
     */
    void checkIn(String employeeId);

    /**
     * 퇴근 처리
     *
     * @param employeeId 직원 ID
     */
    void checkOut(String employeeId);

    /**
     * InternelUser ID로 출근 처리
     *
     * @param internelUserId InternelUser ID
     */
    void checkInByInternelUserId(String internelUserId);

    /**
     * InternelUser ID로 퇴근 처리
     *
     * @param internelUserId InternelUser ID
     */
    void checkOutByInternelUserId(String internelUserId);

    /**
     * InternelUser ID로 출퇴근 기록 목록 조회
     *
     * @param internelUserId InternelUser ID
     * @return 출퇴근 기록 목록
     */
    List<AttendanceRecordDto> getAttendanceRecordsByInternelUserId(String internelUserId);
}
