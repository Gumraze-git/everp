package org.ever._4ever_be_auth.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.ever._4ever_be_auth.user.application.port.in.AuthUserSagaPort;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.ever._4ever_be_auth.user.util.TemporaryPasswordGenerator;
import org.ever.event.CreateAuthUserEvent;
import org.ever.event.CreateAuthUserResultEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserSagaService implements AuthUserSagaPort {

    private static final int TEMP_PASSWORD_LENGTH = 10;

    private static final Map<String, String> DEPARTMENT_ROLE_PREFIX = Map.of(
            "DEPT-001", "MM",
            "DEPT-002", "SD",
            "DEPT-003", "IM",
            "DEPT-004", "FCM",
            "DEPT-005", "HRM",
            "DEPT-006", "PP"
    );


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordGenerator passwordGenerator;
    private final SagaRollbackService sagaRollbackService;


    @Override
    @Transactional
    public CreateAuthUserResultEvent handleCreateAuthUser(CreateAuthUserEvent event) {
        try {
            // userId
            String externalUserId = event.getUserId();

            // loginEmail 설정
            String loginEmail = buildLoginEmail(event.getEmail());
            validateDuplicate(loginEmail);      // 로그인 이메일 검증

            // 비밀번호 설정
            String temporaryPassword = passwordGenerator.generate(TEMP_PASSWORD_LENGTH);
            String encodedPassword = passwordEncoder.encode(temporaryPassword);

            // 사용자 역할 mapping
            UserRole userRole = resolveUserRole(event.getDepartmentCode(), event.getPositionCode());

            User user = User.createWithExternalId(
                    externalUserId,
                    event.getEmail(),
                    loginEmail,
                    encodedPassword,
                    userRole
            );

            userRepository.save(user);

            log.info("[SAGA][SUCCESS] 로그인 계정 생성 완료 - transactionId: {}",
                    event.getTransactionId());

            return CreateAuthUserResultEvent.builder()
                    .eventId(event.getEventId())
                    .transactionId(event.getTransactionId())
                    .success(true)
                    .userId(user.getUserId())
                    .build();
        } catch (Exception error) {
            log.error("[SAGA][FAIL] 로그인 계정 생성 실패 - transactionId: {}, cause: {}", event.getTransactionId(), error.getMessage(), error);
            sagaRollbackService.rollbackUser(event.getUserId(), event.getTransactionId());
            return CreateAuthUserResultEvent.builder()
                    .eventId(event.getEventId())
                    .transactionId(event.getTransactionId())
                    .success(false)
                    .failureReason(error.getMessage())
                    .build();
        }

    }

    private UserRole resolveUserRole(String departmentCode, String positionCode) {
        if (positionCode == null || positionCode.length() < 3) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_LOGIC_ERROR,
                    "[SAGA] positionCode 형식이 올바르지 않습니다."
            );
        }
        String numericPart = positionCode.substring(positionCode.length() - 3);

        int positionNumber;
        try {
            positionNumber = Integer.parseInt(numericPart);
        } catch (NumberFormatException error) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_LOGIC_ERROR,
                    "[SAGA] positionCode 숫자 변환에 실패했습니다."
            );
        }

        if (positionNumber >= 7 && positionNumber <= 10) {
            return UserRole.CEO_ADMIN;
        }

        String prefix = DEPARTMENT_ROLE_PREFIX.get(departmentCode);
        if (prefix == null) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_LOGIC_ERROR,
                    "[SAGA] 매핑되지 않은 departmentCode 입니다."
            );
        }

        String suffix = (positionNumber >= 4 && positionNumber <= 6) ? "ADMIN" : "USER";
        String roleName = prefix + "_" + suffix;

        try {
            return UserRole.valueOf(roleName);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(
                    ErrorCode.BUSINESS_LOGIC_ERROR,
                    "[SAGA] 정의되지 않은 사용자 역할입니다: " + roleName
            );
        }


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
        String prefix = email.substring(0, email.indexOf("@"));
        return prefix + "@everp.com";
    }
}