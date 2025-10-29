package org.ever._4ever_be_auth.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private static final String ADMIN_LOGIN_EMAIL = "admin@everp.com";
    private static final String ADMIN_PASSWORD = "password@Admin";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        createAdminIfAbsent();
        seedModuleAccounts();
    }

    private void createAdminIfAbsent() {
        if (userRepository.existsByLoginEmail(ADMIN_LOGIN_EMAIL)) {
            return;
        }
        String encodedPassword = passwordEncoder.encode(ADMIN_PASSWORD);
        User admin = User.create(ADMIN_LOGIN_EMAIL, encodedPassword, UserRole.ALL_ADMIN);
        admin.updateContactEmail(ADMIN_LOGIN_EMAIL);
        admin.updatePassword(encodedPassword, LocalDateTime.now());
        userRepository.save(admin);

        log.info("Test admin user created: {}", ADMIN_LOGIN_EMAIL);
    }

    private void seedModuleAccounts() {
        List<ModuleAccount> accounts = List.of(
                moduleAccount("mm-user@everp.com", UserRole.MM_USER, "password@MMUser1"),
                moduleAccount("mm-admin@everp.com", UserRole.MM_ADMIN, "password@MMAdmin1"),
                moduleAccount("sd-user@everp.com", UserRole.SD_USER, "password@SDUser1"),
                moduleAccount("sd-admin@everp.com", UserRole.SD_ADMIN, "password@SDAdmin1"),
                moduleAccount("im-user@everp.com", UserRole.IM_USER, "password@IMUser1"),
                moduleAccount("im-admin@everp.com", UserRole.IM_ADMIN, "password@IMAdmin1"),
                moduleAccount("fcm-user@everp.com", UserRole.FCM_USER, "password@FCMUser1"),
                moduleAccount("fcm-admin@everp.com", UserRole.FCM_ADMIN, "password@FCMAdmin1"),
                moduleAccount("hrm-user@everp.com", UserRole.HRM_USER, "password@HRMUser1"),
                moduleAccount("hrm-admin@everp.com", UserRole.HRM_ADMIN, "password@HRMAdmin1"),
                moduleAccount("pp-user@everp.com", UserRole.PP_USER, "password@PPUser1"),
                moduleAccount("pp-admin@everp.com", UserRole.PP_ADMIN, "password@PPAdmin1")
        );

        for (ModuleAccount account : accounts) {
            if (userRepository.existsByLoginEmail(account.loginEmail)) {
                continue;
            }

            String encodedPassword = passwordEncoder.encode(account.rawPassword);
            User user = User.create(account.loginEmail, encodedPassword, account.role);
            user.updateContactEmail(account.contactEmail);
            user.updatePassword(encodedPassword, LocalDateTime.now());
            userRepository.save(user);

            log.info("Seeded mock account: {} ({})", account.loginEmail, account.role());
        }
    }

    private ModuleAccount moduleAccount(String email, UserRole role, String rawPassword) {
        return new ModuleAccount(email, email, rawPassword, role);
    }

    private record ModuleAccount(String loginEmail, String contactEmail, String rawPassword, UserRole role) {
    }
}
