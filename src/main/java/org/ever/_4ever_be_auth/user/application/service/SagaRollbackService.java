package org.ever._4ever_be_auth.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SagaRollbackService {

    private final UserRepository userRepository;

    @Transactional
    public void rollbackUser(String userId, String transactionId) {
        if (userId == null || userId.isBlank()) {
            log.warn("[SAGA][ROLLBACK] userId가 없어 보상 스킵 - txId: {}", transactionId);
            return;
        }

        boolean exists = userRepository.existsByUserId(userId);
        if (!exists) {
            log.info("[SAGA][ROLLBACK] 이미 삭제된 사용자 - txId: {}, userId: {}", transactionId, userId);
            return;
        }

        userRepository.deleteByUserId(userId);
        log.warn("[SAGA][ROLLBACK] 로그인 계정 삭제 완료 - txId: {}, userId: {}", transactionId, userId);
    }
}