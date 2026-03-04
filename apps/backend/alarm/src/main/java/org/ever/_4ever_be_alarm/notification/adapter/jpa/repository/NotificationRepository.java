package org.ever._4ever_be_alarm.notification.adapter.jpa.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * 특정 기간 내 알림 조회
     */
    @Query("SELECT n FROM Notification n " +
        "WHERE n.createdAt BETWEEN :startDate AND :endDate " +
        "ORDER BY n.createdAt DESC")
    List<Notification> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    /**
     * 소스별 알림 조회
     */
    @Query("SELECT n FROM Notification n " +
        "WHERE n.source.id = :sourceId " +
        "ORDER BY n.createdAt DESC")
    List<Notification> findBySourceId(@Param("sourceId") UUID sourceId);

    /**
     * 참조 타입별 알림 조회
     */
    @Query("SELECT n FROM Notification n " +
        "WHERE n.referenceType = :referenceType " +
        "ORDER BY n.createdAt DESC")
    List<Notification> findByReferenceType(@Param("referenceType") String referenceType);

    /**
     * 예약된 알림 조회 (scheduled_at이 현재 시간 이전)
     */
    @Query("SELECT n FROM Notification n " +
        "WHERE n.scheduledAt IS NOT NULL AND n.scheduledAt <= :currentTime " +
        "ORDER BY n.scheduledAt ASC")
    List<Notification> findScheduledNotifications(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 특정 참조 ID로 알림 조회
     */
    @Query("SELECT n FROM Notification n " +
        "WHERE n.referenceId = :referenceId " +
        "ORDER BY n.createdAt DESC")
    List<Notification> findByReferenceId(@Param("referenceId") UUID referenceId);
}