package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.hr.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveRequestRepositoryCustom {
    /**
     * 휴가 신청 목록 조회 (동적 쿼리)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<LeaveRequestListItemDto>
     */
    Page<LeaveRequestListItemDto> findLeaveRequestList(LeaveRequestSearchConditionVo condition, Pageable pageable);
}
