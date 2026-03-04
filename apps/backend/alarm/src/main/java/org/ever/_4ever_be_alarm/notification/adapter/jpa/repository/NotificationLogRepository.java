package org.ever._4ever_be_alarm.notification.adapter.jpa.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

    /**
     * 특정 알림의 로그 조회
     */
    @Query("SELECT nl FROM NotificationLog nl " +
        "WHERE nl.notification.id = :notificationId " +
        "ORDER BY nl.createdAt DESC")
    List<NotificationLog> findByNotificationId(@Param("notificationId") UUID notificationId);

    /**
     * 특정 에러 코드의 로그 조회
     */
    @Query("SELECT nl FROM NotificationLog nl " +
        "WHERE nl.notificationErrorCode.id = :errorCodeId " +
        "ORDER BY nl.createdAt DESC")
    List<NotificationLog> findByErrorCodeId(@Param("errorCodeId") UUID errorCodeId);

    /**
     * 특정 기간 내 로그 조회
     */
    @Query("SELECT nl FROM NotificationLog nl " +
        "WHERE nl.createdAt BETWEEN :startDate AND :endDate " +
        "ORDER BY nl.createdAt DESC")
    List<NotificationLog> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);

    /**
     * 재시도 횟수가 특정 값 이상인 로그 조회
     */
    @Query("SELECT nl FROM NotificationLog nl " +
        "WHERE nl.retryCount >= :retryCount " +
        "ORDER BY nl.createdAt DESC")
    List<NotificationLog> findByRetryCountGreaterThanEqual(@Param("retryCount") Integer retryCount);

    /**
     * 특정 알림의 최신 로그 조회
     */
    @Query("SELECT nl FROM NotificationLog nl " +
        "WHERE nl.notification.id = :notificationId " +
        "ORDER BY nl.createdAt DESC " +
        "LIMIT 1")
    NotificationLog findLatestByNotificationId(@Param("notificationId") UUID notificationId);

    /**
     * 최대 재시도 횟수에 도달한 로그 조회
     */
    @Query("SELECT nl FROM NotificationLog nl " +
        "WHERE nl.retryCount >= 3 " +
        "ORDER BY nl.createdAt DESC")
    List<NotificationLog> findMaxRetryReachedLogs();
}