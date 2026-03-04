package org.ever._4ever_be_business.tam.dao.impl;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_business.tam.dao.TimeRecordDAO;
import org.ever._4ever_be_business.tam.entity.Attendance;
import org.ever._4ever_be_business.tam.repository.AttendanceRepository;
import org.ever._4ever_be_business.tam.vo.AttendanceSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TimeRecordDAOImpl implements TimeRecordDAO {

    private final AttendanceRepository attendanceRepository;

    @Override
    public Optional<Attendance> findAttendanceByIdWithDetails(String attendanceId) {
        return attendanceRepository.findByIdWithAllRelations(attendanceId);
    }

    @Override
    public Page<Attendance> searchAttendanceRecords(AttendanceSearchConditionVo condition, Pageable pageable) {
        return attendanceRepository.searchAttendanceRecords(condition, pageable);
    }
}
