package org.ever._4ever_be_auth.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.ever._4ever_be_auth.user.service.UserNotificationService;
import org.ever._4ever_be_auth.user.util.TemporaryPasswordGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 내부/외부 사용자 계정 생성을 공통으로 처리하는 지원 컴포넌트.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountCreationSupport {

    private static final int TEMP_PASSWORD_LENGTH = 10;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator passwordGenerator;
    private final UserNotificationService notificationService;

    public AccountCreationResult createAccount(
            String transactionId,
            String eventId,
            String externalUserId,
            String contactEmail,
            UserRole userRole
    ) {
        String loginEmail = buildLoginEmail(contactEmail);
        validateDuplicate(loginEmail);

        String temporaryPassword = passwordGenerator.generate(TEMP_PASSWORD_LENGTH);
        String encodedPassword = passwordEncoder.encode(temporaryPassword);

        User user = User.createWithExternalId(
                externalUserId,
                contactEmail,
                loginEmail,
                encodedPassword,
                userRole
        );

        userRepository.save(user);
        log.info("[SAGA][ACCOUNT] 사용자 계정 저장 완료 - txId: {}, userId: {}", transactionId, user.getUserId());

        notificationService.sendUserNotification(contactEmail, loginEmail, temporaryPassword);
        log.info("[SAGA][ACCOUNT] 사용자 알림 발송 완료 - txId: {}", transactionId);

        return new AccountCreationResult(user, temporaryPassword);
    }

    private void validateDuplicate(String loginEmail) {
        if (userRepository.existsByLoginEmail(loginEmail)) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "[SAGA] 이미 존재하는 로그인 이메일 입니다."
            );
        }
    }

    private String buildLoginEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_LOGIC_ERROR,
                    "[SAGA] 이메일 형식이 올바르지 않습니다."
            );
        }
        String prefix = email.substring(0, email.indexOf("@"));
        return prefix + "@everp.com";
    }

    public record AccountCreationResult(User user, String temporaryPassword) {
    }
}
