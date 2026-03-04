package org.ever._4ever_be_business.tam.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.tam.dao.AttendanceDAO;
import org.ever._4ever_be_business.tam.dto.response.AttendanceListItemDto;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.repository.AttendanceRepository;
import org.ever._4ever_be_business.tam.vo.AttendanceListSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendanceDAOImpl implements AttendanceDAO {

    private final AttendanceRepository attendanceRepository;

    @Override
    public Page<AttendanceListItemDto> findAttendanceList(AttendanceListSearchConditionVo condition, Pageable pageable) {
        return attendanceRepository.findAttendanceList(condition, pageable);
    }

    @Override
    public Page<Attendance> findAttendanceEntities(AttendanceListSearchConditionVo condition, Pageable pageable) {
        return attendanceRepository.findAttendanceEntities(condition, pageable);
    }
}
