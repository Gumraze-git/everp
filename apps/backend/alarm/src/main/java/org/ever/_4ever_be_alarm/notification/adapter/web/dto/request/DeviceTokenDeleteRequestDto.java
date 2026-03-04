package org.ever._4ever_be_alarm.notification.adapter.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FCM 토큰 삭제 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenDeleteRequestDto {

    @NotBlank(message = "fcmToken은 필수입니다.")
    private String fcmToken;
}

