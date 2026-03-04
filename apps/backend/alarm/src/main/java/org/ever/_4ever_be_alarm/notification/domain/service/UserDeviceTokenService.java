package org.ever._4ever_be_alarm.notification.domain.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.notification.domain.model.UserDeviceInfo;
import org.ever._4ever_be_alarm.notification.domain.model.constants.DeviceTypeEnum;
import org.ever._4ever_be_alarm.notification.domain.port.in.UserDeviceTokenUseCase;
import org.ever._4ever_be_alarm.notification.domain.port.out.UserDeviceTokenRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 디바이스 토큰 관리 서비스
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDeviceTokenService implements UserDeviceTokenUseCase {

    private final UserDeviceTokenRepositoryPort userDeviceTokenRepository;

    @Transactional
    @Override
    public UserDeviceInfo registerToken(
        String userId,
        String fcmToken,
        String deviceId,
        String deviceType
    ) {
        log.info("[TOKEN-REGISTER] 토큰 등록 시작 - userId: {}, deviceId: {}", userId, deviceId);

        try {
            UUID userUuid = UUID.fromString(userId);

            // 기존 토큰이 있는지 확인 (deviceId 기준)
            if (deviceId != null && !deviceId.isBlank()) {
                Optional<UserDeviceInfo> existingToken =
                    userDeviceTokenRepository.findByUserIdAndDeviceId(userUuid, deviceId);

                if (existingToken.isPresent()) {
                    log.info("[TOKEN-REGISTER] 기존 토큰 발견, 업데이트 수행 - tokenId: {}",
                        existingToken.get().getId());
                    return updateToken(userId, fcmToken, deviceId, deviceType);
                }
            }

            // 동일한 FCM 토큰이 이미 존재하는지 확인
            Optional<UserDeviceInfo> existingFcmToken =
                userDeviceTokenRepository.findByFcmToken(fcmToken);

            if (existingFcmToken.isPresent()) {
                log.warn("[TOKEN-REGISTER] 동일한 FCM 토큰이 이미 존재 - fcmToken: {}", fcmToken);
                // 기존 토큰 반환 또는 업데이트
                return existingFcmToken.get();
            }

            // 신규 토큰 생성
            UserDeviceInfo newToken = UserDeviceInfo.builder()
                .userId(userUuid)
                .fcmToken(fcmToken)
                .deviceId(deviceId)
                .deviceType(DeviceTypeEnum.fromString(deviceType))
                .isActive(true)
                .build();

            UserDeviceInfo savedToken = userDeviceTokenRepository.save(newToken);

            log.info("[TOKEN-REGISTER] 토큰 등록 완료 - tokenId: {}", savedToken.getId());

            return savedToken;

        } catch (IllegalArgumentException e) {
            log.error("[TOKEN-REGISTER] 잘못된 userId 형식 - userId: {}", userId, e);
            throw new IllegalArgumentException("유효하지 않은 userId 형식입니다: " + userId, e);
        } catch (Exception e) {
            log.error("[TOKEN-REGISTER] 토큰 등록 실패 - userId: {}, error: {}",
                userId, e.getMessage(), e);
            throw new RuntimeException("토큰 등록 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    @Override
    public UserDeviceInfo updateToken(
        String userId,
        String fcmToken,
        String deviceId,
        String deviceType
    ) {
        log.info("[TOKEN-UPDATE] 토큰 업데이트 시작 - userId: {}, deviceId: {}", userId, deviceId);

        try {
            UUID userUuid = UUID.fromString(userId);

            // deviceId로 기존 토큰 조회
            Optional<UserDeviceInfo> existingToken;
            if (deviceId != null && !deviceId.isBlank()) {
                existingToken = userDeviceTokenRepository
                    .findByUserIdAndDeviceId(userUuid, deviceId);
            } else {
                // deviceId가 없으면 fcmToken으로 조회
                existingToken = userDeviceTokenRepository.findByFcmToken(fcmToken);
            }

            if (existingToken.isEmpty()) {
                log.warn("[TOKEN-UPDATE] 기존 토큰을 찾을 수 없음, 신규 등록 수행 - userId: {}", userId);
                return registerToken(userId, fcmToken, deviceId, deviceType);
            }

            // 기존 토큰 업데이트
            UserDeviceInfo tokenToUpdate = existingToken.get();
            UserDeviceInfo updatedToken = UserDeviceInfo.builder()
                .id(tokenToUpdate.getId())
                .userId(userUuid)
                .fcmToken(fcmToken)
                .deviceId(deviceId != null ? deviceId : tokenToUpdate.getDeviceId())
                .deviceType(deviceType != null
                    ? DeviceTypeEnum.fromString(deviceType)
                    : tokenToUpdate.getDeviceType())
                .isActive(true)
                .build();

            UserDeviceInfo savedToken = userDeviceTokenRepository.save(updatedToken);

            log.info("[TOKEN-UPDATE] 토큰 업데이트 완료 - tokenId: {}", savedToken.getId());

            return savedToken;

        } catch (IllegalArgumentException e) {
            log.error("[TOKEN-UPDATE] 잘못된 userId 형식 - userId: {}", userId, e);
            throw new IllegalArgumentException("유효하지 않은 userId 형식입니다: " + userId, e);
        } catch (Exception e) {
            log.error("[TOKEN-UPDATE] 토큰 업데이트 실패 - userId: {}, error: {}",
                userId, e.getMessage(), e);
            throw new RuntimeException("토큰 업데이트 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDeviceInfo> getActiveTokensByUserId(String userId) {
        log.debug("[TOKEN-GET] 활성화된 토큰 조회 - userId: {}", userId);

        try {
            UUID userUuid = UUID.fromString(userId);

            List<UserDeviceInfo> tokens =
                userDeviceTokenRepository.findActiveTokensByUserId(userUuid);

            log.debug("[TOKEN-GET] 활성화된 토큰 조회 완료 - userId: {}, count: {}",
                userId, tokens.size());

            return tokens;

        } catch (IllegalArgumentException e) {
            log.error("[TOKEN-GET] 잘못된 userId 형식 - userId: {}", userId, e);
            throw new IllegalArgumentException("유효하지 않은 userId 형식입니다: " + userId, e);
        } catch (Exception e) {
            log.error("[TOKEN-GET] 토큰 조회 실패 - userId: {}, error: {}",
                userId, e.getMessage(), e);
            throw new RuntimeException("토큰 조회 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    @Override
    public void deleteToken(String fcmToken) {
        log.info("[TOKEN-DELETE] 토큰 삭제 시작 - fcmToken: {}", fcmToken);

        try {
            int deletedCount = userDeviceTokenRepository.deleteByFcmToken(fcmToken);

            if (deletedCount == 0) {
                log.warn("[TOKEN-DELETE] 삭제할 토큰을 찾을 수 없음 - fcmToken: {}", fcmToken);
            } else {
                log.info("[TOKEN-DELETE] 토큰 삭제 완료 - deletedCount: {}", deletedCount);
            }

        } catch (Exception e) {
            log.error("[TOKEN-DELETE] 토큰 삭제 실패 - fcmToken: {}, error: {}",
                fcmToken, e.getMessage(), e);
            throw new RuntimeException("토큰 삭제 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    @Override
    public void deactivateAllTokens(String userId) {
        log.info("[TOKEN-DEACTIVATE] 모든 토큰 비활성화 시작 - userId: {}", userId);

        try {
            UUID userUuid = UUID.fromString(userId);

            int deactivatedCount = userDeviceTokenRepository.deactivateAllByUserId(userUuid);

            log.info("[TOKEN-DEACTIVATE] 모든 토큰 비활성화 완료 - userId: {}, deactivatedCount: {}",
                userId, deactivatedCount);

        } catch (IllegalArgumentException e) {
            log.error("[TOKEN-DEACTIVATE] 잘못된 userId 형식 - userId: {}", userId, e);
            throw new IllegalArgumentException("유효하지 않은 userId 형식입니다: " + userId, e);
        } catch (Exception e) {
            log.error("[TOKEN-DEACTIVATE] 토큰 비활성화 실패 - userId: {}, error: {}",
                userId, e.getMessage(), e);
            throw new RuntimeException("토큰 비활성화 중 오류가 발생했습니다.", e);
        }
    }
}

