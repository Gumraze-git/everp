package org.ever._4ever_be_auth.user.service;

import org.ever._4ever_be_auth.user.dto.CreateUserRequestDto;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserNotificationService notificationService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, notificationService);
    }

    @DisplayName("createUser시 생성한 로그인 이메일과 초기 비밀번호를 contactEmail로 발송함.")
    @Test
    void createUser_withPermission_sendsInitialPasswordAndEmail() {
        // given
        CreateUserRequestDto requestDto = new CreateUserRequestDto(
                "mmUser@email.com",
                UserRole.MM_USER
        );

        when(userRepository.save(any(User.class))).thenReturn(mock(User.class));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        userService.createUser(requestDto, UserRole.HRM_ADMIN);

        // then
        verify(notificationService).sendUserNotification(
                eq("mmUser@email.com"),
                eq("mmUser@everp.com"),
                anyString()
        );
    }

}
