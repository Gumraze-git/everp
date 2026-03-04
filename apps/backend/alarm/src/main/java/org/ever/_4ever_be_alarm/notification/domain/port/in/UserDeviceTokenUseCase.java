package org.ever._4ever_be_alarm.notification.domain.port.in;

import java.util.List;
import org.ever._4ever_be_alarm.notification.domain.model.UserDeviceInfo;

/**
 * 사용자 디바이스 토큰 관리 유즈케이스
 */
public interface UserDeviceTokenUseCase {

    /**
     * FCM 토큰 등록 (신규 또는 업데이트)
     */
    UserDeviceInfo registerToken(String userId, String fcmToken, String deviceId,
        String deviceType);

    /**
     * FCM 토큰 업데이트
     */
    UserDeviceInfo updateToken(String userId, String fcmToken, String deviceId,
        String deviceType);

    /**
     * 사용자의 활성화된 FCM 토큰 목록 조회
     */
    List<UserDeviceInfo> getActiveTokensByUserId(String userId);

    /**
     * FCM 토큰 삭제
     */
    void deleteToken(String fcmToken);

    /**
     * 사용자의 모든 토큰 비활성화
     */
    void deactivateAllTokens(String userId);
}

