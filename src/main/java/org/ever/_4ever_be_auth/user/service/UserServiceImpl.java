package org.ever._4ever_be_auth.user.service;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.ever._4ever_be_auth.user.dto.CreateUserRequestDto;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String EVERP_DOMAIN = "@everp.com"; // domain 주소
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserNotificationService notificationService;

    @Override
    public User createUser(CreateUserRequestDto requestDto, UserRole requesterUserRole) {
        String contactEmail = requestDto.getContactEmail();
        String loginEmail = contactEmail.substring(0, contactEmail.indexOf("@")) + EVERP_DOMAIN;

        if (userRepository.existsByLoginEmail(loginEmail)) {
            throw new BusinessException(
                    ErrorCode.DUPLICATE_RESOURCE,
                    "이미 등록된 이메일 입니다: " + loginEmail
            );
        }

        // 초기 비밀번호 세팅
        String rawInitialPassword = generateInitialPassword();
        String encodedPassword = passwordEncoder.encode(rawInitialPassword);

        // 사용자 저장
        User savedUser = userRepository.save(
                User.create(loginEmail, encodedPassword, requestDto.getUserRole())
        );

        // 이메일 발송
        notificationService.sendUserNotification(contactEmail, loginEmail, rawInitialPassword);

        return savedUser;
    }

    private String generateInitialPassword() {
        return UUID.randomUUID().toString();
    }
}
