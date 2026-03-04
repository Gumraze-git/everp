package org.ever._4ever_be_alarm.notification.adapter.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_alarm.common.validation.AllowedValues;
import org.ever._4ever_be_alarm.common.validation.ValidUuidV7;

/**
 * FCM 토큰 등록 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenRegisterRequestDto {

    @ValidUuidV7
    @NotBlank(message = "userId는 필수입니다.")
    @JsonProperty("userId")
    private String userId;

    @NotBlank(message = "fcmToken은 필수입니다.")
    @JsonProperty("token")
    private String fcmToken;

    @JsonProperty("deviceId")
    private String deviceId;

    @AllowedValues(
        allowedValues = {"IOS", "ANDROID", "WEB"},
        ignoreCase = true,
        message = "deviceType은 IOS, ANDROID, WEB 중 하나여야 합니다."
    )
    private String deviceType; // IOS, ANDROID, WEB
}

