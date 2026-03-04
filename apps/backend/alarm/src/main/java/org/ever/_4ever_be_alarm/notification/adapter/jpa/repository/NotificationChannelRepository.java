package org.ever._4ever_be_alarm.notification.adapter.jpa.repository;

import java.util.List;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationChannelRepository extends JpaRepository<NotificationChannel, UUID> {

    /**
     * 특정 알림의 채널 조회
     */
    @Query("SELECT nc FROM NotificationChannel nc " +
        "WHERE nc.notification.id = :notificationId")
    List<NotificationChannel> findByNotificationId(@Param("notificationId") UUID notificationId);

    /**
     * 특정 채널 타입의 알림 채널 조회
     */
    @Query("SELECT nc FROM NotificationChannel nc " +
        "WHERE nc.channel.id = :channelId")
    List<NotificationChannel> findByChannelId(@Param("channelId") UUID channelId);

    /**
     * 알림 ID로 채널 삭제
     */
    @Query("DELETE FROM NotificationChannel nc " +
        "WHERE nc.notification.id = :notificationId")
    void deleteByNotificationId(@Param("notificationId") UUID notificationId);

    /**
     * 특정 알림과 채널의 조합 존재 여부 확인
     */
    @Query("SELECT COUNT(nc) > 0 FROM NotificationChannel nc " +
        "WHERE nc.notification.id = :notificationId AND nc.channel.id = :channelId")
    boolean existsByNotificationIdAndChannelId(@Param("notificationId") UUID notificationId,
        @Param("channelId") UUID channelId);
}