package org.ever._4ever_be_gw.alarm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_gw.common.dto.validation.AllowedValues;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "FCM 토큰 등록 요청")
public class NotificationFcmTokenRequestDto {

    @NotBlank(message = "FCM 토큰은 필수입니다.")
    @Schema(description = "FCM 토큰", example = "fXkX9...example_token...", required = true)
    private String token;

    private String deviceId;

    @AllowedValues(
        allowedValues = {"IOS", "ANDROID", "WEB"},
        ignoreCase = true,
        message = "deviceType은 IOS, ANDROID, WEB 중 하나여야 합니다."
    )
    private String deviceType; // IOS, ANDROID, WEB
}
