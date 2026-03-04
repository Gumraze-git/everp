package org.ever._4ever_be_business.tam.repository;

import org.ever._4ever_be_business.tam.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String>, AttendanceRepositoryCustom {
    /**
     * 직원 ID로 모든 출퇴근 기록 조회 (근무일 내림차순)
     *
     * @param employeeId 직원 ID
     * @return 출퇴근 기록 목록
     */
    List<Attendance> findAllByEmployeeIdOrderByWorkDateDesc(String employeeId);

    /**
     * 전체 근태 기록을 최신순으로 조회
     */
    @EntityGraph(attributePaths = {"employee", "employee.internelUser"})
    Page<Attendance> findAllByOrderByWorkDateDesc(Pageable pageable);

    /**
     * 특정 사용자 근태 기록을 최신순으로 조회
     */
    @EntityGraph(attributePaths = {"employee", "employee.internelUser"})
    Page<Attendance> findAllByEmployee_InternelUser_UserIdOrderByWorkDateDesc(String userId, Pageable pageable);
}
