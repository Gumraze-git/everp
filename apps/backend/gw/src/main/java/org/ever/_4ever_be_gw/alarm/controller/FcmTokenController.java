package org.ever._4ever_be_gw.alarm.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationFcmTokenRequestDto;
import org.ever._4ever_be_gw.alarm.service.AlarmHttpService;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alarm/fcm-tokens")
@Validated
@RequiredArgsConstructor
@Slf4j
public class FcmTokenController {

    private final AlarmHttpService alarmHttpService;


    // ===== FCM 토큰 등록 =====
    @PostMapping("/register")
    @Operation(summary = "FCM 토큰 등록", description = "사용자의 FCM 토큰을 등록합니다.")
    public ResponseEntity<Object> registerFcmToken(
        @AuthenticationPrincipal EverUserPrincipal principal,
        EverJwtAuthenticationToken authentication,

        @Valid
        @RequestBody
        NotificationFcmTokenRequestDto notificationFcmTokenRequestDto
    ) {
        final String token = (authentication != null && authentication.getToken() != null)
            ? authentication.getToken().getTokenValue()
            : null;

        log.info("[FCM][USER] 사용자 정보 조회 요청 수신: principalPresent={}, tokenPresent={}",
            principal != null, token != null);

        // Service가 ResponseEntity를 반환하므로 그대로 반환
        return alarmHttpService.registerFcmToken(
            principal.getUserId(),
            notificationFcmTokenRequestDto
        );
    }

}
