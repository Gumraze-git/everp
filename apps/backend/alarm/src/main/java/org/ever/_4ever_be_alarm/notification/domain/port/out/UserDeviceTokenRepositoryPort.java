package org.ever._4ever_be_alarm.notification.domain.port.out;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.domain.model.UserDeviceInfo;

/**
 * 사용자 디바이스 토큰 저장소 포트
 */
public interface UserDeviceTokenRepositoryPort {

    /**
     * 토큰 정보 저장
     */
    UserDeviceInfo save(UserDeviceInfo tokenInfo);

    /**
     * 사용자 ID로 활성화된 토큰 목록 조회
     */
    List<UserDeviceInfo> findActiveTokensByUserId(UUID userId);

    /**
     * 사용자 ID로 모든 토큰 조회
     */
    List<UserDeviceInfo> findAllByUserId(UUID userId);

    /**
     * FCM 토큰으로 조회
     */
    Optional<UserDeviceInfo> findByFcmToken(String fcmToken);

    /**
     * 사용자 ID와 디바이스 ID로 조회
     */
    Optional<UserDeviceInfo> findByUserIdAndDeviceId(UUID userId, String deviceId);

    /**
     * FCM 토큰 삭제
     */
    int deleteByFcmToken(String fcmToken);

    /**
     * 사용자 ID로 모든 토큰 비활성화
     */
    int deactivateAllByUserId(UUID userId);
}

