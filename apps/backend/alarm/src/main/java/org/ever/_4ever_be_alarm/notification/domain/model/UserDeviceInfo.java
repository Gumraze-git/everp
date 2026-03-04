package org.ever._4ever_be_alarm.notification.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import org.ever._4ever_be_alarm.notification.domain.model.constants.DeviceTypeEnum;

/**
 * 사용자 디바이스 토큰 정보 도메인 모델
 */
@Getter
@Builder
public class UserDeviceInfo {

    private UUID id;
    private UUID userId;
    private String fcmToken;
    private String deviceId;
    private DeviceTypeEnum deviceType;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

