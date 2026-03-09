package org.ever._4ever_be_alarm.api.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_alarm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.DeviceTokenDeleteRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.DeviceTokenRegisterRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.DeviceTokenUpdateRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.DeviceTokenListResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.DeviceTokenResponseDto;
import org.ever._4ever_be_alarm.notification.domain.model.UserDeviceInfo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "디바이스 토큰", description = "FCM 디바이스 토큰 관리 API")
@ApiServerErrorResponse
public interface DeviceTokenApi {

    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 디바이스 토큰을 등록합니다.")
    ResponseEntity<UserDeviceInfo> registerToken(@RequestBody DeviceTokenRegisterRequestDto request);

    @Operation(summary = "FCM 토큰 수정", description = "기존 디바이스 토큰을 갱신합니다.")
    ResponseEntity<DeviceTokenResponseDto> updateToken(@RequestBody DeviceTokenUpdateRequestDto request);

    @Operation(summary = "사용자별 FCM 토큰 조회", description = "사용자의 활성 FCM 토큰 목록을 조회합니다.")
    ResponseEntity<DeviceTokenListResponseDto> getActiveTokensByUserId(@PathVariable("userId") String userId);

    @Operation(summary = "FCM 토큰 삭제", description = "단일 FCM 토큰을 삭제합니다.")
    ResponseEntity<Void> deleteToken(@RequestBody DeviceTokenDeleteRequestDto request);

    @Operation(summary = "사용자 토큰 전체 비활성화", description = "사용자의 모든 디바이스 토큰을 비활성화합니다.")
    ResponseEntity<Void> deactivateAllTokens(@PathVariable("userId") String userId);
}
