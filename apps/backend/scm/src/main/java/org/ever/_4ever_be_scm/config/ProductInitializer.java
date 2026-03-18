package org.ever._4ever_be_scm.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_scm.scm.iv.entity.Product;
import org.ever._4ever_be_scm.scm.iv.entity.SupplierCompany;
import org.ever._4ever_be_scm.scm.iv.repository.ProductRepository;
import org.ever._4ever_be_scm.scm.iv.repository.SupplierCompanyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 차량 부품 데모 시나리오에 필요한 제품 마스터를 생성한다.
 * SupplierUserInitializer가 선행되어 supplier_company가 준비되어 있다는 전제하에 동작한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(2)
public class ProductInitializer implements CommandLineRunner {

    private final SupplierCompanyRepository supplierCompanyRepository;
    private final ProductRepository productRepository;

    private static final List<ProductSeed> PRODUCT_SEEDS = List.of(
        new ProductSeed("prd-demo-mat-001", "019a3df1-7843-7590-a5fd-94aa9aae7d0a", "MAT-ALS-001", "알루미늄 패널 시트 2.5T", "MATERIAL", "KG",
            new BigDecimal("75000"), new BigDecimal("95000")),
        new ProductSeed("prd-demo-mat-002", "019a52d5-2824-7c7e-9826-e5c56987d189", "MAT-HSS-001", "고장력 강판 코일", "MATERIAL", "KG",
            new BigDecimal("82000"), new BigDecimal("102000")),
        new ProductSeed("prd-demo-mat-003", "019a59a5-f5d5-7003-98b0-f8d77df4f031", "MAT-GSP-001", "아연도금 패널 시트", "MATERIAL", "KG",
            new BigDecimal("68000"), new BigDecimal("85000")),
        new ProductSeed("prd-demo-mat-004", "019a52d5-1ad8-754d-af67-e541f85473c4", "MAT-STM-001", "스테인리스 메쉬 시트", "MATERIAL", "M2",
            new BigDecimal("54000"), new BigDecimal("69000")),
        new ProductSeed("prd-demo-mat-005", "019a52d5-0df8-724b-a16f-7a9d3bcd5384", "MAT-ABS-001", "ABS 레진 펠렛", "MATERIAL", "KG",
            new BigDecimal("28000"), new BigDecimal("36000")),
        new ProductSeed("prd-demo-mat-006", "019a52d5-01a5-758a-8d36-e2ef00d8ffb7", "MAT-PPP-001", "PP 레진 펠렛", "MATERIAL", "KG",
            new BigDecimal("22000"), new BigDecimal("29000")),
        new ProductSeed("prd-demo-mat-007", "019a52d4-f64f-7028-8715-365ab52e4879", "MAT-CFF-001", "카본 보강 패브릭", "MATERIAL", "M2",
            new BigDecimal("110000"), new BigDecimal("140000")),
        new ProductSeed("prd-demo-mat-008", "019a52d4-e876-709e-8646-d31b8db20a95", "MAT-GFM-001", "유리섬유 매트", "MATERIAL", "KG",
            new BigDecimal("36000"), new BigDecimal("47000")),
        new ProductSeed("prd-demo-mat-009", "019a52d4-dc52-7605-868b-0ed7486cb106", "MAT-TGI-001", "강화 글래스 인서트", "MATERIAL", "EA",
            new BigDecimal("120000"), new BigDecimal("150000")),
        new ProductSeed("prd-demo-mat-010", "019a52d4-cffd-7876-9a7e-34590cc2c447", "MAT-PCL-001", "폴리카보네이트 렌즈 수지", "MATERIAL", "KG",
            new BigDecimal("45000"), new BigDecimal("58000")),
        new ProductSeed("prd-demo-mat-011", "019a52d4-c49d-77d8-912d-960432b4565c", "MAT-RSS-001", "실링 러버 스트립", "MATERIAL", "M",
            new BigDecimal("15000"), new BigDecimal("22000")),
        new ProductSeed("prd-demo-mat-012", "019a52d4-b8d1-7509-9072-31d2e147055e", "MAT-NVH-001", "NVH 댐핑 라미네이트", "MATERIAL", "M2",
            new BigDecimal("26000"), new BigDecimal("34000")),
        new ProductSeed("prd-demo-mat-013", "019a52d4-ab46-7abe-9071-025222fb6144", "MAT-CTS-001", "크롬 트림 스트립", "MATERIAL", "M",
            new BigDecimal("33000"), new BigDecimal("43000")),
        new ProductSeed("prd-demo-mat-014", "019a52d4-96be-72cb-85dd-19fbe3d80880", "MAT-HFK-001", "체결 하드웨어 키트", "MATERIAL", "SET",
            new BigDecimal("32000"), new BigDecimal("42000")),
        new ProductSeed("prd-demo-mat-015", "019a52d4-8961-76f0-a2a8-0dbd756d30da", "MAT-SCP-001", "표면 코팅 패키지", "MATERIAL", "L",
            new BigDecimal("30000"), new BigDecimal("39000")),
        new ProductSeed("prd-demo-mat-016", "019a52d4-7141-7a42-8674-a4c6597acfd7", "MAT-SAP-001", "구조용 접착제 팩", "MATERIAL", "KG",
            new BigDecimal("29000"), new BigDecimal("38000")),

        new ProductSeed("prd-demo-sub-001", "019a3df1-7843-7590-a5fd-94aa9aae7d0a", "ITM-BBM-001", "범퍼 브래킷 모듈", "ITEM", "EA",
            new BigDecimal("185000"), new BigDecimal("260000")),
        new ProductSeed("prd-demo-sub-002", "019a52d5-2824-7c7e-9826-e5c56987d189", "ITM-GBZ-001", "그릴 베젤 서브어셈블리", "ITEM", "EA",
            new BigDecimal("165000"), new BigDecimal("235000")),
        new ProductSeed("prd-demo-sub-003", "019a52d5-0df8-724b-a16f-7a9d3bcd5384", "ITM-DTC-001", "도어 트림 클립 세트", "ITEM", "SET",
            new BigDecimal("72000"), new BigDecimal("108000")),
        new ProductSeed("prd-demo-sub-004", "019a52d4-dc52-7605-868b-0ed7486cb106", "ITM-LHF-001", "램프 하우징 프레임", "ITEM", "EA",
            new BigDecimal("210000"), new BigDecimal("285000")),
        new ProductSeed("prd-demo-sub-005", "019a52d4-c49d-77d8-912d-960432b4565c", "ITM-WAL-001", "휠 아치 라이너 좌측", "ITEM", "EA",
            new BigDecimal("118000"), new BigDecimal("176000")),
        new ProductSeed("prd-demo-sub-006", "019a52d4-c49d-77d8-912d-960432b4565c", "ITM-WAR-001", "휠 아치 라이너 우측", "ITEM", "EA",
            new BigDecimal("118000"), new BigDecimal("176000")),
        new ProductSeed("prd-demo-sub-007", "019a52d4-f64f-7028-8715-365ab52e4879", "ITM-BSB-001", "배터리 브래킷 스탬프 베이스", "ITEM", "EA",
            new BigDecimal("156000"), new BigDecimal("228000")),
        new ProductSeed("prd-demo-sub-008", "019a52d4-cffd-7876-9a7e-34590cc2c447", "ITM-RGM-001", "라디에이터 그릴 메쉬 유닛", "ITEM", "EA",
            new BigDecimal("148000"), new BigDecimal("215000")),
        new ProductSeed("prd-demo-sub-009", "019a52d4-ab46-7abe-9071-025222fb6144", "ITM-TGC-001", "트림 가니시 캐리어", "ITEM", "EA",
            new BigDecimal("92000"), new BigDecimal("136000")),
        new ProductSeed("prd-demo-sub-010", "019a52d4-96be-72cb-85dd-19fbe3d80880", "ITM-WGB-001", "와이어 가이드 브래킷", "ITEM", "EA",
            new BigDecimal("84000"), new BigDecimal("123000")),

        new ProductSeed("prd-demo-fg-001", "019a52d5-01a5-758a-8d36-e2ef00d8ffb7", "ITM-FBC-001", "전방 범퍼 커버", "ITEM", "EA",
            new BigDecimal("420000"), new BigDecimal("620000")),
        new ProductSeed("prd-demo-fg-002", "019a52d4-ab46-7abe-9071-025222fb6144", "ITM-RDG-001", "라디에이터 그릴", "ITEM", "EA",
            new BigDecimal("360000"), new BigDecimal("540000")),
        new ProductSeed("prd-demo-fg-003", "019a52d4-b8d1-7509-9072-31d2e147055e", "ITM-DTA-001", "도어 트림 어셈블리", "ITEM", "EA",
            new BigDecimal("385000"), new BigDecimal("575000")),
        new ProductSeed("prd-demo-fg-004", "019a52d4-dc52-7605-868b-0ed7486cb106", "ITM-LHH-001", "램프 하우징", "ITEM", "EA",
            new BigDecimal("440000"), new BigDecimal("650000")),
        new ProductSeed("prd-demo-fg-005", "019a52d4-c49d-77d8-912d-960432b4565c", "ITM-WAL-100", "휠 아치 라이너", "ITEM", "EA",
            new BigDecimal("310000"), new BigDecimal("470000")),
        new ProductSeed("prd-demo-fg-006", "019a52d4-f64f-7028-8715-365ab52e4879", "ITM-BRB-001", "배터리 브래킷", "ITEM", "EA",
            new BigDecimal("335000"), new BigDecimal("498000"))
    );

    @Override
    @Transactional
    public void run(String... args) {
        log.info("[Initializer] 차량 부품 데모 제품 마스터 시드를 시작합니다.");

        Map<String, SupplierCompany> suppliersById = supplierCompanyRepository.findAll().stream()
            .collect(Collectors.toMap(SupplierCompany::getId, Function.identity()));
        if (suppliersById.isEmpty()) {
            log.warn("[Initializer] supplier_company 데이터가 존재하지 않아 제품 시드를 건너뜁니다.");
            return;
        }

        Set<String> existingProducts = productRepository.findAll().stream()
            .map(product -> {
                SupplierCompany supplier = product.getSupplierCompany();
                String supplierId = supplier != null ? supplier.getId() : "";
                return supplierId + ":" + product.getProductName();
            })
            .collect(Collectors.toSet());

        int created = 0;
        for (ProductSeed seed : PRODUCT_SEEDS) {
            SupplierCompany company = suppliersById.get(seed.supplierCompanyId());
            if (company == null) {
                log.warn("[Initializer] 공급사를 찾을 수 없어 제품 생성을 건너뜁니다. supplierCompanyId={}, productName={}",
                    seed.supplierCompanyId(), seed.productName());
                continue;
            }
            String uniqueKey = company.getId() + ":" + seed.productName();
            if (productRepository.existsById(seed.productId()) || existingProducts.contains(uniqueKey)) {
                log.debug("[Initializer] 이미 존재하는 제품을 건너뜁니다. productId={}, productName={}",
                    seed.productId(), seed.productName());
                continue;
            }

            Product product = Product.builder()
                .id(seed.productId())
                .productCode(seed.productCode())
                .category(seed.category())
                .supplierCompany(company)
                .productName(seed.productName())
                .unit(seed.unit())
                .originPrice(seed.originPrice())
                .sellingPrice(seed.sellingPrice())
                .build();

            productRepository.save(product);
            created++;
            log.info("[Initializer] 제품 생성 완료 - productCode={}, productName={}, supplierCompanyId={}",
                seed.productCode(), seed.productName(), company.getId());
        }

        log.info("[Initializer] 차량 부품 데모 제품 마스터 시드 완료 (생성 {}건, 계획 {}건)", created, PRODUCT_SEEDS.size());
    }

    private record ProductSeed(
        String productId,
        String supplierCompanyId,
        String productCode,
        String productName,
        String category,
        String unit,
        BigDecimal originPrice,
        BigDecimal sellingPrice
    ) {
    }
}
