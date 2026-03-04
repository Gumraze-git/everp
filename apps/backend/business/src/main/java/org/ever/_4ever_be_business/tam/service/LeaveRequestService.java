package org.ever._4ever_be_business.tam.service;

import org.ever._4ever_be_business.tam.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.tam.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveRequestService {
    /**
     * 휴가 신청 목록 조회 (검색 + 페이징)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<LeaveRequestListItemDto>
     */
    Page<LeaveRequestListItemDto> getLeaveRequestList(LeaveRequestSearchConditionVo condition, Pageable pageable);
}
