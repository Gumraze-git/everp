package org.ever._4ever_be_alarm.notification.adapter.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, UUID> {

    /**
     * 사용자 ID로 활성화된 토큰 목록 조회
     */
    @Query("SELECT u FROM UserDeviceToken u WHERE u.userId = :userId AND u.isActive = true")
    List<UserDeviceToken> findActiveTokensByUserId(@Param("userId") UUID userId);

    /**
     * 사용자 ID로 모든 토큰 조회
     */
    @Query("SELECT u FROM UserDeviceToken u WHERE u.userId = :userId")
    List<UserDeviceToken> findAllByUserId(@Param("userId") UUID userId);

    /**
     * FCM 토큰으로 조회
     */
    Optional<UserDeviceToken> findByFcmToken(String fcmToken);

    /**
     * 사용자 ID와 디바이스 ID로 조회
     */
    Optional<UserDeviceToken> findByUserIdAndDeviceId(UUID userId, String deviceId);

    /**
     * FCM 토큰으로 삭제
     */
    @Modifying
    @Query("DELETE FROM UserDeviceToken u WHERE u.fcmToken = :fcmToken")
    int deleteByFcmToken(@Param("fcmToken") String fcmToken);

    /**
     * 사용자 ID로 모든 토큰 비활성화
     */
    @Modifying
    @Query("UPDATE UserDeviceToken u SET u.isActive = false WHERE u.userId = :userId")
    int deactivateAllByUserId(@Param("userId") UUID userId);

    /**
     * 특정 토큰만 활성화하고 나머지는 비활성화
     */
    @Modifying
    @Query("UPDATE UserDeviceToken u SET u.isActive = CASE WHEN u.id = :tokenId THEN true ELSE false END WHERE u.userId = :userId")
    int activateOnlyOne(@Param("userId") UUID userId, @Param("tokenId") UUID tokenId);
}

