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
    private static final String ADMIN_USER_ID = "019a357e-bd3f-6aae-85ec-eaa006ba1e13"; // fixed UUID v7

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
        User admin = User.createWithExternalId(
                ADMIN_USER_ID,
                ADMIN_LOGIN_EMAIL,
                ADMIN_LOGIN_EMAIL,
                encodedPassword,
                UserRole.ALL_ADMIN
        );
        admin.updateContactEmail(ADMIN_LOGIN_EMAIL);
        admin.updatePassword(encodedPassword, LocalDateTime.now());
        userRepository.save(admin);

        log.info("[INFO] 테스트용 ADMIN 계정이 생성되었습니다. {}", ADMIN_LOGIN_EMAIL);
    }

    private void seedModuleAccounts() {
        log.info("[INFO] 테스트용 계정이 생성되었습니다. {}", ADMIN_LOGIN_EMAIL);
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
            String fixedId = ACCOUNT_IDS.get(account.loginEmail);
            User user = (fixedId != null)
                    ? User.createWithExternalId(fixedId, account.contactEmail, account.loginEmail, encodedPassword, account.role)
                    : User.create(account.loginEmail, encodedPassword, account.role);
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

    private static final java.util.Map<String, String> ACCOUNT_IDS = java.util.Map.ofEntries(
            java.util.Map.entry("mm-user@everp.com", "019a357e-bd3f-3d22-ace8-7b339c303573"),
            java.util.Map.entry("mm-admin@everp.com", "019a357e-bd3f-1015-0ef6-c9a4ee00c9c4"),
            java.util.Map.entry("sd-user@everp.com", "019a357e-bd3f-1385-2ed7-d8661e4f4608"),
            java.util.Map.entry("sd-admin@everp.com", "019a357e-bd3f-61f7-0f2a-4cac7ee80614"),
            java.util.Map.entry("im-user@everp.com", "019a357e-bd3f-c6a1-12c3-b1d747113cf2"),
            java.util.Map.entry("im-admin@everp.com", "019a357e-bd3f-2511-95fb-838c11aee27a"),
            java.util.Map.entry("fcm-user@everp.com", "019a357e-bd3f-c1f8-2d52-960d438eaccf"),
            java.util.Map.entry("fcm-admin@everp.com", "019a357e-bd3f-c724-2ef4-c8b96914e35f"),
            java.util.Map.entry("hrm-user@everp.com", "019a357e-bd3f-d1d8-afff-3b26eec14ab5"),
            java.util.Map.entry("hrm-admin@everp.com", "019a357e-bd3f-7150-d6a7-8533d5876769"),
            java.util.Map.entry("pp-user@everp.com", "019a357e-bd3f-c8b0-3723-da17d92cfd5f"),
            java.util.Map.entry("pp-admin@everp.com", "019a357e-bd3f-ef34-2deb-286b7a1ecec0"),
            java.util.Map.entry("customer-user@everp.com", "019a357e-bd3f-90d0-ab33-eba47eedb4f8"),
            java.util.Map.entry("customer-admin@everp.com", "019a357e-bd3f-6f9b-5b64-20e2abea5672"),
            java.util.Map.entry("supplier-user@everp.com", "019a357e-bd3f-7e3c-bb84-bd48e86f70e6"),
            java.util.Map.entry("supplier-admin@everp.com", "019a357e-bd3f-896d-5529-79ee4bf65f41")
    );
}
