package org.ever._4ever_be_business.config;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.company.repository.CustomerCompanyRepository;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerUserInitializer implements CommandLineRunner {

    private final CustomerUserRepository customerUserRepository;
    private final CustomerCompanyRepository customerCompanyRepository;

    private static final String[] COMPANY_NAMES = new String[] {
            "Hanil Motors Co.",
            "현대모비스 영업본부",
            "K-Auto Trading",
            "대영 카 컴퍼니",
            "Prime Vehicle Retail",
            "서연 자동차 판매",
            "AutoBridge Korea",
            "동운 모빌리티",
            "Neo Motor Group",
            "한빛 카 딜러스",
            "Global Auto Parts",
            "넥스트드라이브 모터스",
            "스카이라인 오토모티브",
            "에버카 솔루션즈",
            "인피니티 모터스"
    };

    private static final String[] BASE_ADDRESSES = new String[] {
        "서울특별시 강남구 테헤란로 231",
        "경기도 수원시 영통구 중부대로 55",
        "부산광역시 해운대구 센텀1로 24",
        "대전광역시 유성구 엑스포로 123",
        "광주광역시 서구 상무중앙로 45",
        "대구광역시 동구 동대구로 512",
        "인천광역시 연수구 인천타워대로 250",
        "울산광역시 남구 번영로 10",
        "세종특별자치시 도움5로 16",
        "경기도 용인시 기흥구 구성로 77"
    };

    private static final String[] DETAIL_ADDRESSES = new String[] {
        "Sales HQ 12F",
        "고객센터 3층",
        "전시장 A동",
        "물류센터 B-2",
        "상담부스 C-01",
        "본관 7층",
        "Service Desk 2F",
        "Office Tower 15F",
        "컨퍼런스룸 4F",
        "카매니저 라운지"
    };

    private static final String[] OFFICE_PHONES = new String[] {
        "02-555-1000",
        "031-222-3344",
        "051-777-8888",
        "042-123-4567",
        "062-987-6543",
        "053-234-5678",
        "032-111-2222",
        "052-345-6789",
        "044-200-1234",
        "031-456-7890"
    };

    private static final Map<String, String> ACCOUNT_IDS = Map.ofEntries(
        // base
        Map.entry("customer-admin@everp.com", "019a3e3b-5592-7541-84a9-dce035f6b424"),
        // 1..15
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
        Map.entry("customer-admin15@everp.com", "019a52d5-47b1-7226-8003-4982850a95be")
    );

    @Override
    public void run(String... args) {
        log.info("[Initializer] 고객사 사용자/회사 시드 시작");

        List<String> emails = new ArrayList<>();
        emails.add("customer-admin@everp.com");
        for (int i = 1; i <= 15; i++) {
            emails.add("customer-admin" + i + "@everp.com");
        }

        int idx = 0;
        for (String email : emails) {
            String userId = ACCOUNT_IDS.get(email);
            if (userId == null) {
                log.warn("[Initializer] customer ACCOUNT_IDS에 매핑이 없습니다: {}", email);
                idx++;
                continue;
            }

            try {
                final int currentIndex = idx;
                CustomerUser user = customerUserRepository.findByUserId(userId)
                    .orElseGet(() -> createCustomerUser(userId, email, currentIndex));

                String companyCode = "CUST-" + trailing7(userId);
                CustomerCompany company = customerCompanyRepository.findByCompanyCode(companyCode)
                    .orElseGet(() -> createCustomerCompany(user, userId, currentIndex));

                // 담당자 ↔ 고객사 연결 보정
                if (user.getCustomerCompany() == null || !user.getCustomerCompany().getId().equals(company.getId())) {
                    user.assignCompany(company);
                    customerUserRepository.save(user);
                }
                if (company.getCustomerUserId() == null || !company.getCustomerUserId().equals(user.getId())) {
                    company.assignCustomerUser(user.getId());
                    customerCompanyRepository.save(company);
                }
            } catch (Exception e) {
                log.warn("[Initializer] 고객사 시드 실패 - email: {}, userId: {}, msg: {}", email, userId, e.getMessage());
            }

            idx++;
        }

        log.info("[Initializer] 고객사 사용자/회사 시드 완료");
    }

    private CustomerUser createCustomerUser(String userId, String email, int idx) {
        String displayName = (idx == 0) ? "고객 관리자" : ("고객 관리자" + idx);
        String phone = String.format(Locale.ROOT, "010-%04d-%04d", 9000 + (idx % 1000), 1 + idx);

        String customerUserCode = "CUC-" + trailing7(userId);

        // 목업 데이터 생성용: id를 userId로 고정해 재현성 유지
        CustomerUser user = new CustomerUser(
            userId,              // id
            userId,              // userId
            displayName,
            null,                // 회사는 아래에서 지정
            customerUserCode,
            email,
            phone
        );
        CustomerUser saved = customerUserRepository.save(user);
        log.info("[Initializer] CustomerUser 생성: {} ({})", email, userId);
        return saved;
    }

    private CustomerCompany createCustomerCompany(CustomerUser user, String userId, int idx) {
        String suffix = trailing7(userId);
        String companyCode = "CUST-" + suffix;

        String companyName = COMPANY_NAMES[idx % COMPANY_NAMES.length];
        String baseAddress = BASE_ADDRESSES[idx % BASE_ADDRESSES.length];
        String detailAddress = DETAIL_ADDRESSES[idx % DETAIL_ADDRESSES.length];
        String officePhone = OFFICE_PHONES[idx % OFFICE_PHONES.length];
        String officeEmail = String.format(Locale.ROOT, "cust%s@ever.co", suffix);

        // 리드타임: 5초, 10초, 30초 하나씩, 이후 5~10분 사이 분배
        Duration leadTime;
        if (idx == 0) leadTime = Duration.ofSeconds(5);
        else if (idx == 1) leadTime = Duration.ofSeconds(10);
        else if (idx == 2) leadTime = Duration.ofSeconds(7);
        else leadTime = Duration.ofSeconds(5 + ((idx - 3) % 10));  // 5~15초

        CustomerCompany company = new CustomerCompany(
            userId,              // id
            user.getId(),        // customerUserId
            companyCode,
            companyName,
            "BN-" + suffix,
            "CEO Kim",
            "06236",
            baseAddress,
            detailAddress,
            officePhone,
            officeEmail,
            "자동차 외장재 고객사"
        );
        // 리드타임 설정
        company.updateDeliveryLeadTime(leadTime);

        CustomerCompany saved = customerCompanyRepository.save(company);
        // 회사에 담당자 지정
        saved.assignCustomerUser(user.getId());
        customerCompanyRepository.save(saved);
        user.assignCompany(saved);
        customerUserRepository.save(user);

        log.info("[Initializer] CustomerCompany 생성: {} (code: {}, leadTime: {}s)", companyName, companyCode, leadTime.getSeconds());
        return saved;
    }

    private String trailing7(String uuid) {
        String compact = uuid.replace("-", "");
        int len = compact.length();
        return (len <= 7) ? compact : compact.substring(len - 7);
    }
}
