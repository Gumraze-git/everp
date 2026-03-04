package org.ever._4ever_be_alarm.notification.adapter.web.in;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.common.response.ApiResponse;
import org.ever._4ever_be_alarm.common.validation.ValidUuidV7;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.DeviceTokenDeleteRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.DeviceTokenRegisterRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.DeviceTokenUpdateRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.DeviceTokenListResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.DeviceTokenResponseDto;
import org.ever._4ever_be_alarm.notification.domain.model.UserDeviceInfo;
import org.ever._4ever_be_alarm.notification.domain.port.in.UserDeviceTokenUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * FCM 디바이스 토큰 관리 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/device-tokens")
@RequiredArgsConstructor
@Validated
public class DeviceTokenController {

    private final UserDeviceTokenUseCase userDeviceTokenUseCase;

    /**
     * FCM 토큰 등록
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDeviceInfo>> registerToken(
        @Valid
        @RequestBody
        DeviceTokenRegisterRequestDto request
    ) {
        log.info("[API] FCM 토큰 등록 요청 - userId: {}, deviceId: {}",
            request.getUserId(), request.getDeviceId());

        try {
            UserDeviceInfo tokenInfo = userDeviceTokenUseCase.registerToken(
                request.getUserId(),
                request.getFcmToken(),
                request.getDeviceId(),
                request.getDeviceType()
            );

            log.info("[API] FCM 토큰 등록 성공 - tokenId: {}", tokenInfo.getId());

            return ResponseEntity.ok(
                ApiResponse.success(
                    tokenInfo,
                    "FCM 토큰이 성공적으로 등록되었습니다.",
                    HttpStatus.OK
                )
            );

        } catch (Exception e) {
            log.error("[API] FCM 토큰 등록 실패 - userId: {}, error: {}",
                request.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * FCM 토큰 업데이트
     */
    @PutMapping("/update")
    public ResponseEntity<ApiResponse<DeviceTokenResponseDto>> updateToken(
        @Valid
        @RequestBody
        DeviceTokenUpdateRequestDto request
    ) {
        log.info("[API] FCM 토큰 업데이트 요청 - userId: {}, deviceId: {}",
            request.getUserId(), request.getDeviceId());

        try {
            UserDeviceInfo tokenInfo = userDeviceTokenUseCase.updateToken(
                request.getUserId(),
                request.getFcmToken(),
                request.getDeviceId(),
                request.getDeviceType()
            );

            DeviceTokenResponseDto response = toDeviceTokenResponseDto(tokenInfo);

            log.info("[API] FCM 토큰 업데이트 성공 - tokenId: {}", response.getId());

            return ResponseEntity.ok(
                ApiResponse.success(response, "FCM 토큰이 성공적으로 업데이트되었습니다.", HttpStatus.OK)
            );

        } catch (Exception e) {
            log.error("[API] FCM 토큰 업데이트 실패 - userId: {}, error: {}",
                request.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 사용자의 활성화된 FCM 토큰 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<DeviceTokenListResponseDto>> getActiveTokensByUserId(
        @ValidUuidV7
        @PathVariable("userId")
        String userId
    ) {
        log.info("[API] FCM 토큰 목록 조회 요청 - userId: {}", userId);

        try {
            List<UserDeviceInfo> tokens = userDeviceTokenUseCase.getActiveTokensByUserId(
                userId);

            List<DeviceTokenResponseDto> tokenDtos = tokens.stream()
                .map(this::toDeviceTokenResponseDto)
                .collect(Collectors.toList());

            DeviceTokenListResponseDto response = DeviceTokenListResponseDto.builder()
                .tokens(tokenDtos)
                .totalCount(tokenDtos.size())
                .build();

            log.info("[API] FCM 토큰 목록 조회 성공 - userId: {}, count: {}", userId, tokenDtos.size());

            return ResponseEntity.ok(
                ApiResponse.success(response, "FCM 토큰 목록 조회에 성공했습니다.", HttpStatus.OK)
            );

        } catch (Exception e) {
            log.error("[API] FCM 토큰 목록 조회 실패 - userId: {}, error: {}",
                userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * FCM 토큰 삭제
     */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteToken(
        @Valid
        @RequestBody
        DeviceTokenDeleteRequestDto request
    ) {
        log.info("[API] FCM 토큰 삭제 요청 - fcmToken: {}", request.getFcmToken());

        try {
            userDeviceTokenUseCase.deleteToken(request.getFcmToken());

            log.info("[API] FCM 토큰 삭제 성공 - fcmToken: {}", request.getFcmToken());

            return ResponseEntity.ok(
                ApiResponse.success(
                    null,
                    "FCM 토큰이 성공적으로 삭제되었습니다.",
                    HttpStatus.OK
                )
            );

        } catch (Exception e) {
            log.error("[API] FCM 토큰 삭제 실패 - fcmToken: {}, error: {}",
                request.getFcmToken(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 사용자의 모든 토큰 비활성화
     */
    @PutMapping("/deactivate/{userId}")
    public ResponseEntity<ApiResponse<Void>> deactivateAllTokens(
        @ValidUuidV7
        @PathVariable("userId")
        String userId
    ) {
        log.info("[API] FCM 토큰 비활성화 요청 - userId: {}", userId);

        try {
            userDeviceTokenUseCase.deactivateAllTokens(userId);

            log.info("[API] FCM 토큰 비활성화 성공 - userId: {}", userId);

            return ResponseEntity.ok(
                ApiResponse.success(
                    null,
                    "사용자의 모든 FCM 토큰이 성공적으로 비활성화되었습니다.",
                    HttpStatus.OK
                )
            );

        } catch (Exception e) {
            log.error("[API] FCM 토큰 비활성화 실패 - userId: {}, error: {}",
                userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Domain 모델을 DTO로 변환
     */
    private DeviceTokenResponseDto toDeviceTokenResponseDto(UserDeviceInfo tokenInfo) {
        return DeviceTokenResponseDto.builder()
            .id(tokenInfo.getId() != null ? tokenInfo.getId().toString() : null)
            .userId(tokenInfo.getUserId() != null ? tokenInfo.getUserId().toString() : null)
            .fcmToken(tokenInfo.getFcmToken())
            .deviceId(tokenInfo.getDeviceId())
            .deviceType(tokenInfo.getDeviceType().toString())
            .isActive(tokenInfo.getIsActive())
            .createdAt(tokenInfo.getCreatedAt())
            .updatedAt(tokenInfo.getUpdatedAt())
            .build();
    }
}

