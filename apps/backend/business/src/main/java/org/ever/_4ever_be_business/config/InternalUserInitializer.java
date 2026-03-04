package org.ever._4ever_be_business.config;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.hr.entity.Department;
import org.ever._4ever_be_business.hr.entity.Employee;
import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.enums.Gender;
import org.ever._4ever_be_business.hr.enums.UserStatus;
import org.ever._4ever_be_business.hr.repository.DepartmentRepository;
import org.ever._4ever_be_business.hr.repository.EmployeeRepository;
import org.ever._4ever_be_business.hr.repository.InternelUserRepository;
import org.ever._4ever_be_business.hr.repository.PositionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Order(1) // PayrollInitializer보다 먼저 실행
@RequiredArgsConstructor
public class InternalUserInitializer implements CommandLineRunner {

    private final InternelUserRepository internelUserRepository;
    private final EmployeeRepository employeeRepository;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;

    private static final String POSITION_CODE_EMPLOYEE = "POS-00501"; // 인적자원-사원
    private static final String POSITION_CODE_MANAGER = "POS-00505";  // 인적자원-차장

    private static final Map<String, String> AUTH_ACCOUNT_IDS = Map.ofEntries(
        Map.entry("hrm-user@everp.com", "019a3ded-4e50-7e19-aa43-eb1820cd8649"),
        Map.entry("hrm-admin@everp.com", "019a3ded-6488-795b-a1fc-31557a2b1aa5"),
        Map.entry("mm-user@everp.com", "019a3dee-8f03-77a0-92f9-e34b09e467fe"),
        Map.entry("mm-admin@everp.com", "019a3dec-a3f3-781c-986b-8c0368cb1e73"),
        Map.entry("sd-user@everp.com", "019a3dec-cf67-7ff7-9e94-b829bbd01152"),
        Map.entry("sd-admin@everp.com", "019a3e39-cbac-7773-a09b-5da7bb0ee3ec"),
        Map.entry("im-user@everp.com", "019a3dec-f1f1-7696-8195-54b87025022a"),
        Map.entry("im-admin@everp.com", "019a3ded-1748-75ea-932c-1d8ad64f75f1"),
        Map.entry("fcm-user@everp.com", "019a3ded-2a80-7104-8552-dd26c30ed45c"),
        Map.entry("fcm-admin@everp.com", "019a3ded-3c02-7294-b376-de49c32e0754"),
        Map.entry("pp-user@everp.com", "019a3e3c-57e9-7e9a-b10f-0ee551498cae"),
        Map.entry("pp-admin@everp.com", "019a3df5-456d-7e0c-8212-388ca6118c18"),
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
        Map.entry("mm-admin1@everp.com", "019a52bf-01a4-797d-bf90-440ca765d9ec"),
        Map.entry("mm-admin2@everp.com", "019a52bf-2bf1-7c13-a9c2-bb4465f22f40"),
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
        Map.entry("sd-admin1@everp.com", "019a52c0-c245-7546-b630-37c8457d85b4"),
        Map.entry("sd-admin2@everp.com", "019a52c0-cf94-7ae7-a43f-90b6a97364e9"),
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
        Map.entry("im-admin1@everp.com", "019a52d5-af6a-76a9-907a-362be9673e6d"),
        Map.entry("im-admin2@everp.com", "019a52d5-df43-70df-bc4f-99936ae897ea"),
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
        Map.entry("pp-admin1@everp.com", "019a52d6-57cc-73db-b64c-a9140a6f5e7a"),
        Map.entry("pp-admin2@everp.com", "019a52d6-6668-7d66-b163-a1299a143320"),
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
        Map.entry("fcm-admin1@everp.com", "019a52d7-bca2-726a-af39-09464173a738"),
        Map.entry("fcm-admin2@everp.com", "019a52d7-c592-7d18-bf5c-5ae121f492ec"),
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
        Map.entry("hrm-admin1@everp.com", "019a52d7-7451-7176-b163-9105089f0e1d"),
        Map.entry("hrm-admin2@everp.com", "019a52d7-7d97-78e6-aa10-e91b4b3d06d8")
    );

    private static final List<SeedUser> INTERNAL_USERS = List.of(
        seedUser("hrm-user@everp.com", "HRM 사용자", "EMP-HRM-001", POSITION_CODE_EMPLOYEE),
        seedUser("hrm-admin@everp.com", "HRM 관리자", "EMP-HRM-ADMIN-001", POSITION_CODE_MANAGER),
        seedUser("mm-user@everp.com", "MM 사용자", "EMP-MM-001", POSITION_CODE_EMPLOYEE),
        seedUser("mm-admin@everp.com", "MM 관리자", "EMP-MM-ADMIN-001", POSITION_CODE_MANAGER),
        seedUser("sd-user@everp.com", "SD 사용자", "EMP-SD-001", POSITION_CODE_EMPLOYEE),
        seedUser("sd-admin@everp.com", "SD 관리자", "EMP-SD-ADMIN-001", POSITION_CODE_MANAGER),
        seedUser("im-user@everp.com", "IM 사용자", "EMP-IM-001", POSITION_CODE_EMPLOYEE),
        seedUser("im-admin@everp.com", "IM 관리자", "EMP-IM-ADMIN-001", POSITION_CODE_MANAGER),
        seedUser("fcm-user@everp.com", "FCM 사용자", "EMP-FCM-001", POSITION_CODE_EMPLOYEE),
        seedUser("fcm-admin@everp.com", "FCM 관리자", "EMP-FCM-ADMIN-001", POSITION_CODE_MANAGER),
        seedUser("pp-user@everp.com", "PP 사용자", "EMP-PP-001", POSITION_CODE_EMPLOYEE),
        seedUser("pp-admin@everp.com", "PP 관리자", "EMP-PP-ADMIN-001", POSITION_CODE_MANAGER)
    );

    private static final List<ModuleSeedConfig> MODULE_SEED_CONFIGS = List.of(
        new ModuleSeedConfig("MM", "DEPT-001", "mm-user", 10, "mm-admin", 2),
        new ModuleSeedConfig("SD", "DEPT-002", "sd-user", 10, "sd-admin", 2),
        new ModuleSeedConfig("IM", "DEPT-003", "im-user", 10, "im-admin", 2),
        new ModuleSeedConfig("FCM", "DEPT-004", "fcm-user", 10, "fcm-admin", 2),
        new ModuleSeedConfig("HRM", "DEPT-005", "hrm-user", 10, "hrm-admin", 2),
        new ModuleSeedConfig("PP", "DEPT-006", "pp-user", 10, "pp-admin", 2)
    );

    private static SeedUser seedUser(String loginEmail, String displayName, String employeeCode, String positionCode) {
        return new SeedUser(
            requireAuthUserId(loginEmail),
            loginEmail,
            displayName,
            employeeCode,
            positionCode
        );
    }

    private static String requireAuthUserId(String loginEmail) {
        String userId = AUTH_ACCOUNT_IDS.get(loginEmail.toLowerCase());
        if (userId == null) {
            throw new IllegalStateException("Auth 계정에 대응되는 userId를 찾을 수 없습니다: " + loginEmail);
        }
        return userId;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("========================================");
        log.info("[InternalUserInitializer] 내부 사용자 기본 데이터 점검 시작");
        log.info("========================================");

        Position employeePosition = positionRepository.findByPositionCode(POSITION_CODE_EMPLOYEE)
            .orElseThrow(() -> new IllegalStateException("필수 직급을 찾을 수 없습니다: " + POSITION_CODE_EMPLOYEE));
        Position managerPosition = positionRepository.findByPositionCode(POSITION_CODE_MANAGER)
            .orElseThrow(() -> new IllegalStateException("필수 직급을 찾을 수 없습니다: " + POSITION_CODE_MANAGER));

        int createdCount = 0;
        for (SeedUser seed : INTERNAL_USERS) {
            boolean created = internelUserRepository.findByUserId(seed.userId()).isEmpty();
            if (created) {
                createInternalUser(seed, employeePosition, managerPosition);
                createdCount++;
            }
        }
        log.info("[InternalUserInitializer] 기본 사용자 생성: {} 명 (기존: {} 명)", createdCount, INTERNAL_USERS.size() - createdCount);

        // 모듈별 계정 시드
        seedModuleMockUsers();

        long totalEmployees = employeeRepository.count();
        log.info("========================================");
        log.info("[InternalUserInitializer] 내부 사용자 기본 데이터 점검 완료 - 총 Employee: {} 명", totalEmployees);
        log.info("========================================");
    }

    // 샘플 한국인 이름 목록
    private static final String[] KOREAN_NAMES = new String[] {
        "김민수", "이서연", "박지훈", "최예린", "정하준",
        "강다은", "윤도현", "임수빈", "장하늘", "오유진",
        "한서준", "서지우", "신민재", "문예원", "권하린"
    };

    private void seedModuleMockUsers() {
        try {
            List<Department> departments = departmentRepository.findAll();
            Map<String, Department> departmentMap = departments.stream()
                .collect(Collectors.toMap(Department::getDepartmentCode, Function.identity()));

            int globalIndex = 0;

            for (ModuleSeedConfig module : MODULE_SEED_CONFIGS) {
                Department department = departmentMap.get(module.departmentCode());
                if (department == null) {
                    log.warn("[Initializer] 모듈에 대응되는 부서를 찾을 수 없습니다. module={}, departmentCode={}",
                        module.moduleCode(), module.departmentCode());
                    continue;
                }

                List<Position> positions = positionRepository.findByDepartmentId(department.getId());
                if (positions == null || positions.isEmpty()) {
                    log.warn("[Initializer] 부서에 직급이 없습니다. departmentCode={}", department.getDepartmentCode());
                    continue;
                }
                positions.sort(Comparator.comparing(Position::getPositionCode));

                List<Position> nonManagerPositions = positions.stream()
                    .filter(position -> !Boolean.TRUE.equals(position.getIsManager()))
                    .collect(Collectors.toList());
                if (nonManagerPositions.isEmpty()) {
                    nonManagerPositions = List.of(positions.get(0));
                }

                List<Position> managerPositions = positions.stream()
                    .filter(position -> Boolean.TRUE.equals(position.getIsManager()))
                    .collect(Collectors.toList());
                if (managerPositions.isEmpty()) {
                    managerPositions = List.of(positions.get(positions.size() - 1));
                }

                for (int i = 1; i <= module.userCount(); i++) {
                    String email = module.userPrefix() + i + "@everp.com";
                    int sampleIndex = globalIndex++;
                    Position position = nonManagerPositions.get((i - 1) % nonManagerPositions.size());
                    seedModuleAccount(module, email, false, position, i, sampleIndex);
                }

                for (int i = 1; i <= module.adminCount(); i++) {
                    String email = module.adminPrefix() + i + "@everp.com";
                    int sampleIndex = globalIndex++;
                    Position position = managerPositions.get((i - 1) % managerPositions.size());
                    seedModuleAccount(module, email, true, position, i, sampleIndex);
                }
            }
        } catch (Exception e) {
            log.warn("[Initializer] 모듈 계정 시드 중 오류: {}", e.getMessage());
        }
    }

    private void seedModuleAccount(
        ModuleSeedConfig module,
        String loginEmail,
        boolean admin,
        Position position,
        int orderWithinRole,
        int sampleIndex
    ) {
        String userId = resolveAuthUserId(loginEmail);
        if (userId == null) {
            return;
        }
        if (internelUserRepository.findByUserId(userId).isPresent()) {
            return; // 멱등 처리
        }

        LocalDateTime now = LocalDateTime.now();
        Gender gender = (sampleIndex % 2 == 0) ? Gender.MALE : Gender.FEMALE;
        LocalDateTime birthDate = now.minusYears(25 + (sampleIndex % 10));
        LocalDateTime hireDate = now.minusYears(1 + (sampleIndex % 3));
        LocalDateTime departmentStartAt = now.minusMonths(3 + (sampleIndex % 6));
        String phone = String.format("010-%04d-%04d",
            1000 + (sampleIndex % 9000),
            1000 + ((orderWithinRole - 1) % 9000));
        String employeeCode = buildEmployeeCode(module.moduleCode(), admin, orderWithinRole);
        String displayName = KOREAN_NAMES[sampleIndex % KOREAN_NAMES.length];

        InternelUser internelUser = new InternelUser(
            userId,
            userId,
            displayName,
            employeeCode,
            position,
            gender,
            birthDate,
            hireDate,
            "서울특별시",
            loginEmail,
            phone,
            departmentStartAt,
            "학사",
            "경력 " + (sampleIndex % 6) + "년",
            UserStatus.ACTIVE
        );

        InternelUser saved = internelUserRepository.save(internelUser);
        Employee employee = new Employee(saved.getId(), saved, 15L, now.minusMonths(6));
        employeeRepository.save(employee);

        log.info("[Initializer] 모듈 내부 사용자 생성 - module: {}, role: {}, loginEmail: {}, userId: {}",
            module.moduleCode(),
            admin ? "ADMIN" : "USER",
            loginEmail,
            userId
        );
    }

    private String resolveAuthUserId(String loginEmail) {
        String userId = AUTH_ACCOUNT_IDS.get(loginEmail.toLowerCase());
        if (userId == null) {
            log.warn("[Initializer] Auth 계정 매핑을 찾을 수 없습니다. loginEmail={}", loginEmail);
        }
        return userId;
    }

    private String buildEmployeeCode(String moduleCode, boolean admin, int orderWithinRole) {
        String roleSegment = admin ? "ADMIN" : "USER";
        return "EMP-" + moduleCode + "-" + roleSegment + "-" + String.format("%03d", orderWithinRole);
    }

    private void createInternalUser(SeedUser seed, Position employeePosition, Position managerPosition) {
        Position position = seed.positionCode().equals(POSITION_CODE_MANAGER)
            ? managerPosition
            : employeePosition;

        InternelUser internelUser = new InternelUser(
            seed.userId(),
            seed.userId(),
            seed.displayName(),
            seed.employeeCode(),
            position,
            Gender.MALE,
            LocalDateTime.now().minusYears(30),
            LocalDateTime.now().minusYears(3),
            "서울특별시 강남구",
            seed.loginEmail(),
            "010-0000-0000",
            LocalDateTime.now().minusYears(3),
            "학사",
            "경력 3년",
            UserStatus.ACTIVE
        );
        InternelUser savedInternelUser = internelUserRepository.save(internelUser);

        Employee employee = new Employee(
            savedInternelUser.getId(),
            savedInternelUser,
            15L,
            LocalDateTime.now().minusMonths(6)
        );
        employeeRepository.save(employee);

        log.info("[Initializer] 내부 사용자 생성 - userId: {}, name: {}", seed.userId(), seed.displayName());
    }

    private record ModuleSeedConfig(
        String moduleCode,
        String departmentCode,
        String userPrefix,
        int userCount,
        String adminPrefix,
        int adminCount
    ) {
    }

    private record SeedUser(
        String userId,
        String loginEmail,
        String displayName,
        String employeeCode,
        String positionCode
    ) {
    }
}
