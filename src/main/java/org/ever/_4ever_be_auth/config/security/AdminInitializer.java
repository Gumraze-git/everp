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
    private static final String DEFAULT_PASSWORD = "password";

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
        String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);
        User admin = User.create(ADMIN_LOGIN_EMAIL, encodedPassword, UserRole.ALL_ADMIN);
        admin.updateContactEmail(ADMIN_LOGIN_EMAIL);
        admin.updatePassword(encodedPassword, LocalDateTime.now());
        userRepository.save(admin);

        log.info("[INFO] 테스트용 ADMIN 계정이 생성되었습니다. {}", ADMIN_LOGIN_EMAIL);
    }

    private void seedModuleAccounts() {
        List<ModuleAccount> accounts = List.of(
                moduleAccount("mm-user@everp.com", UserRole.MM_USER),
                moduleAccount("mm-admin@everp.com", UserRole.MM_ADMIN),
                moduleAccount("sd-user@everp.com", UserRole.SD_USER),
                moduleAccount("sd-admin@everp.com", UserRole.SD_ADMIN),
                moduleAccount("im-user@everp.com", UserRole.IM_USER),
                moduleAccount("im-admin@everp.com", UserRole.IM_ADMIN),
                moduleAccount("fcm-user@everp.com", UserRole.FCM_USER),
                moduleAccount("fcm-admin@everp.com", UserRole.FCM_ADMIN),
                moduleAccount("hrm-user@everp.com", UserRole.HRM_USER),
                moduleAccount("hrm-admin@everp.com", UserRole.HRM_ADMIN),
                moduleAccount("pp-user@everp.com", UserRole.PP_USER),
                moduleAccount("pp-admin@everp.com", UserRole.PP_ADMIN),
                moduleAccount("customer-user@everp.com", UserRole.CUSTOMER_USER),
                moduleAccount("customer-admin@everp.com", UserRole.CUSTOMER_ADMIN),
                moduleAccount("supplier-user@everp.com", UserRole.SUPPLIER_USER),
                moduleAccount("supplier-admin@everp.com", UserRole.SUPPLIER_ADMIN)
        );

        for (ModuleAccount account : accounts) {
            if (userRepository.existsByLoginEmail(account.loginEmail)) {
                continue;
            }

            String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);
            User user = User.create(account.loginEmail, encodedPassword, account.role);
            user.updateContactEmail(account.contactEmail);
            user.updatePassword(encodedPassword, LocalDateTime.now());
            userRepository.save(user);

            log.info("Seeded mock account: {} ({})", account.loginEmail, account.role());
        }
    }

    private ModuleAccount moduleAccount(String email, UserRole role) {
        return new ModuleAccount(email, email, role);
    }

    private record ModuleAccount(String loginEmail, String contactEmail, UserRole role) {
    }
}
