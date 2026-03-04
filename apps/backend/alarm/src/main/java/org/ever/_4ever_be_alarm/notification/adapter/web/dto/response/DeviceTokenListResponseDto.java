package org.ever._4ever_be_alarm.notification.adapter.web.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FCM 토큰 목록 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTokenListResponseDto {

    private List<DeviceTokenResponseDto> tokens;
    private Integer totalCount;
}

