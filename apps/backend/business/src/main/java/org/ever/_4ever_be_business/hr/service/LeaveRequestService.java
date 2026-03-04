package org.ever._4ever_be_business.hr.service;

import org.ever._4ever_be_business.hr.dto.request.CreateLeaveRequestDto;
import org.ever._4ever_be_business.hr.dto.response.LeaveRequestListItemDto;
import org.ever._4ever_be_business.hr.dto.response.RemainingLeaveDaysDto;
import org.ever._4ever_be_business.hr.vo.LeaveRequestSearchConditionVo;
import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LeaveRequestService {
    /**
     * 대시보드 휴가 신청 목록
     */
    java.util.List<DashboardWorkflowItemDto> getDashboardLeaveRequestList(String userId, int size);
    /**
     * 휴가 신청 목록 조회
     *
     * @param condition 검색 조건
     * @param pageable  페이징 정보
     * @return Page<LeaveRequestListItemDto>
     */
    Page<LeaveRequestListItemDto> getLeaveRequestList(LeaveRequestSearchConditionVo condition, Pageable pageable);

    /**
     * 휴가 신청
     *
     * @param requestDto 휴가 신청 정보
     */
    void createLeaveRequest(CreateLeaveRequestDto requestDto, String InternelUserId);

    /**
     * 휴가 신청 승인
     *
     * @param requestId 휴가 신청 ID
     */
    void approveLeaveRequest(String requestId);

    /**
     * 휴가 신청 반려
     *
     * @param requestId 휴가 신청 ID
     */
    void rejectLeaveRequest(String requestId);

    /**
     * 잔여 연차 조회
     *
     * @param userId 사용자 ID
     * @return RemainingLeaveDaysDto
     */
    RemainingLeaveDaysDto getRemainingLeaveDays(String userId);
}
