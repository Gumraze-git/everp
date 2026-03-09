package org.ever._4ever_be_gw.api.alarm;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationFcmTokenRequestDto;
import org.ever._4ever_be_gw.alarm.service.AlarmHttpService;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@ApiServerErrorResponse
public interface FcmTokenApi {

    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록합니다.")
    public ResponseEntity<Object> registerFcmToken(
        @AuthenticationPrincipal EverUserPrincipal principal,
        EverJwtAuthenticationToken authentication,

        @Valid
        @RequestBody
        NotificationFcmTokenRequestDto notificationFcmTokenRequestDto
    );

}
