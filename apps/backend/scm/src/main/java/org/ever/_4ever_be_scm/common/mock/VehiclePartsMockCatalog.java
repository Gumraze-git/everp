package org.ever._4ever_be_scm.common.mock;

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

    private static final List<String> PURCHASE_REQUESTER_NAMES = List.of(
            "박준호 구매담당",
            "이수빈 자재계획",
            "김도현 조달담당",
            "최유진 생산관리",
            "정민재 구매팀장"
    );

    private static final List<String> STOCK_MANAGER_NAMES = List.of(
            "화성 외장창고",
            "울산 조립창고",
            "아산 트림창고",
            "광주 모듈창고"
    );

    private static final List<String> MES_LINE_NAMES = List.of(
            "사출 1라인",
            "도장 2라인",
            "조립 3라인",
            "검사 1라인"
    );

    private static final List<String> CUSTOMER_COMPANY_NAMES = List.of(
            "Hanil Motors Co.",
            "현대모비스 영업본부",
            "K-Auto Trading",
            "서연 자동차 판매",
            "동운 모빌리티",
            "넥스트드라이브 모터스"
    );

    private static final List<String> FINISHED_PARTS = List.of(
            "전방 범퍼 커버",
            "라디에이터 그릴",
            "도어 트림 어셈블리",
            "램프 하우징",
            "휠 아치 라이너",
            "배터리 브래킷",
            "후드 패널",
            "테일게이트 트림"
    );

    private static final List<String> MATERIALS = List.of(
            "알루미늄 패널 시트",
            "ABS 레진 펠렛",
            "크롬 트림 스트립",
            "구조용 접착제 팩",
            "실링 고무 스트립",
            "강화 유리 인서트",
            "하드웨어 체결 키트",
            "표면 코팅제 팩"
    );

    private VehiclePartsMockCatalog() {
    }

    public static WorkflowScenario purchaseRequestScenario(int index) {
        String finishedPart = finishedPart(index);
        String material = material(index + 1);
        String title = switch (Math.floorMod(index, 4)) {
            case 0 -> finishedPart + " 보충 요청";
            case 1 -> finishedPart + " 금형 보수 자재 요청";
            case 2 -> material + " 구매 요청";
            default -> material + " 긴급 조달";
        };

        return new WorkflowScenario(
                syntheticId("mm-pr", index),
                code("PR", "mm-pr", index),
                title,
                requesterName(index),
                pick(List.of("PENDING", "APPROVAL"), index)
        );
    }

    public static WorkflowScenario purchaseOrderScenario(int index) {
        String supplierName = supplierCompanyName(index);
        String part = finishedPart(index);
        return new WorkflowScenario(
                syntheticId("mm-po", index),
                code("PO", "mm-po", index),
                supplierName + " · " + part + " 발주",
                requesterName(index),
                pick(List.of("APPROVAL", "DELIVERING", "DELIVERED"), index)
        );
    }

    public static WorkflowScenario supplierPurchaseOrderScenario(int index, String supplierName, String productTitle) {
        String resolvedSupplierName = StringUtils.hasText(supplierName) ? supplierName : supplierCompanyName(index);
        String resolvedProductTitle = StringUtils.hasText(productTitle) ? productTitle : finishedPart(index);
        return new WorkflowScenario(
                syntheticId("supplier-po", index),
                code("PO", "supplier-po", index),
                resolvedSupplierName + " · " + resolvedProductTitle + " 발주",
                resolvedSupplierName,
                pick(List.of("PENDING", "APPROVAL", "DELIVERING"), index)
        );
    }

    public static WorkflowScenario stockLogScenario(int index, boolean inbound) {
        String warehouseName = warehouseName(index);
        String target = inbound ? finishedPart(index) : finishedPart(index + 2);
        return new WorkflowScenario(
                syntheticId(inbound ? "im-inbound" : "im-outbound", index),
                code(inbound ? "IN" : "OUT", inbound ? "im-inbound" : "im-outbound", index),
                warehouseName + " · " + target + " " + (inbound ? "입고" : "출고"),
                warehouseName,
                inbound ? "COMPLETED" : pick(List.of("IN_PROGRESS", "COMPLETED"), index)
        );
    }

    public static WorkflowScenario productionQuotationScenario(int index) {
        String customerName = pick(CUSTOMER_COMPANY_NAMES, index);
        String part = finishedPart(index + 1);
        return new WorkflowScenario(
                syntheticId("pp-quotation", index),
                code("QO", "pp-quotation", index),
                part + " 생산 전환 견적",
                customerName,
                pick(List.of("APPROVAL", "PENDING"), index)
        );
    }

    public static WorkflowScenario mesScenario(int index) {
        String lineName = lineName(index);
        String part = finishedPart(index + 3);
        return new WorkflowScenario(
                syntheticId("pp-mes", index),
                code("MES", "pp-mes", index),
                lineName + " · " + part + " 생산",
                lineName,
                pick(List.of("IN_PROGRESS", "PENDING", "COMPLETED"), index)
        );
    }

    private static String requesterName(int index) {
        return pick(PURCHASE_REQUESTER_NAMES, index);
    }

    private static String warehouseName(int index) {
        return pick(STOCK_MANAGER_NAMES, index);
    }

    private static String lineName(int index) {
        return pick(MES_LINE_NAMES, index);
    }

    private static String supplierCompanyName(int index) {
        return pick(SUPPLIER_COMPANY_NAMES, index);
    }

    private static String finishedPart(int index) {
        return pick(FINISHED_PARTS, index);
    }

    private static String material(int index) {
        return pick(MATERIALS, index);
    }

    private static String code(String prefix, String scenarioKey, int index) {
        int value = Math.floorMod(Objects.hash(prefix, scenarioKey, index), 0x1000000);
        return prefix + "-" + String.format(Locale.ROOT, "%06X", value);
    }

    private static String syntheticId(String domainKey, int index) {
        return domainKey + "-" + String.format(Locale.ROOT, "%03d", index + 1);
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
}
