package org.ever._4ever_be_business.tam.service;

import org.ever._4ever_be_business.tam.dto.request.UpdateTimeRecordDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordDetailDto;
import org.ever._4ever_be_business.tam.dto.response.TimeRecordListItemDto;
import org.ever._4ever_be_business.tam.vo.AttendanceSearchConditionVo;
import org.ever._4ever_be_business.tam.vo.TimeRecordDetailVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TimeRecordService {
    /**
     * 근태 기록 상세 정보 조회
     *
     * @param vo TimeRecordDetailVo
     * @return TimeRecordDetailDto
     */
    TimeRecordDetailDto getTimeRecordDetail(TimeRecordDetailVo vo);

    /**
     * 근태 기록 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<TimeRecordListItemDto>
     */
    Page<TimeRecordListItemDto> getAttendanceList(AttendanceSearchConditionVo condition, Pageable pageable);

    /**
     * 근태 기록 수정
     *
     * @param timerecordId 근태 기록 ID
     * @param requestDto   수정 요청 데이터
     */
    void updateTimeRecord(String timerecordId, UpdateTimeRecordDto requestDto);
}
