package org.ever._4ever_be_alarm.notification.adapter.jpa.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.NotificationTarget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTargetRepository extends JpaRepository<NotificationTarget, UUID> {

    /**
     * 사용자별 알림 대상 조회 (EntityGraph로 N+1 문제 해결)
     */
    @EntityGraph(attributePaths = {"notification", "notification.source"})
    @Query("SELECT nt FROM NotificationTarget nt " +
        "WHERE nt.userId = :userId " +
        "ORDER BY nt.createdAt DESC")
    Page<NotificationTarget> findByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * 사용자별 읽지 않은 알림 대상 조회
     */
    @Query("SELECT nt FROM NotificationTarget nt " +
        "WHERE nt.userId = :userId AND nt.isRead = false " +
        "ORDER BY nt.createdAt DESC")
    List<NotificationTarget> findUnreadByUserId(@Param("userId") UUID userId);

    /**
     * 사용자별 및 소스별 알림 대상 조회 (EntityGraph로 N+1 문제 해결)
     */
    @EntityGraph(attributePaths = {"notification", "notification.source"})
    @Query("SELECT nt FROM NotificationTarget nt " +
        "JOIN nt.notification n " +
        "JOIN n.source s " +
        "WHERE nt.userId = :userId AND s.sourceName = :sourceName")
    Page<NotificationTarget> findByUserIdAndSource(
        @Param("userId") UUID userId,
        @Param("sourceName") String sourceName,
        Pageable pageable);

    /**
     * 특정 알림의 모든 대상 조회
     */
    @Query("SELECT nt FROM NotificationTarget nt " +
        "WHERE nt.notification.id = :notificationId " +
        "ORDER BY nt.createdAt DESC")
    List<NotificationTarget> findByNotificationId(@Param("notificationId") UUID notificationId);

    /**
     * 특정 알림과 사용자의 대상 조회
     */
    @Query("SELECT nt FROM NotificationTarget nt " +
        "WHERE nt.notification.id = :notificationId AND nt.userId = :userId")
    Optional<NotificationTarget> findByNotificationIdAndUserId(
        @Param("notificationId") UUID notificationId,
        @Param("userId") UUID userId);

    /**
     * 사용자별 전체 알림 개수 조회
     */
    @Query("SELECT COUNT(nt) FROM NotificationTarget nt " +
        "WHERE nt.userId = :userId")
    Long countByUserId(@Param("userId") UUID userId);

    /**
     * 사용자별 읽지 않은 알림 개수 조회
     */
    @Query("SELECT COUNT(nt) FROM NotificationTarget nt " +
        "WHERE nt.userId = :userId AND nt.isRead = false")
    Long countUnreadByUserId(@Param("userId") UUID userId);

    /**
     * 사용자별 읽음 상태에 따른 알림 개수 조회
     */
    @Query("SELECT COUNT(nt) FROM NotificationTarget nt " +
        "WHERE nt.userId = :userId AND nt.isRead = :isRead")
    Long countByUserIdAndIsRead(@Param("userId") UUID userId, @Param("isRead") boolean isRead);

    /**
     * 사용자별 알림 읽음 처리
     */
    @Modifying
    @Query("UPDATE NotificationTarget nt " +
        "SET nt.isRead = true, nt.readAt = :readAt " +
        "WHERE nt.userId = :userId AND nt.id = :targetId")
    int markAsReadByUserIdAndTargetId(@Param("userId") UUID userId,
        @Param("targetId") UUID targetId,
        @Param("readAt") LocalDateTime readAt);

    /**
     * 사용자의 모든 알림 읽음 처리
     */
    @Modifying
    @Query("UPDATE NotificationTarget nt " +
        "SET nt.isRead = true, nt.readAt = :readAt " +
        "WHERE nt.userId = :userId AND nt.isRead = false")
    int markAllAsReadByUserId(@Param("userId") UUID userId, @Param("readAt") LocalDateTime readAt);

    /**
     * 특정 Notification ID와 User ID로 알림 읽음 처리
     */
    @Modifying
    @Query("UPDATE NotificationTarget nt " +
        "SET nt.isRead = true, nt.readAt = :readAt " +
        "WHERE nt.userId = :userId AND nt.notification.id = :notificationId")
    int markAsReadByUserIdAndNotificationId(
        @Param("userId") UUID userId,
        @Param("notificationId") UUID notificationId,
        @Param("readAt") LocalDateTime readAt
    );

    /**
     * 특정 상태의 알림 대상 조회
     */
    @Query("SELECT nt FROM NotificationTarget nt " +
        "WHERE nt.notificationStatus.id = :statusId " +
        "ORDER BY nt.createdAt DESC")
    List<NotificationTarget> findByStatusId(@Param("statusId") UUID statusId);

    /**
     * 특정 기간 내 알림 대상 조회
     */
    @Query("SELECT nt FROM NotificationTarget nt " +
        "WHERE nt.createdAt BETWEEN :startDate AND :endDate " +
        "ORDER BY nt.createdAt DESC")
    List<NotificationTarget> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
}