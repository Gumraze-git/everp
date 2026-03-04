package org.ever._4ever_be_alarm.notification.adapter.jpa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.UserDeviceToken;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.UserDeviceTokenRepository;
import org.ever._4ever_be_alarm.notification.domain.model.UserDeviceInfo;
import org.ever._4ever_be_alarm.notification.domain.port.out.UserDeviceTokenRepositoryPort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 디바이스 토큰 JPA 어댑터
 */
@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserDeviceTokenJpaAdapter implements UserDeviceTokenRepositoryPort {

    private final UserDeviceTokenRepository userDeviceTokenRepository;

    @Override
    @Transactional
    public UserDeviceInfo save(UserDeviceInfo tokenInfo) {
        log.info("[JPA-TOKEN-SAVE] 토큰 저장 시작 - userId: {}, deviceId: {}",
            tokenInfo.getUserId(), tokenInfo.getDeviceId());

        try {
            UserDeviceToken entity = UserDeviceToken.builder()
                .id(tokenInfo.getId())
                .userId(tokenInfo.getUserId())
                .fcmToken(tokenInfo.getFcmToken())
                .deviceId(tokenInfo.getDeviceId())
                .deviceType(tokenInfo.getDeviceType())
                .isActive(tokenInfo.getIsActive())
                .build();

            UserDeviceToken savedEntity = userDeviceTokenRepository.save(entity);

            log.info("[JPA-TOKEN-SAVE] 토큰 저장 완료 - tokenId: {}", savedEntity.getId());

            return toUserDeviceTokenInfo(savedEntity);

        } catch (Exception e) {
            log.error("[JPA-TOKEN-SAVE] 토큰 저장 실패 - userId: {}, error: {}",
                tokenInfo.getUserId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<UserDeviceInfo> findActiveTokensByUserId(UUID userId) {
        log.debug("[JPA-TOKEN-FIND] 활성화된 토큰 조회 - userId: {}", userId);

        List<UserDeviceToken> tokens = userDeviceTokenRepository.findActiveTokensByUserId(userId);

        log.debug("[JPA-TOKEN-FIND] 활성화된 토큰 조회 완료 - userId: {}, count: {}", userId, tokens.size());

        return tokens.stream()
            .map(this::toUserDeviceTokenInfo)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDeviceInfo> findAllByUserId(UUID userId) {
        log.debug("[JPA-TOKEN-FIND] 모든 토큰 조회 - userId: {}", userId);

        List<UserDeviceToken> tokens = userDeviceTokenRepository.findAllByUserId(userId);

        log.debug("[JPA-TOKEN-FIND] 모든 토큰 조회 완료 - userId: {}, count: {}", userId, tokens.size());

        return tokens.stream()
            .map(this::toUserDeviceTokenInfo)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDeviceInfo> findByFcmToken(String fcmToken) {
        log.debug("[JPA-TOKEN-FIND] 토큰 조회 - fcmToken: {}", fcmToken);

        return userDeviceTokenRepository.findByFcmToken(fcmToken)
            .map(this::toUserDeviceTokenInfo);
    }

    @Override
    public Optional<UserDeviceInfo> findByUserIdAndDeviceId(UUID userId, String deviceId) {
        log.debug("[JPA-TOKEN-FIND] 토큰 조회 - userId: {}, deviceId: {}", userId, deviceId);

        return userDeviceTokenRepository.findByUserIdAndDeviceId(userId, deviceId)
            .map(this::toUserDeviceTokenInfo);
    }

    @Override
    @Transactional
    public int deleteByFcmToken(String fcmToken) {
        log.info("[JPA-TOKEN-DELETE] 토큰 삭제 - fcmToken: {}", fcmToken);

        int deletedCount = userDeviceTokenRepository.deleteByFcmToken(fcmToken);

        log.info("[JPA-TOKEN-DELETE] 토큰 삭제 완료 - deletedCount: {}", deletedCount);

        return deletedCount;
    }

    @Override
    @Transactional
    public int deactivateAllByUserId(UUID userId) {
        log.info("[JPA-TOKEN-DEACTIVATE] 모든 토큰 비활성화 - userId: {}", userId);

        int deactivatedCount = userDeviceTokenRepository.deactivateAllByUserId(userId);

        log.info("[JPA-TOKEN-DEACTIVATE] 모든 토큰 비활성화 완료 - deactivatedCount: {}", deactivatedCount);

        return deactivatedCount;
    }

    /**
     * Entity를 Domain 모델로 변환
     */
    private UserDeviceInfo toUserDeviceTokenInfo(UserDeviceToken entity) {
        return UserDeviceInfo.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .fcmToken(entity.getFcmToken())
            .deviceId(entity.getDeviceId())
            .deviceType(entity.getDeviceType())
            .isActive(entity.getIsActive())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }
}

