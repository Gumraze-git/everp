package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.LeaveRequest;
import org.ever._4ever_be_business.hr.enums.LeaveRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * LeaveRequest Repository
 */
@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, String>, LeaveRequestRepositoryCustom {

    /**
     * 특정 직원의 승인된 휴가 일수 합계 조회
     *
     * @param employeeId 직원 ID
     * @return 승인된 휴가 일수 합계 (없으면 0)
     */
    @Query("SELECT COALESCE(SUM(lr.numberOfLeaveDays), 0) " +
           "FROM LeaveRequest lr " +
           "WHERE lr.employee.id = :employeeId " +
           "AND lr.status = :status")
    Integer sumApprovedLeaveDaysByEmployeeId(@Param("employeeId") String employeeId,
                                              @Param("status") LeaveRequestStatus status);

    /**
     * 특정 직원의 1년 이내 승인된 휴가 일수 합계 조회
     *
     * @param employeeId 직원 ID
     * @param startDate 시작 날짜 (1년 전)
     * @param status 휴가 신청 상태
     * @return 승인된 휴가 일수 합계 (없으면 0)
     */
    @Query("SELECT COALESCE(SUM(lr.numberOfLeaveDays), 0) " +
           "FROM LeaveRequest lr " +
           "WHERE lr.employee.id = :employeeId " +
           "AND lr.status = :status " +
           "AND lr.startDate >= :startDate")
    Integer sumApprovedLeaveDaysInLastYear(@Param("employeeId") String employeeId,
                                            @Param("status") LeaveRequestStatus status,
                                            @Param("startDate") LocalDateTime startDate);

    /**
     * 최신 휴가 신청 목록 조회 (전사)
     */
    @EntityGraph(attributePaths = {"employee", "employee.internelUser"})
    Page<LeaveRequest> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 특정 사용자 휴가 신청 최신 목록 조회
     */
    @EntityGraph(attributePaths = {"employee", "employee.internelUser"})
    Page<LeaveRequest> findAllByEmployee_InternelUser_UserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * 특정 직원의 1년 이내 휴가 신청 목록 조회 (모든 상태 포함)
     *
     * @param employeeId 직원 ID
     * @param startDate 시작 날짜 (1년 전)
     * @return 휴가 신청 목록
     */
    @Query("SELECT lr FROM LeaveRequest lr " +
           "WHERE lr.employee.id = :employeeId " +
           "AND lr.startDate >= :startDate " +
           "ORDER BY lr.startDate DESC")
    java.util.List<LeaveRequest> findByEmployeeIdAndStartDateAfter(@Param("employeeId") String employeeId,
                                                                     @Param("startDate") LocalDateTime startDate);
}
