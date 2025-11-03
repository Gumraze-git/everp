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
    private static final String ADMIN_USER_ID = "019a3dee-732c-79a4-916a-09336277ee92"; // fixed UUID v7

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
                moduleAccount("customer-admin@everp.com", UserRole.CUSTOMER_ADMIN),
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
            java.util.Map.entry("mm-user@everp.com", "019a3dee-8f03-77a0-92f9-e34b09e467fe"),
            java.util.Map.entry("mm-admin@everp.com", "019a3dec-a3f3-781c-986b-8c0368cb1e73"),
            java.util.Map.entry("sd-user@everp.com", "019a3dec-cf67-7ff7-9e94-b829bbd01152"),
            java.util.Map.entry("sd-admin@everp.com", "019a3e39-cbac-7773-a09b-5da7bb0ee3ec"),
            java.util.Map.entry("im-user@everp.com", "019a3dec-f1f1-7696-8195-54b87025022a"),
            java.util.Map.entry("im-admin@everp.com", "019a3ded-1748-75ea-932c-1d8ad64f75f1"),
            java.util.Map.entry("fcm-user@everp.com", "019a3ded-2a80-7104-8552-dd26c30ed45c"),
            java.util.Map.entry("fcm-admin@everp.com", "019a3ded-3c02-7294-b376-de49c32e0754"),
            java.util.Map.entry("hrm-user@everp.com", "019a3ded-4e50-7e19-aa43-eb1820cd8649"),
            java.util.Map.entry("hrm-admin@everp.com", "019a3ded-6488-795b-a1fc-31557a2b1aa5"),
            java.util.Map.entry("pp-user@everp.com", "019a3e3c-57e9-7e9a-b10f-0ee551498cae"),
            java.util.Map.entry("pp-admin@everp.com", "019a3df5-456d-7e0c-8212-388ca6118c18"),
            java.util.Map.entry("customer-admin@everp.com", "019a3e3b-5592-7541-84a9-dce035f6b424"),
            java.util.Map.entry("supplier-admin@everp.com", "019a3df1-7843-7590-a5fd-94aa9aae7d0a")
    );
}
