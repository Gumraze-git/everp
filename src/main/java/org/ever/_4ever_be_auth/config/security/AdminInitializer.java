package org.ever._4ever_be_auth.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        log.info("[INFO] 테스트용 모듈 계정 시드를 시작합니다.");
        List<ModuleAccount> accounts = new ArrayList<>();

        // base accounts
        accounts.add(moduleAccount("mm-user@everp.com", UserRole.MM_USER));
        accounts.add(moduleAccount("mm-admin@everp.com", UserRole.MM_ADMIN));
        accounts.add(moduleAccount("sd-user@everp.com", UserRole.SD_USER));
        accounts.add(moduleAccount("sd-admin@everp.com", UserRole.SD_ADMIN));
        accounts.add(moduleAccount("im-user@everp.com", UserRole.IM_USER));
        accounts.add(moduleAccount("im-admin@everp.com", UserRole.IM_ADMIN));
        accounts.add(moduleAccount("fcm-user@everp.com", UserRole.FCM_USER));
        accounts.add(moduleAccount("fcm-admin@everp.com", UserRole.FCM_ADMIN));
        accounts.add(moduleAccount("hrm-user@everp.com", UserRole.HRM_USER));
        accounts.add(moduleAccount("hrm-admin@everp.com", UserRole.HRM_ADMIN));
        accounts.add(moduleAccount("pp-user@everp.com", UserRole.PP_USER));
        accounts.add(moduleAccount("pp-admin@everp.com", UserRole.PP_ADMIN));
        accounts.add(moduleAccount("customer-admin@everp.com", UserRole.CUSTOMER_ADMIN));
        accounts.add(moduleAccount("supplier-admin@everp.com", UserRole.SUPPLIER_ADMIN));

        // additional admins
        addRange(accounts, "admin", 1, 5, UserRole.ALL_ADMIN);

        // module-specific ranges
        addRange(accounts, "mm-user", 1, 10, UserRole.MM_USER);
        addRange(accounts, "mm-admin", 1, 2, UserRole.MM_ADMIN);

        addRange(accounts, "sd-user", 1, 10, UserRole.SD_USER);
        addRange(accounts, "sd-admin", 1, 2, UserRole.SD_ADMIN);

        addRange(accounts, "im-user", 1, 10, UserRole.IM_USER);
        addRange(accounts, "im-admin", 1, 2, UserRole.IM_ADMIN);

        addRange(accounts, "pp-user", 1, 10, UserRole.PP_USER);
        addRange(accounts, "pp-admin", 1, 2, UserRole.PP_ADMIN);

        addRange(accounts, "fcm-user", 1, 10, UserRole.FCM_USER);
        addRange(accounts, "fcm-admin", 1, 2, UserRole.FCM_ADMIN);

        addRange(accounts, "hrm-user", 1, 10, UserRole.HRM_USER);
        addRange(accounts, "hrm-admin", 1, 2, UserRole.HRM_ADMIN);

        addRange(accounts, "customer-admin", 1, 15, UserRole.CUSTOMER_ADMIN);
        addRange(accounts, "supplier-admin", 1, 15, UserRole.SUPPLIER_ADMIN);

        for (ModuleAccount account : accounts) {
            if (userRepository.existsByLoginEmail(account.loginEmail)) {
                continue;
            }

            String encodedPassword = passwordEncoder.encode(DEFAULT_PASSWORD);
            String fixedId = ACCOUNT_IDS.get(account.loginEmail);
            try {
                User user = (fixedId != null)
                        ? User.createWithExternalId(fixedId, account.contactEmail, account.loginEmail, encodedPassword, account.role)
                        : User.create(account.loginEmail, encodedPassword, account.role);
                user.updateContactEmail(account.contactEmail);
                user.updatePassword(encodedPassword, LocalDateTime.now());
                userRepository.save(user);
                log.info("Seeded mock account: {} ({})", account.loginEmail, account.role());
            } catch (DataIntegrityViolationException e) {
                log.warn("Skip seeding {} due to constraint violation (possibly duplicate user_id): {}", account.loginEmail, e.getMessage());
            } catch (Exception e) {
                log.warn("Failed to seed {}: {}", account.loginEmail, e.getMessage());
            }
        }
    }

    private ModuleAccount moduleAccount(String email, UserRole role) {
        return new ModuleAccount(email, email, role);
    }

    private void addRange(List<ModuleAccount> accounts, String prefix, int startInclusive, int endInclusive, UserRole role) {
        for (int i = startInclusive; i <= endInclusive; i++) {
            String email = prefix + i + "@everp.com";
            accounts.add(moduleAccount(email, role));
        }
    }

    private record ModuleAccount(String loginEmail, String contactEmail, UserRole role) {
    }

    private static final Map<String, String> ACCOUNT_IDS = java.util.Map.ofEntries(
            Map.entry("mm-user@everp.com", "019a3dee-8f03-77a0-92f9-e34b09e467fe"),
            Map.entry("mm-admin@everp.com", "019a3dec-a3f3-781c-986b-8c0368cb1e73"),
            Map.entry("sd-user@everp.com", "019a3dec-cf67-7ff7-9e94-b829bbd01152"),
            Map.entry("sd-admin@everp.com", "019a3e39-cbac-7773-a09b-5da7bb0ee3ec"),
            Map.entry("im-user@everp.com", "019a3dec-f1f1-7696-8195-54b87025022a"),
            Map.entry("im-admin@everp.com", "019a3ded-1748-75ea-932c-1d8ad64f75f1"),
            Map.entry("fcm-user@everp.com", "019a3ded-2a80-7104-8552-dd26c30ed45c"),
            Map.entry("fcm-admin@everp.com", "019a3ded-3c02-7294-b376-de49c32e0754"),
            Map.entry("hrm-user@everp.com", "019a3ded-4e50-7e19-aa43-eb1820cd8649"),
            Map.entry("hrm-admin@everp.com", "019a3ded-6488-795b-a1fc-31557a2b1aa5"),
            Map.entry("pp-user@everp.com", "019a3e3c-57e9-7e9a-b10f-0ee551498cae"),
            Map.entry("pp-admin@everp.com", "019a3df5-456d-7e0c-8212-388ca6118c18"),
            Map.entry("customer-admin@everp.com", "019a3e3b-5592-7541-84a9-dce035f6b424"),
            Map.entry("supplier-admin@everp.com", "019a3df1-7843-7590-a5fd-94aa9aae7d0a"),

            // Admin series
            Map.entry("admin1@everp.com", "019a52bb-e6f9-7ff8-beb5-e1dfe4a99bf0"),
            Map.entry("admin2@everp.com", "019a52bc-be29-7234-a87e-a1b522a324c7"),
            Map.entry("admin3@everp.com", "019a52bd-69de-70a7-af61-8afcd6057b13"),
            Map.entry("admin4@everp.com", "019a52bd-8fc8-7615-a06f-b6e562b26e0f"),
            Map.entry("admin5@everp.com", "019a52bd-b49b-771e-97de-642d4b8d852a"),

            // MM-USER series
            Map.entry("mm-user1@everp.com", "019a52be-3ad5-7a10-aef9-6ca859ea2d95"),
            Map.entry("mm-user2@everp.com", "019a52be-5e77-7335-b930-b855d88d6c33"),
            Map.entry("mm-user3@everp.com", "019a52be-80fb-71de-aa69-717e26090e36"),
            Map.entry("mm-user4@everp.com", "019a52be-a19b-7be4-b123-dde7c302395d"),
            Map.entry("mm-user5@everp.com", "019a52be-c350-7566-ab77-d643a50c7fc9"),
            Map.entry("mm-user6@everp.com", "019a52d8-5bc6-71ec-8be5-768ae151969c"),
            Map.entry("mm-user7@everp.com", "019a52d8-639c-798e-ab64-280f2069dc1d"),
            Map.entry("mm-user8@everp.com", "019a52d8-6b63-7dda-ac62-6d92332577cd"),
            Map.entry("mm-user9@everp.com", "019a52d8-78a9-75fc-9a49-8dafb8e14c71"),
            Map.entry("mm-user10@everp.com", "019a52d8-8127-746d-9352-f414a6c75658"),

            // MM-ADMIN series
            Map.entry("mm-admin1@everp.com", "019a52bf-01a4-797d-bf90-440ca765d9ec"),
            Map.entry("mm-admin2@everp.com", "019a52bf-2bf1-7c13-a9c2-bb4465f22f40"),

            // SD-USER series
            Map.entry("sd-user1@everp.com", "019a52bf-9840-7fc0-ae40-02ab9ef57d60"),
            Map.entry("sd-user2@everp.com", "019a52c0-1094-70b0-abec-c6cb87740dc3"),
            Map.entry("sd-user3@everp.com", "019a52bf-ff06-7a4d-bf13-d709aea60d7e"),
            Map.entry("sd-user4@everp.com", "019a52c0-2095-7aa1-83df-767b75834a92"),
            Map.entry("sd-user5@everp.com", "019a52c0-2910-7294-8d54-a609cef8307d"),
            Map.entry("sd-user6@everp.com", "019a52d8-d0e4-708d-843d-dcec3c160374"),
            Map.entry("sd-user7@everp.com", "019a52d8-dd65-7acf-9d20-73b5b649f808"),
            Map.entry("sd-user8@everp.com", "019a52d8-e540-792b-8201-0a3e7a680a3d"),
            Map.entry("sd-user9@everp.com", "019a52d8-ee39-712d-bdde-1a6d3120e2b9"),
            Map.entry("sd-user10@everp.com", "019a52d8-fb67-7377-82a3-1468fcc1b271"),

            // SD-ADMIN series
            Map.entry("sd-admin1@everp.com", "019a52c0-c245-7546-b630-37c8457d85b4"),
            Map.entry("sd-admin2@everp.com", "019a52c0-cf94-7ae7-a43f-90b6a97364e9"),

            // IM-USER series
            Map.entry("im-user1@everp.com", "019a52d5-5e17-712d-bbcd-8370bd0ca391"),
            Map.entry("im-user2@everp.com", "019a52d5-76fa-735a-934e-9a1dd0d185ef"),
            Map.entry("im-user3@everp.com", "019a52d5-8745-7e8c-b9bd-6c36fc2b7380"),
            Map.entry("im-user4@everp.com", "019a52d5-9762-7b76-97f7-5cc7eb98d27c"),
            Map.entry("im-user5@everp.com", "019a52d5-a31a-739e-8b06-0f2db993e4a7"),
            Map.entry("im-user6@everp.com", "019a52da-c705-7a7f-aabc-0ebd68168066"),
            Map.entry("im-user7@everp.com", "019a52da-d4aa-7d18-ac5c-1d5ff8c5283b"),
            Map.entry("im-user8@everp.com", "019a52da-e211-7d26-a163-209bd7ada5a8"),
            Map.entry("im-user9@everp.com", "019a52da-eeb9-7fab-bd64-0661cf80b50b"),
            Map.entry("im-user10@everp.com", "019a52da-f91b-7b76-a0db-f52e38df0cc0"),

            // IM-ADMIN series
            Map.entry("im-admin1@everp.com", "019a52d5-af6a-76a9-907a-362be9673e6d"),
            Map.entry("im-admin2@everp.com", "019a52d5-df43-70df-bc4f-99936ae897ea"),

            // PP-USER series
            Map.entry("pp-user1@everp.com", "019a52d5-ef2e-75e5-8705-02fc494ec05d"),
            Map.entry("pp-user2@everp.com", "019a52d6-000d-7881-bd1c-741b7f757e51"),
            Map.entry("pp-user3@everp.com", "019a52d6-116b-7746-bac4-c9dff44842ec"),
            Map.entry("pp-user4@everp.com", "019a52d6-1f6f-789b-81a8-541c084cb8b2"),
            Map.entry("pp-user5@everp.com", "019a52d6-3b39-7ca7-b48a-77213d5b34ca"),
            Map.entry("pp-user6@everp.com", "019a52da-8b88-724a-a00b-edbcbb5a6bdb"),
            Map.entry("pp-user7@everp.com", "019a52da-95d5-7c64-af1c-90f3a9ec6780"),
            Map.entry("pp-user8@everp.com", "019a52da-9f1f-73f4-a11e-fc4dc98c7f19"),
            Map.entry("pp-user9@everp.com", "019a52da-a78e-783d-a8f5-4dec476045f7"),
            Map.entry("pp-user10@everp.com", "019a52da-b52a-7a82-a8c9-b59300ebe934"),

            // PP-ADMIN series
            Map.entry("pp-admin1@everp.com", "019a52d6-57cc-73db-b64c-a9140a6f5e7a"),
            Map.entry("pp-admin2@everp.com", "019a52d6-6668-7d66-b163-a1299a143320"),

            // FCM-USER series
            Map.entry("fcm-user1@everp.com", "019a52d7-fd5c-7f93-b976-e1cab493ad5d"),
            Map.entry("fcm-user2@everp.com", "019a52d7-f3db-7e46-9ebb-41c895fd0a02"),
            Map.entry("fcm-user3@everp.com", "019a52d7-e993-75bf-b7af-1f1f33394322"),
            Map.entry("fcm-user4@everp.com", "019a52d7-dc73-793d-b112-ab40e4503d45"),
            Map.entry("fcm-user5@everp.com", "019a52d7-d010-7803-9a53-745a7af9b8f0"),
            Map.entry("fcm-user6@everp.com", "019a52da-6310-7cd6-8973-57727c3776e4"),
            Map.entry("fcm-user7@everp.com", "019a52da-53c0-7104-9a05-05ea1b2f4872"),
            Map.entry("fcm-user8@everp.com", "019a52da-4025-7bea-bc0c-524d5b25d175"),
            Map.entry("fcm-user9@everp.com", "019a52da-332a-77f5-8afa-23068523a817"),
            Map.entry("fcm-user10@everp.com", "019a52da-15b6-7c03-8ac8-010aeb31baeb"),

            // FCM-ADMIN series
            Map.entry("fcm-admin1@everp.com", "019a52d7-bca2-726a-af39-09464173a738"),
            Map.entry("fcm-admin2@everp.com", "019a52d7-c592-7d18-bf5c-5ae121f492ec"),

            // HRM-USER series
            Map.entry("hrm-user1@everp.com", "019a52d7-b0d1-7f82-bed7-3839a154834f"),
            Map.entry("hrm-user2@everp.com", "019a52d7-a4b4-7782-bfa9-8fdf879092b6"),
            Map.entry("hrm-user3@everp.com", "019a52d7-9b83-7f02-ae6c-5ccf52f0791a"),
            Map.entry("hrm-user4@everp.com", "019a52d7-92fd-7e16-a131-7dcfb5f76a09"),
            Map.entry("hrm-user5@everp.com", "019a52d7-8aae-7430-988a-3859b8644e54"),
            Map.entry("hrm-user6@everp.com", "019a52da-15b6-7c03-8ac8-010aeb31baeb"),
            Map.entry("hrm-user7@everp.com", "019a52da-15b6-7c03-8ac8-010aeb31baeb"),
            Map.entry("hrm-user8@everp.com", "019a52da-0b93-73bd-9f4b-b9e8118dca67"),
            Map.entry("hrm-user9@everp.com", "019a52da-021d-7ce6-bee2-4f3d839fb6b0"),
            Map.entry("hrm-user10@everp.com", "019a52d9-f141-7bec-819d-6bd1ab7ed767"),

            // HRM-ADMIN series
            Map.entry("hrm-admin1@everp.com", "019a52d7-7451-7176-b163-9105089f0e1d"),
            Map.entry("hrm-admin2@everp.com", "019a52d7-7d97-78e6-aa10-e91b4b3d06d8"),

            // CUSTOMER series
            Map.entry("customer-admin1@everp.com", "019a52d6-c565-717d-b4f2-2ae71bf8c141"),
            Map.entry("customer-admin2@everp.com", "019a52d6-dce0-72e1-acab-c4542f8aa8dc"),
            Map.entry("customer-admin3@everp.com", "019a52d6-ecae-7478-8473-34df816d9918"),
            Map.entry("customer-admin4@everp.com", "019a52d6-f565-7d1d-a32b-d93661301b45"),
            Map.entry("customer-admin5@everp.com", "019a52d6-fdd0-7f9a-806e-4adfe131d36e"),
            Map.entry("customer-admin6@everp.com", "019a52d7-07a5-7666-9d11-b9c5a668c590"),
            Map.entry("customer-admin7@everp.com", "019a52d7-1515-754d-9729-69dd82cf3e65"),
            Map.entry("customer-admin8@everp.com", "019a52d7-1ed7-7724-a43c-50bad4b5ad29"),
            Map.entry("customer-admin9@everp.com", "019a52d7-2881-7e9e-8c98-2dbdd1f1d4cb"),
            Map.entry("customer-admin10@everp.com", "019a52d7-3203-76b6-96df-18d8e70640aa"),
            Map.entry("customer-admin11@everp.com", "019a52d7-3a56-7a66-9176-83cf9d52cf9e"),
            Map.entry("customer-admin12@everp.com", "019a52d7-4647-7eb5-b1f2-1ef4527a01d5"),
            Map.entry("customer-admin13@everp.com", "019a52d7-4f29-73ab-878f-89c55ca58c11"),
            Map.entry("customer-admin14@everp.com", "019a52d7-5e0c-7c25-8ef1-f56ab80605ac"),
            Map.entry("customer-admin15@everp.com", "019a52d5-47b1-7226-8003-4982850a95be"),

            // SUPPLIER series
            Map.entry("supplier-admin1@everp.com", "019a52d5-2824-7c7e-9826-e5c56987d189"),
            Map.entry("supplier-admin2@everp.com", "019a52d5-2824-7c7e-9826-e5c56987d189"),
            Map.entry("supplier-admin3@everp.com", "019a52d5-1ad8-754d-af67-e541f85473c4"),
            Map.entry("supplier-admin4@everp.com", "019a52d5-0df8-724b-a16f-7a9d3bcd5384"),
            Map.entry("supplier-admin5@everp.com", "019a52d5-01a5-758a-8d36-e2ef00d8ffb7"),
            Map.entry("supplier-admin6@everp.com", "019a52d4-f64f-7028-8715-365ab52e4879"),
            Map.entry("supplier-admin7@everp.com", "019a52d4-e876-709e-8646-d31b8db20a95"),
            Map.entry("supplier-admin8@everp.com", "019a52d4-dc52-7605-868b-0ed7486cb106"),
            Map.entry("supplier-admin9@everp.com", "019a52d4-cffd-7876-9a7e-34590cc2c447"),
            Map.entry("supplier-admin10@everp.com", "019a52d4-c49d-77d8-912d-960432b4565c"),
            Map.entry("supplier-admin11@everp.com", "019a52d4-b8d1-7509-9072-31d2e147055e"),
            Map.entry("supplier-admin12@everp.com", "019a52d4-ab46-7abe-9071-025222fb6144"),
            Map.entry("supplier-admin13@everp.com", "019a52d4-96be-72cb-85dd-19fbe3d80880"),
            Map.entry("supplier-admin14@everp.com", "019a52d4-8961-76f0-a2a8-0dbd756d30da"),
            Map.entry("supplier-admin15@everp.com", "019a52d4-7141-7a42-8674-a4c6597acfd7")
    );
}
