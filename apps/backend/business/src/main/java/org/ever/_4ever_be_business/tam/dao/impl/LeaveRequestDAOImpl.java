package org.ever._4ever_be_business.tam.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.tam.dao.LeaveRequestDAO;
import org.ever._4ever_be_business.tam.entity.VacationRequest;
import org.ever._4ever_be_business.tam.repository.VacationRequestRepository;
import org.ever._4ever_be_business.tam.vo.LeaveRequestSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeaveRequestDAOImpl implements LeaveRequestDAO {

    private final VacationRequestRepository vacationRequestRepository;

    @Override
    public Page<VacationRequest> searchLeaveRequests(LeaveRequestSearchConditionVo condition, Pageable pageable) {
        return vacationRequestRepository.searchLeaveRequests(condition, pageable);
    }
}
