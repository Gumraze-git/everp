package org.ever._4ever_be_business.tam.dao;

import org.ever._4ever_be_business.tam.entity.VacationRequest;
import org.ever._4ever_be_business.tam.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveRequestDAO {
    /**
     * 휴가 신청 목록 검색 (동적 쿼리)
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<VacationRequest>
     */
    Page<VacationRequest> searchLeaveRequests(LeaveRequestSearchConditionVo condition, Pageable pageable);
}
