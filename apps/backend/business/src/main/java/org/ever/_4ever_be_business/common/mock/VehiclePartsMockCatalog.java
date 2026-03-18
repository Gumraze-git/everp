package org.ever._4ever_be_business.common.mock;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import org.springframework.util.StringUtils;

public final class VehiclePartsMockCatalog {

    private static final List<String> SUPPLIER_COMPANY_NAMES = List.of(
            "Ever Exterior Parts Co.",
            "한빛 오토 외장",
            "Seohan Body Panels",
            "국일 카 바디",
            "Prime Bumper & Grill",
            "성우 모터스 트림",
            "Aurora Auto Exterior",
            "동림 차체 솔루션",
            "Neo Fascia Tech",
            "현우 자동차 외장",
            "Global Auto Trim",
            "한성 파츠",
            "NextGen Automotive",
            "아진 오토파츠",
            "TopLine Components",
            "AutoX Korea"
    );

    private static final List<String> CUSTOMER_COMPANY_NAMES = List.of(
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
    );

    private static final List<String> CUSTOMER_CONTACT_NAMES = List.of(
            "정하준 차장",
            "서지우 과장",
            "문예원 대리",
            "장하늘 대리",
            "오유진 주임",
            "권하린 과장"
    );

    private static final List<String> SALES_MANAGER_NAMES = List.of(
            "박준호 영업담당",
            "이수빈 영업담당",
            "김도현 영업매니저",
            "최유진 영업매니저",
            "정민재 영업팀장"
    );

    private static final List<String> FINANCE_MANAGER_NAMES = List.of(
            "정유진 재무매니저",
            "이하늘 재무담당",
            "박서연 회계담당",
            "김지훈 자금담당"
    );

    private static final List<String> EMPLOYEE_NAMES = List.of(
            "김민수",
            "이서연",
            "박지훈",
            "최예린",
            "정하준",
            "강다은",
            "윤도현",
            "임수빈"
    );

    private static final List<String> LEAVE_LABELS = List.of(
            "연차 신청",
            "오전 반차 신청",
            "오후 반차 신청",
            "병가 신청"
    );

    private static final List<ProductTemplate> FINISHED_PARTS = List.of(
            new ProductTemplate("front-bumper-cover", "전방 범퍼 커버", "EA", "부품", new BigDecimal("148000")),
            new ProductTemplate("radiator-grille", "라디에이터 그릴", "EA", "부품", new BigDecimal("132000")),
            new ProductTemplate("door-trim-assembly", "도어 트림 어셈블리", "EA", "부품", new BigDecimal("118000")),
            new ProductTemplate("lamp-housing", "램프 하우징", "EA", "부품", new BigDecimal("164000")),
            new ProductTemplate("wheel-arch-liner", "휠 아치 라이너", "EA", "부품", new BigDecimal("89000")),
            new ProductTemplate("battery-bracket", "배터리 브래킷", "EA", "부품", new BigDecimal("76000")),
            new ProductTemplate("hood-panel", "후드 패널", "EA", "부품", new BigDecimal("225000")),
            new ProductTemplate("tailgate-trim", "테일게이트 트림", "EA", "부품", new BigDecimal("97000"))
    );

    private static final List<ProductTemplate> MATERIALS = List.of(
            new ProductTemplate("aluminum-panel-sheet", "알루미늄 패널 시트", "KG", "원자재", new BigDecimal("78000")),
            new ProductTemplate("abs-resin-pellet", "ABS 레진 펠렛", "KG", "원자재", new BigDecimal("31000")),
            new ProductTemplate("chrome-trim-strip", "크롬 트림 스트립", "M", "원자재", new BigDecimal("43000")),
            new ProductTemplate("structural-adhesive-pack", "구조용 접착제 팩", "KG", "원자재", new BigDecimal("38000")),
            new ProductTemplate("rubber-seal-strip", "실링 고무 스트립", "M", "원자재", new BigDecimal("22000")),
            new ProductTemplate("tempered-glass-insert", "강화 유리 인서트", "EA", "원자재", new BigDecimal("150000")),
            new ProductTemplate("hardware-fastening-kit", "하드웨어 체결 키트", "SET", "원자재", new BigDecimal("42000")),
            new ProductTemplate("surface-coating-pack", "표면 코팅제 팩", "L", "원자재", new BigDecimal("39000"))
    );

    private VehiclePartsMockCatalog() {
    }

    public static WorkflowScenario salesOrderScenario(int index) {
        ProductTemplate part = finishedPart(index);
        String customerName = customerCompanyName(index);
        return new WorkflowScenario(
                syntheticId("sd-order", index),
                code("OR", "sd-order", index),
                customerName + " · " + part.name() + " 수주",
                customerContactName(index),
                pick(List.of("MATERIAL_PREPARATION", "IN_PRODUCTION", "READY_FOR_SHIPMENT"), index)
        );
    }

    public static WorkflowScenario customerQuotationScenario(int index, String requesterName, String companyName) {
        ProductTemplate part = finishedPart(index);
        String resolvedRequester = StringUtils.hasText(requesterName) ? requesterName : customerContactName(index);
        String resolvedCompany = StringUtils.hasText(companyName) ? companyName : customerCompanyName(index);
        return new WorkflowScenario(
                syntheticId("sd-customer-quote", index),
                code("QO", "sd-customer-quote", index),
                resolvedCompany + " · " + part.name() + " 견적 요청",
                resolvedRequester,
                pick(List.of("PENDING", "APPROVAL"), index)
        );
    }

    public static WorkflowScenario internalQuotationScenario(int index) {
        ProductTemplate part = finishedPart(index + 2);
        String customerName = customerCompanyName(index);
        return new WorkflowScenario(
                syntheticId("sd-internal-quote", index),
                code("QO", "sd-internal-quote", index),
                customerName + " · " + part.name() + " 견적",
                salesManagerName(index),
                pick(List.of("REVIEW", "APPROVAL"), index)
        );
    }

    public static WorkflowScenario customerSalesInvoiceScenario(int index) {
        ProductTemplate part = finishedPart(index + 1);
        String customerName = customerCompanyName(index);
        return new WorkflowScenario(
                syntheticId("fcm-customer-sales-invoice", index),
                code("SV", "fcm-customer-sales-invoice", index),
                customerName + " · " + part.name() + " 매출 전표",
                customerName,
                pick(List.of("PENDING", "PAID", "UNPAID"), index)
        );
    }

    public static WorkflowScenario supplierPurchaseInvoiceScenario(int index, String supplierName) {
        ProductTemplate material = material(index);
        String resolvedSupplier = StringUtils.hasText(supplierName) ? supplierName : supplierCompanyName(index);
        return new WorkflowScenario(
                syntheticId("fcm-supplier-purchase-invoice", index),
                code("PV", "fcm-supplier-purchase-invoice", index),
                resolvedSupplier + " · " + material.name() + " 매입 전표",
                resolvedSupplier,
                pick(List.of("PENDING", "PAID"), index)
        );
    }

    public static WorkflowScenario companySalesInvoiceScenario(int index) {
        ProductTemplate part = finishedPart(index + 3);
        String customerName = customerCompanyName(index + 1);
        return new WorkflowScenario(
                syntheticId("fcm-company-sales-invoice", index),
                code("SV", "fcm-company-sales-invoice", index),
                customerName + " · " + part.name() + " 매출 전표",
                financeManagerName(index),
                pick(List.of("PENDING", "PAID"), index)
        );
    }

    public static WorkflowScenario companyPurchaseInvoiceScenario(int index) {
        ProductTemplate material = material(index + 2);
        String supplierName = supplierCompanyName(index);
        return new WorkflowScenario(
                syntheticId("fcm-company-purchase-invoice", index),
                code("PV", "fcm-company-purchase-invoice", index),
                supplierName + " · " + material.name() + " 매입 전표",
                supplierName,
                pick(List.of("PENDING", "PAID"), index)
        );
    }

    public static WorkflowScenario leaveRequestScenario(int index) {
        String employeeName = employeeName(index);
        return new WorkflowScenario(
                syntheticId("hrm-leave", index),
                code("LV", "hrm-leave", index),
                employeeName + " · " + pick(LEAVE_LABELS, index),
                employeeName,
                pick(List.of("PENDING", "APPROVED"), index)
        );
    }

    public static WorkflowScenario attendanceScenario(int index) {
        String employeeName = employeeName(index);
        boolean late = index % 2 == 1;
        return new WorkflowScenario(
                syntheticId("hrm-attendance", index),
                code("ATT", "hrm-attendance", index),
                employeeName + " · " + (late ? "지각 보고" : "정상 출근"),
                employeeName,
                late ? "LATE" : "NORMAL"
        );
    }

    public static ProductScenario productScenario(String identity) {
        int hash = positiveHash(identity);
        ProductTemplate template = hash % 2 == 0 ? pick(MATERIALS, hash) : pick(FINISHED_PARTS, hash);
        BigDecimal unitPrice = template.unitPrice().add(BigDecimal.valueOf((hash % 4) * 3000L));
        String supplierName = supplierCompanyName(hash);

        return new ProductScenario(
                identity,
                code("MAT", template.key(), hash),
                template.name(),
                template.unit(),
                unitPrice,
                template.category(),
                supplierName
        );
    }

    public static SupplierCompanyScenario supplierCompanyScenario(String supplierCompanyId) {
        int hash = positiveHash(supplierCompanyId);
        String supplierName = supplierCompanyName(hash);

        return new SupplierCompanyScenario(
                supplierCompanyId,
                code("SUP", supplierCompanyId, hash),
                supplierName,
                pick(List.of(
                        "경기도 화성시 자동차산업로 123",
                        "울산광역시 남구 산업로 45",
                        "부산광역시 강서구 녹산산단 301",
                        "충청남도 아산시 외장로 77"
                ), hash),
                pick(List.of("R&D Center, 2F", "Assembly Plant A", "Press Shop Bldg.", "도장동 3라인"), hash),
                pick(List.of("부품", "원자재", "외장 모듈"), hash),
                String.format(Locale.ROOT, "02-%04d-%04d", 1200 + (hash % 7000), 2000 + ((hash / 13) % 7000)),
                "manager-" + syntheticId("supplier-user", hash % 100)
        );
    }

    public static String syntheticSupplierCompanyId(String supplierUserId) {
        return "supplier-company-" + shortToken(supplierUserId);
    }

    private static ProductTemplate finishedPart(int index) {
        return pick(FINISHED_PARTS, index);
    }

    private static ProductTemplate material(int index) {
        return pick(MATERIALS, index);
    }

    private static String supplierCompanyName(int index) {
        return pick(SUPPLIER_COMPANY_NAMES, index);
    }

    private static String customerCompanyName(int index) {
        return pick(CUSTOMER_COMPANY_NAMES, index);
    }

    private static String customerContactName(int index) {
        return pick(CUSTOMER_CONTACT_NAMES, index);
    }

    private static String salesManagerName(int index) {
        return pick(SALES_MANAGER_NAMES, index);
    }

    private static String financeManagerName(int index) {
        return pick(FINANCE_MANAGER_NAMES, index);
    }

    private static String employeeName(int index) {
        return pick(EMPLOYEE_NAMES, index);
    }

    private static String code(String prefix, String scenarioKey, int index) {
        int value = Math.floorMod(Objects.hash(prefix, scenarioKey, index), 0x1000000);
        return prefix + "-" + String.format(Locale.ROOT, "%06X", value);
    }

    private static String syntheticId(String domainKey, int index) {
        return domainKey + "-" + String.format(Locale.ROOT, "%03d", index + 1);
    }

    private static String shortToken(String seed) {
        int value = Math.floorMod(Objects.hashCode(seed), 0x1000000);
        return String.format(Locale.ROOT, "%06X", value);
    }

    private static int positiveHash(String value) {
        return Math.floorMod(Objects.hashCode(value), Integer.MAX_VALUE);
    }

    private static <T> T pick(List<T> items, int index) {
        return items.get(Math.floorMod(index, items.size()));
    }

    public record WorkflowScenario(
            String itemId,
            String itemNumber,
            String itemTitle,
            String name,
            String statusCode
    ) {
    }

    public record ProductScenario(
            String productId,
            String productCode,
            String productName,
            String uomName,
            BigDecimal unitPrice,
            String category,
            String supplierName
    ) {
    }

    public record SupplierCompanyScenario(
            String companyId,
            String companyCode,
            String companyName,
            String baseAddress,
            String detailAddress,
            String category,
            String officePhone,
            String managerId
    ) {
    }

    private record ProductTemplate(
            String key,
            String name,
            String unit,
            String category,
            BigDecimal unitPrice
    ) {
    }
}
