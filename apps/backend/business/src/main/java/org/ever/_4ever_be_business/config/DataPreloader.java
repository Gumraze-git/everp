package org.ever._4ever_be_business.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.company.repository.CustomerCompanyRepository;
import org.ever._4ever_be_business.hr.entity.*;
import org.ever._4ever_be_business.hr.enums.Gender;
import org.ever._4ever_be_business.hr.enums.UserStatus;
import org.ever._4ever_be_business.hr.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 애플리케이션 시작 시 필요한 초기 데이터를 생성하는 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataPreloader {

    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final CustomerCompanyRepository customerCompanyRepository;
    private final InternelUserRepository internelUserRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerUserRepository customerUserRepository;
    private final TrainingRepository trainingRepository;

    @PostConstruct
    @Transactional
    public void preloadData() {
        log.info("========================================");
        log.info("초기 데이터 로딩 시작");
        log.info("========================================");

        loadDepartments();
        loadPositions();
        loadCustomerCompanies();
        loadInternelUsers();
        loadCustomerUsers();
        loadTrainings();

        log.info("========================================");
        log.info("초기 데이터 로딩 완료");
        log.info("========================================");
    }

    /**
     * 부서 데이터 생성
     */
    private void loadDepartments() {
        if (departmentRepository.count() > 0) {
            log.info("부서 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("부서 데이터 생성 중...");

        Department[] departments = {
                new Department("DEPT-001", "구매", "구매 및 조달 업무", LocalDateTime.now()),
                new Department("DEPT-002", "영업", "영업 및 고객 관리 업무", LocalDateTime.now()),
                new Department("DEPT-003", "재고", "재고 관리 및 물류 업무", LocalDateTime.now()),
                new Department("DEPT-004", "재무", "재무 및 회계 업무", LocalDateTime.now()),
                new Department("DEPT-005", "인적자원", "인사 및 조직 관리 업무", LocalDateTime.now()),
                new Department("DEPT-006", "생산", "생산 및 제조 업무", LocalDateTime.now())
        };

        for (Department dept : departments) {
            departmentRepository.save(dept);
            log.info("부서 생성: {} ({})", dept.getDepartmentName(), dept.getDepartmentCode());
        }

        log.info("총 {}개의 부서 생성 완료", departments.length);
    }

    /**
     * 직급 데이터 생성
     * 각 부서마다 모든 직급(사원~사장)을 생성
     */
    private void loadPositions() {
        if (positionRepository.count() > 0) {
            log.info("직급 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("직급 데이터 생성 중...");

        // 모든 부서 조회
        Department[] departments = {
                departmentRepository.findByDepartmentCode("DEPT-001")
                        .orElseThrow(() -> new RuntimeException("구매 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-002")
                        .orElseThrow(() -> new RuntimeException("영업 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-003")
                        .orElseThrow(() -> new RuntimeException("재고 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-004")
                        .orElseThrow(() -> new RuntimeException("재무 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-005")
                        .orElseThrow(() -> new RuntimeException("인적자원 부서를 찾을 수 없습니다.")),
                departmentRepository.findByDepartmentCode("DEPT-006")
                        .orElseThrow(() -> new RuntimeException("생산 부서를 찾을 수 없습니다."))
        };

        // 직급 정보 (이름, 관리직 여부, 연봉)
        Object[][] positionInfos = {
                {"사원", false, new BigDecimal("3500")},
                {"주임", false, new BigDecimal("4000")},
                {"대리", false, new BigDecimal("4800")},
                {"과장", true, new BigDecimal("5800")},
                {"차장", true, new BigDecimal("7000")},
                {"부장", true, new BigDecimal("8500")},
                {"이사", true, new BigDecimal("10500")},
                {"상무", true, new BigDecimal("13000")},
                {"전무", true, new BigDecimal("16000")},
                {"사장", true, new BigDecimal("20000")}
        };

        int positionCount = 0;

        // 각 부서마다 모든 직급 생성
        for (int deptIdx = 0; deptIdx < departments.length; deptIdx++) {
            Department dept = departments[deptIdx];
            String deptCode = String.format("%03d", deptIdx + 1);

            for (int posIdx = 0; posIdx < positionInfos.length; posIdx++) {
                Object[] info = positionInfos[posIdx];
                String positionName = (String) info[0];
                boolean isManager = (boolean) info[1];
                BigDecimal salary = (BigDecimal) info[2];

                // 직급 코드: POS-{부서번호}{직급번호} (예: POS-001001, POS-001002, ...)
                String positionCode = String.format("POS-%s%02d", deptCode, posIdx + 1);

                Position position = new Position(
                        positionCode,
                        positionName,
                        dept,
                        isManager,
                        salary
                );

                positionRepository.save(position);
                positionCount++;

                log.info("직급 생성: {} ({}) - 부서: {}, 연봉: {}만원, 관리직: {}",
                        position.getPositionName(),
                        position.getPositionCode(),
                        dept.getDepartmentName(),
                        position.getSalary(),
                        position.getIsManager() ? "Y" : "N");
            }
        }

        log.info("총 {}개의 직급 생성 완료 ({}개 부서 × {}개 직급)",
                positionCount, departments.length, positionInfos.length);
    }

    /**
     * 고객사 데이터 생성
     */
    private void loadCustomerCompanies() {
        if (customerCompanyRepository.count() > 0) {
            log.info("고객사 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("고객사 데이터 생성 중...");

        CustomerCompany[] companies = {
                new CustomerCompany(
                        null,                           // customerUserId (nullable)
                        "CUST-001",                     // companyCode
                        "현대자동차",                    // companyName
                        "123-81-12345",                 // businessNumber
                        "장재훈",                        // ceoName
                        "06797",                        // zipCode
                        "서울특별시 서초구 헌릉로 12",    // baseAddress
                        "현대자동차 본사",               // detailAddress
                        "02-3464-1114",                 // officePhone
                        "contact@hyundai.com",          // officeEmail
                        "대한민국 대표 완성차 제조업체"   // etc
                ),
                new CustomerCompany(
                        null,
                        "CUST-002",
                        "삼성전자",
                        "124-81-00998",
                        "한종희",
                        "06765",
                        "서울특별시 서초구 서초대로74길 11",
                        "삼성전자 본관",
                        "02-2053-3000",
                        "info@samsung.com",
                        "글로벌 전자제품 제조업체"
                ),
                new CustomerCompany(
                        null,
                        "CUST-003",
                        "LG화학",
                        "116-81-03698",
                        "신학철",
                        "07795",
                        "서울특별시 강서구 마곡중앙10로 10",
                        "LG사이언스파크",
                        "02-3773-1114",
                        "webmaster@lgchem.com",
                        "배터리 및 화학 소재 전문업체"
                ),
                new CustomerCompany(
                        null,
                        "CUST-004",
                        "SK하이닉스",
                        "120-81-02521",
                        "곽노정",
                        "13558",
                        "경기도 성남시 분당구 대왕판교로 645번길 86",
                        "SK하이닉스 본사",
                        "031-5185-4114",
                        "contact@skhynix.com",
                        "반도체 메모리 제조업체"
                ),
                new CustomerCompany(
                        null,
                        "CUST-005",
                        "포스코",
                        "220-81-02382",
                        "장인화",
                        "06194",
                        "서울특별시 강남구 테헤란로 440",
                        "포스코센터",
                        "02-3457-0114",
                        "webmaster@posco.com",
                        "철강 제조 및 유통업체"
                )
        };

        for (CustomerCompany company : companies) {
            customerCompanyRepository.save(company);
            log.info("고객사 생성: {} ({})", company.getCompanyName(), company.getCompanyCode());
        }

        log.info("총 {}개의 고객사 생성 완료", companies.length);
    }

    /**
     * 내부 직원 데이터 생성
     */
    private void loadInternelUsers() {
        if (internelUserRepository.count() > 0) {
            log.info("내부 직원 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("내부 직원 데이터 생성 중...");

        // Position 조회 (구매 부서의 사원, 대리, 과장 사용)
        Position position1 = positionRepository.findByPositionCode("POS-00101")  // 구매-사원
                .orElseThrow(() -> new RuntimeException("Position not found: POS-00101"));
        Position position2 = positionRepository.findByPositionCode("POS-00103")  // 구매-대리
                .orElseThrow(() -> new RuntimeException("Position not found: POS-00103"));
        Position position3 = positionRepository.findByPositionCode("POS-00104")  // 구매-과장
                .orElseThrow(() -> new RuntimeException("Position not found: POS-00104"));

        InternelUser[] internelUsers = {
                new InternelUser(
                        "internel1",                        // id
                        "internel1",                        // userId (Gateway와 동일)
                        "internel1",                        // name
                        "EMP-001",                          // employeeCode
                        position1,                          // position (사원)
                        Gender.MALE,                        // gender
                        LocalDateTime.of(1995, 3, 15, 0, 0),  // birthDate
                        LocalDateTime.of(2023, 1, 1, 0, 0),   // hireDate
                        "서울특별시 강남구",                  // address
                        "internel1@gmail.com",              // email
                        "010-1111-1111",                    // phoneNumber
                        LocalDateTime.of(2023, 1, 1, 0, 0),   // departmentStartAt
                        "학사",                              // education
                        "신입",                               // career
                        UserStatus.ACTIVE
                ),
                new InternelUser(
                        "internel2",
                        "internel2",                        // userId (Gateway와 동일)
                        "internel2",
                        "EMP-002",
                        position2,                          // position (대리)
                        Gender.FEMALE,
                        LocalDateTime.of(1992, 7, 22, 0, 0),
                        LocalDateTime.of(2021, 3, 1, 0, 0),
                        "서울특별시 서초구",
                        "internel2@gmail.com",
                        "010-2222-2222",
                        LocalDateTime.of(2021, 3, 1, 0, 0),
                        "석사",
                        "경력 2년",
                        UserStatus.ACTIVE
                ),
                new InternelUser(
                        "internel3",
                        "internel3",                        // userId (Gateway와 동일)
                        "internel3",
                        "EMP-003",
                        position3,                          // position (과장)
                        Gender.MALE,
                        LocalDateTime.of(1988, 11, 5, 0, 0),
                        LocalDateTime.of(2018, 6, 1, 0, 0),
                        "서울특별시 송파구",
                        "internel3@gmail.com",
                        "010-3333-3333",
                        LocalDateTime.of(2018, 6, 1, 0, 0),
                        "학사",
                        "경력 5년",
                        UserStatus.ACTIVE
                )
        };

        for (InternelUser user : internelUsers) {
            InternelUser savedUser = internelUserRepository.save(user);
            log.info("내부 직원 생성: {} ({})", savedUser.getName(), savedUser.getEmployeeCode());

            // Employee 엔티티 생성
            Employee employee = new Employee(
                    savedUser.getId(),
                    savedUser,
                    15L,  // 연차 15일
                    LocalDateTime.now().minusMonths(6)  // 6개월 전 교육 수료
            );
            employeeRepository.save(employee);
            log.info("Employee 엔티티 생성: {}", savedUser.getName());
        }

        log.info("총 {}개의 내부 직원 생성 완료", internelUsers.length);
    }

    /**
     * 고객사 담당자 데이터 생성
     */
    private void loadCustomerUsers() {
        if (customerUserRepository.count() > 0) {
            log.info("고객사 담당자 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("고객사 담당자 데이터 생성 중...");

        // 기존 고객사 5개 조회
        CustomerCompany company1 = customerCompanyRepository.findByCompanyCode("CUST-001")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company2 = customerCompanyRepository.findByCompanyCode("CUST-002")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company3 = customerCompanyRepository.findByCompanyCode("CUST-003")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company4 = customerCompanyRepository.findByCompanyCode("CUST-004")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));
        CustomerCompany company5 = customerCompanyRepository.findByCompanyCode("CUST-005")
                .orElseThrow(() -> new RuntimeException("CustomerCompany not found"));

        CustomerUser[] customerUsers = {
                new CustomerUser(
                        "customer1",                    // id
                        "customer1",                    // userId (Gateway와 동일)
                        "customer1",                    // customerName
                        company1,                       // customerCompany (현대자동차)
                        "CUST-USER-001",                // customerUserCode
                        "customer1@gmail.com",          // email
                        "010-1001-1001"                 // phoneNumber
                ),
                new CustomerUser(
                        "customer2",
                        "customer2",                    // userId (Gateway와 동일)
                        "customer2",
                        company2,                       // customerCompany (삼성전자)
                        "CUST-USER-002",
                        "customer2@gmail.com",
                        "010-2002-2002"
                ),
                new CustomerUser(
                        "customer3",
                        "customer3",                    // userId (Gateway와 동일)
                        "customer3",
                        company3,                       // customerCompany (LG화학)
                        "CUST-USER-003",
                        "customer3@gmail.com",
                        "010-3003-3003"
                ),
                new CustomerUser(
                        "customer4",
                        "customer4",                    // userId (Gateway와 동일)
                        "customer4",
                        company4,                       // customerCompany (SK하이닉스)
                        "CUST-USER-004",
                        "customer4@gmail.com",
                        "010-4004-4004"
                ),
                new CustomerUser(
                        "customer5",
                        "customer5",                    // userId (Gateway와 동일)
                        "customer5",
                        company5,                       // customerCompany (포스코)
                        "CUST-USER-005",
                        "customer5@gmail.com",
                        "010-5005-5005"
                )
        };

        for (CustomerUser user : customerUsers) {
            customerUserRepository.save(user);
            log.info("고객사 담당자 생성: {} ({})", user.getCustomerName(), user.getCustomerUserCode());
        }

        log.info("총 {}개의 고객사 담당자 생성 완료", customerUsers.length);
    }

    /**
     * 교육 프로그램 데이터 생성
     */
    private void loadTrainings() {
        if (trainingRepository.count() > 0) {
            log.info("교육 프로그램 데이터가 이미 존재합니다. 스킵합니다.");
            return;
        }

        log.info("교육 프로그램 데이터 생성 중...");

        Training[] trainings = {
                new Training(
                        "Spring Boot 심화 과정",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.TECHNICAL_TRAINING,
                        40L,
                        "온라인",
                        15L,
                        30,
                        "Spring Boot 프레임워크를 활용한 백엔드 개발 심화",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.IN_PROGRESS
                ),
                new Training(
                        "Java 프로그래밍 기초",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.BASIC_TRAINING,
                        24L,
                        "오프라인",
                        20L,
                        25,
                        "Java 프로그래밍 언어의 기초 문법과 개념",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.IN_PROGRESS
                ),
                new Training(
                        "데이터베이스 기초",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.BASIC_TRAINING,
                        32L,
                        "온라인",
                        25L,
                        40,
                        "SQL 기초 및 데이터베이스 설계 원칙",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.IN_PROGRESS
                ),
                new Training(
                        "React 프론트엔드 개발",
                        org.ever._4ever_be_business.hr.enums.TrainingCategory.TECHNICAL_TRAINING,
                        48L,
                        "온라인",
                        18L,
                        30,
                        "React 라이브러리를 활용한 현대적 웹 개발",
                        true,
                        org.ever._4ever_be_business.hr.enums.TrainingStatus.IN_PROGRESS
                )
        };

        for (Training training : trainings) {
            trainingRepository.save(training);
            log.info("교육 프로그램 생성: {} ({}) - {}시간, 신청: {}/{}, 상태: {}",
                    training.getTrainingName(),
                    training.getCategory(),
                    training.getDurationHours(),
                    training.getEnrolled(),
                    training.getCapacity(),
                    training.getTrainingStatus());
        }

        log.info("총 {}개의 교육 프로그램 생성 완료", trainings.length);
    }
}
