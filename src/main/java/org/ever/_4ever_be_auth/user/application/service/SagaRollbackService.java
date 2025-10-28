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
        log.warn("[SAGA][ROLLBACK] 로그인 계정 삭제 - txId: {}, userId: {}",
                transactionId, userId);
        userRepository.deleteUserByUserId(userId);
    }
}