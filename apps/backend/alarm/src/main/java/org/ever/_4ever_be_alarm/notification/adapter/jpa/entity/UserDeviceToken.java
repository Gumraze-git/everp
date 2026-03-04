package org.ever._4ever_be_alarm.notification.adapter.jpa.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_alarm.common.entity.TimeStamp;
import org.ever._4ever_be_alarm.notification.domain.model.constants.DeviceTypeEnum;

/**
 * 사용자 디바이스 FCM 토큰 정보
 */
@Entity
@Table(name = "user_device_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDeviceToken extends TimeStamp {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "uuid")
    private UUID userId;

    @Column(name = "fcm_token", nullable = false, length = 500)
    private String fcmToken;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", length = 50)
    private DeviceTypeEnum deviceType;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder
    public UserDeviceToken(
        UUID id,
        UUID userId,
        String fcmToken,
        String deviceId,
        DeviceTypeEnum deviceType,
        Boolean isActive
    ) {
        this.id = id;
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.isActive = isActive != null ? isActive : true;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = Generators.timeBasedEpochGenerator().generate();
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    /**
     * FCM 토큰 업데이트
     */
    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    /**
     * 디바이스 정보 업데이트
     */
    public void updateDeviceInfo(String deviceId, DeviceTypeEnum deviceType) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
    }

    /**
     * 토큰 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 토큰 활성화
     */
    public void activate() {
        this.isActive = true;
    }
}

