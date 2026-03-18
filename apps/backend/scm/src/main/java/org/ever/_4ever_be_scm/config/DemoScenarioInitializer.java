package org.ever._4ever_be_scm.config;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_scm.scm.iv.entity.Product;
import org.ever._4ever_be_scm.scm.iv.entity.ProductStock;
import org.ever._4ever_be_scm.scm.iv.entity.ProductStockLog;
import org.ever._4ever_be_scm.scm.iv.entity.SupplierCompany;
import org.ever._4ever_be_scm.scm.iv.entity.Warehouse;
import org.ever._4ever_be_scm.scm.iv.repository.ProductRepository;
import org.ever._4ever_be_scm.scm.iv.repository.ProductStockLogRepository;
import org.ever._4ever_be_scm.scm.iv.repository.ProductStockRepository;
import org.ever._4ever_be_scm.scm.iv.repository.SupplierCompanyRepository;
import org.ever._4ever_be_scm.scm.iv.repository.WarehouseRepository;
import org.ever._4ever_be_scm.scm.mm.entity.ProductOrder;
import org.ever._4ever_be_scm.scm.mm.entity.ProductOrderApproval;
import org.ever._4ever_be_scm.scm.mm.entity.ProductOrderItem;
import org.ever._4ever_be_scm.scm.mm.entity.ProductOrderShipment;
import org.ever._4ever_be_scm.scm.mm.entity.ProductRequest;
import org.ever._4ever_be_scm.scm.mm.entity.ProductRequestApproval;
import org.ever._4ever_be_scm.scm.mm.entity.ProductRequestItem;
import org.ever._4ever_be_scm.scm.mm.repository.ProductOrderApprovalRepository;
import org.ever._4ever_be_scm.scm.mm.repository.ProductOrderItemRepository;
import org.ever._4ever_be_scm.scm.mm.repository.ProductOrderRepository;
import org.ever._4ever_be_scm.scm.mm.repository.ProductOrderShipmentRepository;
import org.ever._4ever_be_scm.scm.mm.repository.ProductRequestApprovalRepository;
import org.ever._4ever_be_scm.scm.mm.repository.ProductRequestItemRepository;
import org.ever._4ever_be_scm.scm.mm.repository.ProductRequestRepository;
import org.ever._4ever_be_scm.scm.pp.entity.Bom;
import org.ever._4ever_be_scm.scm.pp.entity.BomExplosion;
import org.ever._4ever_be_scm.scm.pp.entity.BomItem;
import org.ever._4ever_be_scm.scm.pp.entity.Mes;
import org.ever._4ever_be_scm.scm.pp.entity.Operation;
import org.ever._4ever_be_scm.scm.pp.entity.Routing;
import org.ever._4ever_be_scm.scm.pp.repository.BomExplosionRepository;
import org.ever._4ever_be_scm.scm.pp.repository.BomItemRepository;
import org.ever._4ever_be_scm.scm.pp.repository.BomRepository;
import org.ever._4ever_be_scm.scm.pp.repository.MesRepository;
import org.ever._4ever_be_scm.scm.pp.repository.OperationRepository;
import org.ever._4ever_be_scm.scm.pp.repository.RoutingRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(100)
public class DemoScenarioInitializer implements CommandLineRunner {

    private static final String MM_USER_ID = "019a3dee-8f03-77a0-92f9-e34b09e467fe";
    private static final String MM_ADMIN_ID = "019a3dec-a3f3-781c-986b-8c0368cb1e73";
    private static final String IM_USER_ID = "019a3dec-f1f1-7696-8195-54b87025022a";
    private static final String IM_ADMIN_ID = "019a3ded-1748-75ea-932c-1d8ad64f75f1";
    private static final String PP_USER_ID = "019a3e3c-57e9-7e9a-b10f-0ee551498cae";
    private static final String PP_ADMIN_ID = "019a3df5-456d-7e0c-8212-388ca6118c18";

    private final SupplierCompanyRepository supplierCompanyRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductStockRepository productStockRepository;
    private final ProductStockLogRepository productStockLogRepository;
    private final ProductRequestApprovalRepository productRequestApprovalRepository;
    private final ProductRequestRepository productRequestRepository;
    private final ProductRequestItemRepository productRequestItemRepository;
    private final ProductOrderApprovalRepository productOrderApprovalRepository;
    private final ProductOrderShipmentRepository productOrderShipmentRepository;
    private final ProductOrderRepository productOrderRepository;
    private final ProductOrderItemRepository productOrderItemRepository;
    private final OperationRepository operationRepository;
    private final BomRepository bomRepository;
    private final BomItemRepository bomItemRepository;
    private final RoutingRepository routingRepository;
    private final BomExplosionRepository bomExplosionRepository;
    private final MesRepository mesRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, SupplierCompany> supplierMap = supplierCompanyRepository.findAll().stream()
            .collect(Collectors.toMap(SupplierCompany::getId, Function.identity()));
        Map<String, Product> productMap = productRepository.findAll().stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));
        Map<String, Operation> operationMap = operationRepository.findAll().stream()
            .collect(Collectors.toMap(Operation::getId, Function.identity()));

        if (supplierMap.isEmpty() || productMap.isEmpty() || operationMap.isEmpty()) {
            log.warn("[DemoScenarioInitializer] 선행 마스터가 없어 SCM 데모 시드를 건너뜁니다.");
            return;
        }

        upsertWarehouses();
        Map<String, Warehouse> warehouseMap = warehouseRepository.findAll().stream()
            .collect(Collectors.toMap(Warehouse::getId, Function.identity()));

        upsertStocks(productMap, warehouseMap);
        Map<String, ProductStock> stockMap = productStockRepository.findAll().stream()
            .collect(Collectors.toMap(ProductStock::getId, Function.identity()));

        upsertProductRequests(productMap);
        upsertProductOrders(productMap, supplierMap);
        upsertStockLogs(stockMap, warehouseMap);
        upsertBoms(operationMap);
        upsertMes();
    }

    private void upsertWarehouses() {
        for (WarehouseSeed seed : warehouseSeeds()) {
            if (warehouseRepository.existsById(seed.id())) {
                continue;
            }
            warehouseRepository.save(Warehouse.builder()
                .id(seed.id())
                .internalUserId(seed.internalUserId())
                .warehouseCode(seed.code())
                .warehouseName(seed.name())
                .warehouseType(seed.type())
                .status("ACTIVE")
                .location(seed.location())
                .description(seed.description())
                .build());
            touch("warehouse", seed.id(), weeksAgo(seed.weeksAgo()), weeksAgo(Math.max(seed.weeksAgo() - 1, 0)));
        }
    }

    private void upsertStocks(Map<String, Product> productMap, Map<String, Warehouse> warehouseMap) {
        for (StockSeed seed : stockSeeds()) {
            if (productStockRepository.existsById(seed.id())) {
                continue;
            }

            productStockRepository.save(ProductStock.builder()
                .id(seed.id())
                .product(require(productMap, seed.productId(), "product"))
                .warehouse(require(warehouseMap, seed.warehouseId(), "warehouse"))
                .status(seed.status())
                .availableCount(seed.availableCount())
                .safetyCount(seed.safetyCount())
                .reservedCount(seed.reservedCount())
                .forShipmentCount(seed.forShipmentCount())
                .build());
            touch("product_stock", seed.id(), weeksAgo(seed.weeksAgo()), weeksAgo(Math.max(seed.weeksAgo() - 1, 0)));
        }
    }

    private void upsertProductRequests(Map<String, Product> productMap) {
        for (ProductRequestSeed seed : productRequestSeeds()) {
            ProductRequestApproval approval = productRequestApprovalRepository.findById(seed.approvalId())
                .orElseGet(() -> {
                    ProductRequestApproval created = productRequestApprovalRepository.save(ProductRequestApproval.builder()
                        .id(seed.approvalId())
                        .approvalStatus(seed.status())
                        .approvedBy(seed.approvedBy())
                        .approvedAt(seed.approvedAt())
                        .rejectedReason(seed.rejectedReason())
                        .build());
                    touch("product_request_approval", created.getId(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));
                    return created;
                });

            if (!productRequestRepository.existsById(seed.id())) {
                productRequestRepository.save(ProductRequest.builder()
                    .id(seed.id())
                    .productRequestCode(seed.code())
                    .productRequestType(seed.type())
                    .requesterId(seed.requesterId())
                    .totalPrice(seed.totalPrice())
                    .approvalId(approval)
                    .build());
                touch("product_request", seed.id(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));
            }

            for (ProductRequestItemSeed itemSeed : seed.items()) {
                if (productRequestItemRepository.existsById(itemSeed.id())) {
                    continue;
                }
                Product product = require(productMap, itemSeed.productId(), "product");
                productRequestItemRepository.save(ProductRequestItem.builder()
                    .id(itemSeed.id())
                    .productRequestId(seed.id())
                    .productId(product.getId())
                    .count(itemSeed.count())
                    .unit(product.getUnit())
                    .price(itemSeed.price())
                    .preferredDeliveryDate(itemSeed.preferredDeliveryDate())
                    .purpose(itemSeed.purpose())
                    .etc(itemSeed.note())
                    .build());
                touch("product_request_item", itemSeed.id(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));
            }
        }
    }

    private void upsertProductOrders(Map<String, Product> productMap, Map<String, SupplierCompany> supplierMap) {
        for (ProductOrderSeed seed : productOrderSeeds()) {
            ProductOrderApproval approval = productOrderApprovalRepository.findById(seed.approvalId())
                .orElseGet(() -> {
                    ProductOrderApproval created = productOrderApprovalRepository.save(ProductOrderApproval.builder()
                        .id(seed.approvalId())
                        .approvalStatus(seed.status())
                        .approvedBy(seed.approvedBy())
                        .approvedAt(seed.approvedAt())
                        .rejectedReason(seed.rejectedReason())
                        .build());
                    touch("product_order_approval", created.getId(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.updatedDaysAgo() - 1, 0)));
                    return created;
                });

            ProductOrderShipment shipment = productOrderShipmentRepository.findById(seed.shipmentId())
                .orElseGet(() -> {
                    ProductOrderShipment created = productOrderShipmentRepository.save(ProductOrderShipment.builder()
                        .id(seed.shipmentId())
                        .status(seed.status())
                        .deliveredAt(seed.deliveredAt())
                        .expectedDelivery(seed.expectedDelivery())
                        .actualDelivery(seed.actualDelivery())
                        .build());
                    touch("product_order_shipment", created.getId(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.updatedDaysAgo() - 1, 0)));
                    return created;
                });

            if (!productOrderRepository.existsById(seed.id())) {
                SupplierCompany supplier = require(supplierMap, seed.supplierCompanyId(), "supplier");
                productOrderRepository.save(ProductOrder.builder()
                    .id(seed.id())
                    .productOrderCode(seed.code())
                    .productOrderType(seed.type())
                    .productRequestId(seed.requestId())
                    .requesterId(seed.requesterId())
                    .supplierCompanyName(supplier.getCompanyName())
                    .approvalId(approval)
                    .shipmentId(shipment)
                    .totalPrice(seed.totalPrice())
                    .dueDate(seed.dueDate())
                    .etc(seed.note())
                    .build());
                touch("product_order", seed.id(), daysAgo(seed.daysAgo()), daysAgo(seed.updatedDaysAgo()));
            }

            for (ProductOrderItemSeed itemSeed : seed.items()) {
                if (productOrderItemRepository.existsById(itemSeed.id())) {
                    continue;
                }
                Product product = require(productMap, itemSeed.productId(), "product");
                productOrderItemRepository.save(ProductOrderItem.builder()
                    .id(itemSeed.id())
                    .productOrderId(seed.id())
                    .productId(product.getId())
                    .count(itemSeed.count())
                    .unit(product.getUnit())
                    .price(itemSeed.price())
                    .build());
                touch("product_order_item", itemSeed.id(), daysAgo(seed.daysAgo()), daysAgo(seed.updatedDaysAgo()));
            }
        }
    }

    private void upsertStockLogs(Map<String, ProductStock> stockMap, Map<String, Warehouse> warehouseMap) {
        for (StockLogSeed seed : stockLogSeeds()) {
            if (productStockLogRepository.existsById(seed.id())) {
                continue;
            }
            productStockLogRepository.save(ProductStockLog.builder()
                .id(seed.id())
                .productStock(require(stockMap, seed.stockId(), "productStock"))
                .previousCount(seed.previousCount())
                .changeCount(seed.changeCount())
                .currentCount(seed.currentCount())
                .movementType(seed.movementType())
                .fromWarehouse(seed.fromWarehouseId() == null ? null : require(warehouseMap, seed.fromWarehouseId(), "warehouse"))
                .toWarehouse(seed.toWarehouseId() == null ? null : require(warehouseMap, seed.toWarehouseId(), "warehouse"))
                .createdById(seed.createdById())
                .referenceCode(seed.referenceCode())
                .note(seed.note())
                .build());
            touch("product_stock_log", seed.id(), daysAgo(seed.daysAgo()), daysAgo(seed.daysAgo()));
        }
    }

    private void upsertBoms(Map<String, Operation> operationMap) {
        for (BomSeed seed : bomSeeds()) {
            if (!bomRepository.existsById(seed.id())) {
                bomRepository.save(Bom.builder()
                    .id(seed.id())
                    .productId(seed.productId())
                    .bomCode(seed.code())
                    .description(seed.description())
                    .version(seed.version())
                    .leadTime(seed.leadTime())
                    .originPrice(seed.originPrice())
                    .sellingPrice(seed.sellingPrice())
                    .build());
                touch("bom", seed.id(), weeksAgo(seed.weeksAgo()), weeksAgo(Math.max(seed.weeksAgo() - 1, 0)));
            }
        }

        for (BomItemSeed seed : bomItemSeeds()) {
            if (bomItemRepository.existsById(seed.id())) {
                continue;
            }
            bomItemRepository.save(BomItem.builder()
                .id(seed.id())
                .bomId(seed.bomId())
                .componentType(seed.componentType())
                .componentId(seed.componentId())
                .unit(seed.unit())
                .count(seed.count())
                .build());
            touch("bom_item", seed.id(), weeksAgo(seed.weeksAgo()), weeksAgo(seed.weeksAgo()));
        }

        for (RoutingSeed seed : routingSeeds()) {
            if (routingRepository.existsById(seed.id())) {
                continue;
            }
            routingRepository.save(Routing.builder()
                .id(seed.id())
                .bomItemId(seed.bomItemId())
                .operationId(require(operationMap, seed.operationId(), "operation").getId())
                .sequence(seed.sequence())
                .requiredTime(seed.requiredTime())
                .build());
            touch("routing", seed.id(), weeksAgo(seed.weeksAgo()), weeksAgo(seed.weeksAgo()));
        }

        for (BomExplosionSeed seed : bomExplosionSeeds()) {
            if (bomExplosionRepository.existsById(seed.id())) {
                continue;
            }
            bomExplosionRepository.save(BomExplosion.builder()
                .id(seed.id())
                .parentBomId(seed.parentBomId())
                .componentProductId(seed.componentProductId())
                .level(seed.level())
                .totalRequiredCount(seed.totalRequiredCount())
                .path(seed.path())
                .routingId(seed.routingId())
                .build());
            touch("bom_explosion", seed.id(), weeksAgo(seed.weeksAgo()), weeksAgo(seed.weeksAgo()));
        }
    }

    private void upsertMes() {
        for (MesSeed seed : mesSeeds()) {
            if (mesRepository.existsById(seed.id())) {
                continue;
            }
            mesRepository.save(Mes.builder()
                .id(seed.id())
                .mesNumber(seed.mesNumber())
                .quotationId(seed.quotationId())
                .bomId(seed.bomId())
                .productId(seed.productId())
                .quantity(seed.quantity())
                .status(seed.status())
                .currentOperationId(seed.currentOperationId())
                .startDate(seed.startDate())
                .endDate(seed.endDate())
                .progressRate(seed.progressRate())
                .build());
            touch("mes", seed.id(), daysAgo(seed.daysAgo()), daysAgo(Math.max(seed.daysAgo() - 1, 0)));
        }
    }

    private <T> T require(Map<String, T> values, String id, String label) {
        T value = values.get(id);
        if (value == null) {
            throw new IllegalStateException("필수 " + label + "를 찾을 수 없습니다: " + id);
        }
        return value;
    }

    private void touch(String table, String id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        jdbcTemplate.update(
            "UPDATE " + table + " SET created_at = ?, updated_at = ? WHERE id = ?",
            Timestamp.valueOf(createdAt),
            Timestamp.valueOf(updatedAt),
            id
        );
    }

    private static LocalDateTime weeksAgo(int weeksAgo) {
        return LocalDateTime.now().minusWeeks(Math.max(weeksAgo, 0)).withHour(9).withMinute(0).withSecond(0).withNano(0);
    }

    private static LocalDateTime daysAgo(int daysAgo) {
        return LocalDateTime.now().minusDays(Math.max(daysAgo, 0)).withHour(9).withMinute(0).withSecond(0).withNano(0);
    }

    private static BigDecimal amount(String value) {
        return new BigDecimal(value);
    }

    private List<WarehouseSeed> warehouseSeeds() {
        return List.of(
            new WarehouseSeed("wh-demo-mat-001", IM_ADMIN_ID, "WH-MAT-HS", "화성 원자재창고", "MATERIAL", "경기도 화성시 자동차산업로 123", "범퍼/트림 소재 보관", 20),
            new WarehouseSeed("wh-demo-mat-002", IM_USER_ID, "WH-MAT-UL", "울산 원자재창고", "MATERIAL", "울산광역시 남구 산업로 45", "그릴/램프 소재 보관", 14),
            new WarehouseSeed("wh-demo-item-001", PP_ADMIN_ID, "WH-ITEM-HS", "화성 완제품창고", "ITEM", "경기도 화성시 외장모듈로 77", "외장 모듈 완제품 보관", 12),
            new WarehouseSeed("wh-demo-item-002", PP_USER_ID, "WH-ITEM-UL", "울산 조립출하창고", "ITEM", "울산광역시 북구 모듈밸리 301", "출하 대기 완제품 보관", 8),
            new WarehouseSeed("wh-demo-qua-001", MM_ADMIN_ID, "WH-QUA-HS", "화성 검품격리창고", "QUARANTINE", "경기도 화성시 품질관리로 19", "입고 검품 및 격리", 6),
            new WarehouseSeed("wh-demo-qua-002", MM_USER_ID, "WH-QUA-UL", "울산 출하보류창고", "QUARANTINE", "울산광역시 북구 물류2로 11", "출하 보류품 임시 보관", 2)
        );
    }

    private List<StockSeed> stockSeeds() {
        return List.of(
            new StockSeed("stk-demo-001", "prd-demo-mat-001", "wh-demo-mat-001", amount("820"), amount("240"), amount("60"), BigDecimal.ZERO, "NORMAL", 12),
            new StockSeed("stk-demo-002", "prd-demo-mat-002", "wh-demo-mat-001", amount("760"), amount("220"), amount("40"), BigDecimal.ZERO, "NORMAL", 11),
            new StockSeed("stk-demo-003", "prd-demo-mat-003", "wh-demo-mat-001", amount("540"), amount("180"), amount("20"), BigDecimal.ZERO, "NORMAL", 10),
            new StockSeed("stk-demo-004", "prd-demo-mat-004", "wh-demo-mat-001", amount("310"), amount("120"), amount("10"), BigDecimal.ZERO, "NORMAL", 10),
            new StockSeed("stk-demo-005", "prd-demo-mat-005", "wh-demo-mat-001", amount("180"), amount("150"), amount("15"), BigDecimal.ZERO, "CAUTION", 9),
            new StockSeed("stk-demo-006", "prd-demo-mat-006", "wh-demo-mat-001", amount("160"), amount("140"), amount("10"), BigDecimal.ZERO, "NORMAL", 9),
            new StockSeed("stk-demo-007", "prd-demo-mat-007", "wh-demo-mat-002", amount("205"), amount("90"), amount("15"), BigDecimal.ZERO, "NORMAL", 8),
            new StockSeed("stk-demo-008", "prd-demo-mat-008", "wh-demo-mat-002", amount("240"), amount("110"), amount("25"), BigDecimal.ZERO, "NORMAL", 8),
            new StockSeed("stk-demo-009", "prd-demo-mat-009", "wh-demo-mat-002", amount("32"), amount("60"), BigDecimal.ZERO, BigDecimal.ZERO, "URGENT", 7),
            new StockSeed("stk-demo-010", "prd-demo-mat-010", "wh-demo-mat-002", amount("74"), amount("95"), amount("6"), BigDecimal.ZERO, "CAUTION", 7),
            new StockSeed("stk-demo-011", "prd-demo-mat-011", "wh-demo-mat-002", amount("460"), amount("150"), amount("18"), BigDecimal.ZERO, "NORMAL", 6),
            new StockSeed("stk-demo-012", "prd-demo-mat-012", "wh-demo-mat-002", amount("210"), amount("100"), amount("12"), BigDecimal.ZERO, "NORMAL", 6),
            new StockSeed("stk-demo-013", "prd-demo-mat-013", "wh-demo-mat-002", amount("68"), amount("90"), amount("5"), BigDecimal.ZERO, "CAUTION", 5),
            new StockSeed("stk-demo-014", "prd-demo-mat-014", "wh-demo-qua-001", amount("40"), amount("80"), BigDecimal.ZERO, BigDecimal.ZERO, "URGENT", 4),
            new StockSeed("stk-demo-015", "prd-demo-mat-015", "wh-demo-mat-001", amount("125"), amount("95"), amount("10"), BigDecimal.ZERO, "NORMAL", 4),
            new StockSeed("stk-demo-016", "prd-demo-mat-016", "wh-demo-mat-001", amount("85"), amount("100"), amount("10"), BigDecimal.ZERO, "CAUTION", 3),

            new StockSeed("stk-demo-017", "prd-demo-sub-001", "wh-demo-item-001", amount("58"), amount("24"), amount("5"), amount("4"), "NORMAL", 8),
            new StockSeed("stk-demo-018", "prd-demo-sub-002", "wh-demo-item-001", amount("66"), amount("28"), amount("6"), amount("4"), "NORMAL", 8),
            new StockSeed("stk-demo-019", "prd-demo-sub-003", "wh-demo-item-001", amount("22"), amount("18"), amount("3"), amount("2"), "NORMAL", 7),
            new StockSeed("stk-demo-020", "prd-demo-sub-004", "wh-demo-item-001", amount("14"), amount("18"), amount("2"), amount("2"), "CAUTION", 7),
            new StockSeed("stk-demo-021", "prd-demo-sub-005", "wh-demo-item-002", amount("42"), amount("16"), amount("4"), amount("5"), "NORMAL", 6),
            new StockSeed("stk-demo-022", "prd-demo-sub-006", "wh-demo-item-002", amount("39"), amount("16"), amount("4"), amount("5"), "NORMAL", 6),
            new StockSeed("stk-demo-023", "prd-demo-sub-007", "wh-demo-item-002", amount("11"), amount("18"), amount("1"), amount("2"), "URGENT", 5),
            new StockSeed("stk-demo-024", "prd-demo-sub-008", "wh-demo-item-001", amount("48"), amount("20"), amount("4"), amount("3"), "NORMAL", 5),
            new StockSeed("stk-demo-025", "prd-demo-sub-009", "wh-demo-item-002", amount("29"), amount("12"), amount("2"), amount("2"), "NORMAL", 4),
            new StockSeed("stk-demo-026", "prd-demo-sub-010", "wh-demo-qua-002", amount("7"), amount("12"), BigDecimal.ZERO, BigDecimal.ZERO, "URGENT", 3),

            new StockSeed("stk-demo-027", "prd-demo-fg-001", "wh-demo-item-001", amount("16"), amount("8"), amount("2"), amount("3"), "NORMAL", 4),
            new StockSeed("stk-demo-028", "prd-demo-fg-002", "wh-demo-item-001", amount("19"), amount("9"), amount("3"), amount("3"), "NORMAL", 4),
            new StockSeed("stk-demo-029", "prd-demo-fg-003", "wh-demo-item-002", amount("12"), amount("7"), amount("2"), amount("2"), "NORMAL", 3),
            new StockSeed("stk-demo-030", "prd-demo-fg-004", "wh-demo-item-002", amount("9"), amount("8"), amount("1"), amount("2"), "NORMAL", 3),
            new StockSeed("stk-demo-031", "prd-demo-fg-005", "wh-demo-item-002", amount("6"), amount("7"), amount("1"), amount("1"), "CAUTION", 2),
            new StockSeed("stk-demo-032", "prd-demo-fg-006", "wh-demo-item-001", amount("11"), amount("6"), amount("1"), amount("2"), "NORMAL", 2)
        );
    }

    private List<ProductRequestSeed> productRequestSeeds() {
        return List.of(
            new ProductRequestSeed("pr-demo-001", "pra-demo-001", "PR-202512-001", "MATERIAL", MM_USER_ID, amount("4680000"), "PENDING", null, null, 70,
                List.of(new ProductRequestItemSeed("pri-demo-001", "prd-demo-mat-005", amount("130"), amount("4680000"), daysAgo(55), "범퍼 커버 사출 소재 보충", "범퍼 커버 양산 대응"))),
            new ProductRequestSeed("pr-demo-002", "pra-demo-002", "PR-202601-002", "MATERIAL", MM_USER_ID, amount("3870000"), "APPROVAL", MM_ADMIN_ID, null, 56,
                List.of(new ProductRequestItemSeed("pri-demo-002", "prd-demo-mat-013", amount("90"), amount("3870000"), daysAgo(45), "라디에이터 그릴 장식부 조달", "그릴 보충 생산"))),
            new ProductRequestSeed("pr-demo-003", "pra-demo-003", "PR-202601-003", "ITEM", MM_ADMIN_ID, amount("2700000"), "APPROVAL", MM_ADMIN_ID, null, 49,
                List.of(new ProductRequestItemSeed("pri-demo-003", "prd-demo-sub-001", amount("12"), amount("2700000"), daysAgo(36), "범퍼 브래킷 서브어셈블리 조달", "범퍼 양산 사전 확보"))),
            new ProductRequestSeed("pr-demo-004", "pra-demo-004", "PR-202602-004", "MATERIAL", MM_USER_ID, amount("3420000"), "PENDING", null, null, 35,
                List.of(new ProductRequestItemSeed("pri-demo-004", "prd-demo-mat-016", amount("90"), amount("3420000"), daysAgo(28), "램프 하우징 접착 자재 보충", "출하 준비"))),
            new ProductRequestSeed("pr-demo-005", "pra-demo-005", "PR-202602-005", "ITEM", MM_ADMIN_ID, amount("2052000"), "APPROVAL", MM_ADMIN_ID, null, 21,
                List.of(new ProductRequestItemSeed("pri-demo-005", "prd-demo-sub-003", amount("19"), amount("2052000"), daysAgo(17), "도어 트림 클립 세트 긴급 조달", "긴급 조립 대응"))),
            new ProductRequestSeed("pr-demo-006", "pra-demo-006", "PR-202603-006", "MATERIAL", MM_USER_ID, amount("1620000"), "APPROVAL", MM_ADMIN_ID, null, 13,
                List.of(new ProductRequestItemSeed("pri-demo-006", "prd-demo-mat-010", amount("30"), amount("1620000"), daysAgo(8), "램프 렌즈 수지 확보", "램프 하우징 생산"))),
            new ProductRequestSeed("pr-demo-007", "pra-demo-007", "PR-202603-007", "ITEM", MM_USER_ID, amount("2736000"), "PENDING", null, null, 8,
                List.of(new ProductRequestItemSeed("pri-demo-007", "prd-demo-sub-009", amount("18"), amount("2736000"), daysAgo(5), "트림 가니시 캐리어 보충", "도어 트림 마감재 보충"))),
            new ProductRequestSeed("pr-demo-008", "pra-demo-008", "PR-202603-008", "MATERIAL", MM_ADMIN_ID, amount("3360000"), "REJECTED", MM_ADMIN_ID, "대체 재고 전환", 4,
                List.of(new ProductRequestItemSeed("pri-demo-008", "prd-demo-mat-014", amount("80"), amount("3360000"), daysAgo(3), "체결 하드웨어 키트 추가 구매", "대체 가능 재고 확인")))
        );
    }

    private List<ProductOrderSeed> productOrderSeeds() {
        return List.of(
            new ProductOrderSeed("po-demo-001", "poa-demo-001", "pos-demo-001", "pr-demo-001", "019a52d5-0df8-724b-a16f-7a9d3bcd5384", "PO-202512-001", "MATERIAL", MM_USER_ID,
                amount("4680000"), "PENDING", null, null, null, daysAgo(62).plusDays(7), 62, 62, "범퍼 커버 양산용 ABS 레진 1차 발주",
                List.of(new ProductOrderItemSeed("poi-demo-001", "prd-demo-mat-005", amount("130"), amount("4680000")))),
            new ProductOrderSeed("po-demo-002", "poa-demo-002", "pos-demo-002", "pr-demo-002", "019a52d4-ab46-7abe-9071-025222fb6144", "PO-202601-002", "MATERIAL", MM_USER_ID,
                amount("3870000"), "APPROVAL", MM_ADMIN_ID, daysAgo(47), null, daysAgo(48).plusDays(6), 48, 46, "라디에이터 그릴 크롬 트림 스트립 조달",
                List.of(new ProductOrderItemSeed("poi-demo-002", "prd-demo-mat-013", amount("90"), amount("3870000")))),
            new ProductOrderSeed("po-demo-003", "poa-demo-003", "pos-demo-003", "pr-demo-003", "019a3df1-7843-7590-a5fd-94aa9aae7d0a", "PO-202601-003", "ITEM", MM_ADMIN_ID,
                amount("2700000"), "DELIVERING", MM_ADMIN_ID, daysAgo(30), null, daysAgo(34).plusDays(5), 34, 30, "범퍼 브래킷 모듈 선행 조달",
                List.of(new ProductOrderItemSeed("poi-demo-003", "prd-demo-sub-001", amount("12"), amount("2700000")))),
            new ProductOrderSeed("po-demo-004", "poa-demo-004", "pos-demo-004", "pr-demo-004", "019a52d4-7141-7a42-8674-a4c6597acfd7", "PO-202602-004", "MATERIAL", MM_USER_ID,
                amount("3420000"), "DELIVERED", MM_ADMIN_ID, daysAgo(22), daysAgo(20).toLocalDate(), daysAgo(26).plusDays(4), 26, 20, "램프 하우징 접착 자재 입고 완료",
                List.of(new ProductOrderItemSeed("poi-demo-004", "prd-demo-mat-016", amount("90"), amount("3420000")))),
            new ProductOrderSeed("po-demo-005", "poa-demo-005", "pos-demo-005", "pr-demo-005", "019a52d5-0df8-724b-a16f-7a9d3bcd5384", "PO-202602-005", "ITEM", MM_ADMIN_ID,
                amount("2052000"), "APPROVAL", MM_ADMIN_ID, daysAgo(18), null, daysAgo(19).plusDays(5), 19, 18, "도어 트림 클립 세트 긴급 발주",
                List.of(new ProductOrderItemSeed("poi-demo-005", "prd-demo-sub-003", amount("19"), amount("2052000")))),
            new ProductOrderSeed("po-demo-006", "poa-demo-006", "pos-demo-006", "pr-demo-006", "019a52d4-cffd-7876-9a7e-34590cc2c447", "PO-202603-006", "MATERIAL", MM_USER_ID,
                amount("1620000"), "PENDING", null, null, null, daysAgo(12).plusDays(6), 12, 12, "램프 렌즈 수지 2차 발주",
                List.of(new ProductOrderItemSeed("poi-demo-006", "prd-demo-mat-010", amount("30"), amount("1620000")))),
            new ProductOrderSeed("po-demo-007", "poa-demo-007", "pos-demo-007", "pr-demo-007", "019a52d4-ab46-7abe-9071-025222fb6144", "PO-202603-007", "ITEM", MM_USER_ID,
                amount("2736000"), "DELIVERING", MM_ADMIN_ID, daysAgo(5), null, daysAgo(7).plusDays(5), 7, 5, "트림 가니시 캐리어 납기 추적",
                List.of(new ProductOrderItemSeed("poi-demo-007", "prd-demo-sub-009", amount("18"), amount("2736000")))),
            new ProductOrderSeed("po-demo-008", "poa-demo-008", "pos-demo-008", "pr-demo-008", "019a52d4-96be-72cb-85dd-19fbe3d80880", "PO-202603-008", "MATERIAL", MM_ADMIN_ID,
                amount("3360000"), "DELIVERED", MM_ADMIN_ID, daysAgo(2), daysAgo(1).toLocalDate(), daysAgo(3).plusDays(3), 3, 1, "체결 하드웨어 보충 입고 완료",
                List.of(new ProductOrderItemSeed("poi-demo-008", "prd-demo-mat-014", amount("80"), amount("3360000"))))
        );
    }

    private List<StockLogSeed> stockLogSeeds() {
        return List.of(
            new StockLogSeed("log-demo-001", "stk-demo-005", amount("60"), amount("120"), amount("180"), "입고", null, "wh-demo-mat-001", MM_USER_ID, "PO-202512-001", "범퍼 커버 ABS 레진 입고", 61),
            new StockLogSeed("log-demo-002", "stk-demo-013", amount("20"), amount("48"), amount("68"), "입고", null, "wh-demo-mat-002", MM_USER_ID, "PO-202601-002", "그릴 크롬 트림 입고", 47),
            new StockLogSeed("log-demo-003", "stk-demo-017", amount("34"), amount("24"), amount("58"), "입고", null, "wh-demo-item-001", MM_ADMIN_ID, "PO-202601-003", "범퍼 브래킷 모듈 입고", 33),
            new StockLogSeed("log-demo-004", "stk-demo-016", amount("20"), amount("65"), amount("85"), "입고", null, "wh-demo-mat-001", MM_USER_ID, "PO-202602-004", "구조용 접착제 보충", 25),
            new StockLogSeed("log-demo-005", "stk-demo-019", amount("8"), amount("14"), amount("22"), "입고", null, "wh-demo-item-001", MM_ADMIN_ID, "PO-202602-005", "도어 트림 클립 세트 긴급입고", 18),
            new StockLogSeed("log-demo-006", "stk-demo-010", amount("18"), amount("56"), amount("74"), "입고", null, "wh-demo-mat-002", MM_USER_ID, "PO-202603-006", "램프 렌즈 수지 입고", 11),
            new StockLogSeed("log-demo-007", "stk-demo-025", amount("11"), amount("18"), amount("29"), "입고", null, "wh-demo-item-002", MM_USER_ID, "PO-202603-007", "트림 가니시 캐리어 입고", 5),
            new StockLogSeed("log-demo-008", "stk-demo-014", amount("2"), amount("38"), amount("40"), "입고", null, "wh-demo-qua-001", MM_ADMIN_ID, "PO-202603-008", "체결 하드웨어 검품 입고", 1),

            new StockLogSeed("log-demo-009", "stk-demo-027", amount("21"), amount("-5"), amount("16"), "출고", "wh-demo-item-001", null, PP_ADMIN_ID, "OR-202601-001", "범퍼 커버 양산 출고", 54),
            new StockLogSeed("log-demo-010", "stk-demo-028", amount("24"), amount("-5"), amount("19"), "출고", "wh-demo-item-001", null, PP_USER_ID, "OR-202601-002", "라디에이터 그릴 보충 출고", 40),
            new StockLogSeed("log-demo-011", "stk-demo-029", amount("16"), amount("-4"), amount("12"), "출고", "wh-demo-item-002", null, PP_USER_ID, "OR-202602-003", "도어 트림 조립 투입", 23),
            new StockLogSeed("log-demo-012", "stk-demo-030", amount("13"), amount("-4"), amount("9"), "출고", "wh-demo-item-002", null, PP_ADMIN_ID, "OR-202602-004", "램프 하우징 선행 출하", 16),
            new StockLogSeed("log-demo-013", "stk-demo-031", amount("8"), amount("-2"), amount("6"), "출고", "wh-demo-item-002", null, PP_ADMIN_ID, "OR-202603-005", "휠 아치 라이너 선출고", 6),
            new StockLogSeed("log-demo-014", "stk-demo-032", amount("14"), amount("-3"), amount("11"), "출고", "wh-demo-item-001", null, PP_USER_ID, "OR-202603-006", "배터리 브래킷 출고", 2),

            new StockLogSeed("log-demo-015", "stk-demo-023", amount("18"), amount("-7"), amount("11"), "이동", "wh-demo-item-001", "wh-demo-item-002", IM_ADMIN_ID, "TR-202603-001", "배터리 브래킷 베이스 조립창고 이송", 13),
            new StockLogSeed("log-demo-016", "stk-demo-026", amount("12"), amount("-5"), amount("7"), "이동", "wh-demo-item-001", "wh-demo-qua-002", IM_ADMIN_ID, "TR-202603-002", "와이어 가이드 브래킷 품질 보류", 10),
            new StockLogSeed("log-demo-017", "stk-demo-009", amount("44"), amount("-12"), amount("32"), "이동", "wh-demo-mat-002", "wh-demo-qua-001", IM_USER_ID, "TR-202603-003", "강화 글래스 인서트 파손 샘플 격리", 7),
            new StockLogSeed("log-demo-018", "stk-demo-020", amount("20"), amount("-6"), amount("14"), "이동", "wh-demo-item-001", "wh-demo-qua-002", IM_USER_ID, "TR-202603-004", "램프 하우징 프레임 외관 재검", 3)
        );
    }

    private List<BomSeed> bomSeeds() {
        return List.of(
            new BomSeed("bom-demo-001", "prd-demo-sub-001", "BOM-BBM-001", "범퍼 브래킷 모듈 서브어셈블리", 1, amount("1.5"), amount("260000"), amount("185000"), 10),
            new BomSeed("bom-demo-101", "prd-demo-fg-001", "BOM-FBC-001", "전방 범퍼 커버 양산 BOM", 2, amount("2.2"), amount("620000"), amount("420000"), 8),
            new BomSeed("bom-demo-102", "prd-demo-fg-002", "BOM-RDG-001", "라디에이터 그릴 보충 생산 BOM", 1, amount("1.8"), amount("540000"), amount("360000"), 6),
            new BomSeed("bom-demo-103", "prd-demo-fg-003", "BOM-DTA-001", "도어 트림 어셈블리 긴급 생산 BOM", 1, amount("2.4"), amount("575000"), amount("385000"), 4),
            new BomSeed("bom-demo-104", "prd-demo-fg-004", "BOM-LHH-001", "램프 하우징 출하 준비 BOM", 1, amount("2.1"), amount("650000"), amount("440000"), 2)
        );
    }

    private List<BomItemSeed> bomItemSeeds() {
        return List.of(
            new BomItemSeed("bmi-demo-001", "bom-demo-001", "MATERIAL", "prd-demo-mat-001", "KG", amount("1.2"), 10),
            new BomItemSeed("bmi-demo-002", "bom-demo-001", "MATERIAL", "prd-demo-mat-014", "SET", amount("1.0"), 10),
            new BomItemSeed("bmi-demo-003", "bom-demo-001", "MATERIAL", "prd-demo-mat-016", "KG", amount("0.8"), 10),

            new BomItemSeed("bmi-demo-101", "bom-demo-101", "ITEM", "bom-demo-001", "EA", amount("1.0"), 8),
            new BomItemSeed("bmi-demo-102", "bom-demo-101", "MATERIAL", "prd-demo-mat-005", "KG", amount("1.5"), 8),
            new BomItemSeed("bmi-demo-103", "bom-demo-101", "MATERIAL", "prd-demo-mat-013", "M", amount("0.6"), 8),
            new BomItemSeed("bmi-demo-104", "bom-demo-101", "MATERIAL", "prd-demo-mat-015", "L", amount("0.4"), 8),

            new BomItemSeed("bmi-demo-201", "bom-demo-102", "MATERIAL", "prd-demo-mat-002", "KG", amount("1.1"), 6),
            new BomItemSeed("bmi-demo-202", "bom-demo-102", "MATERIAL", "prd-demo-mat-013", "M", amount("0.8"), 6),
            new BomItemSeed("bmi-demo-203", "bom-demo-102", "MATERIAL", "prd-demo-mat-014", "SET", amount("1.0"), 6),

            new BomItemSeed("bmi-demo-301", "bom-demo-103", "MATERIAL", "prd-demo-mat-006", "KG", amount("1.4"), 4),
            new BomItemSeed("bmi-demo-302", "bom-demo-103", "MATERIAL", "prd-demo-mat-011", "M", amount("1.1"), 4),
            new BomItemSeed("bmi-demo-303", "bom-demo-103", "MATERIAL", "prd-demo-mat-014", "SET", amount("1.0"), 4),

            new BomItemSeed("bmi-demo-401", "bom-demo-104", "MATERIAL", "prd-demo-mat-009", "EA", amount("1.0"), 2),
            new BomItemSeed("bmi-demo-402", "bom-demo-104", "MATERIAL", "prd-demo-mat-010", "KG", amount("0.9"), 2),
            new BomItemSeed("bmi-demo-403", "bom-demo-104", "MATERIAL", "prd-demo-mat-016", "KG", amount("0.7"), 2)
        );
    }

    private List<RoutingSeed> routingSeeds() {
        return List.of(
            new RoutingSeed("rt-demo-001", "bmi-demo-001", "3", 1, 80, 10),
            new RoutingSeed("rt-demo-002", "bmi-demo-002", "5", 2, 45, 10),
            new RoutingSeed("rt-demo-003", "bmi-demo-003", "4", 3, 55, 10),
            new RoutingSeed("rt-demo-101", "bmi-demo-101", "4", 4, 120, 8),
            new RoutingSeed("rt-demo-102", "bmi-demo-102", "2", 5, 140, 8),
            new RoutingSeed("rt-demo-103", "bmi-demo-103", "6", 6, 70, 8),
            new RoutingSeed("rt-demo-104", "bmi-demo-104", "7", 7, 30, 8),
            new RoutingSeed("rt-demo-201", "bmi-demo-201", "3", 1, 95, 6),
            new RoutingSeed("rt-demo-202", "bmi-demo-202", "6", 2, 60, 6),
            new RoutingSeed("rt-demo-203", "bmi-demo-203", "8", 3, 25, 6),
            new RoutingSeed("rt-demo-301", "bmi-demo-301", "2", 1, 110, 4),
            new RoutingSeed("rt-demo-302", "bmi-demo-302", "4", 2, 85, 4),
            new RoutingSeed("rt-demo-303", "bmi-demo-303", "8", 3, 25, 4),
            new RoutingSeed("rt-demo-401", "bmi-demo-401", "4", 1, 75, 2),
            new RoutingSeed("rt-demo-402", "bmi-demo-402", "7", 2, 45, 2),
            new RoutingSeed("rt-demo-403", "bmi-demo-403", "8", 3, 20, 2)
        );
    }

    private List<BomExplosionSeed> bomExplosionSeeds() {
        return List.of(
            new BomExplosionSeed("bex-demo-001", "bom-demo-101", "prd-demo-sub-001", 1, amount("1.0"), "전방 범퍼 커버 > 범퍼 브래킷 모듈", "rt-demo-101", 8),
            new BomExplosionSeed("bex-demo-002", "bom-demo-101", "prd-demo-mat-001", 2, amount("1.2"), "전방 범퍼 커버 > 범퍼 브래킷 모듈 > 알루미늄 패널 시트 2.5T", "rt-demo-001", 8),
            new BomExplosionSeed("bex-demo-003", "bom-demo-101", "prd-demo-mat-014", 2, amount("1.0"), "전방 범퍼 커버 > 범퍼 브래킷 모듈 > 체결 하드웨어 키트", "rt-demo-002", 8),
            new BomExplosionSeed("bex-demo-004", "bom-demo-101", "prd-demo-mat-016", 2, amount("0.8"), "전방 범퍼 커버 > 범퍼 브래킷 모듈 > 구조용 접착제 팩", "rt-demo-003", 8),
            new BomExplosionSeed("bex-demo-005", "bom-demo-101", "prd-demo-mat-005", 1, amount("1.5"), "전방 범퍼 커버 > ABS 레진 펠렛", "rt-demo-102", 8),
            new BomExplosionSeed("bex-demo-006", "bom-demo-101", "prd-demo-mat-013", 1, amount("0.6"), "전방 범퍼 커버 > 크롬 트림 스트립", "rt-demo-103", 8),
            new BomExplosionSeed("bex-demo-007", "bom-demo-101", "prd-demo-mat-015", 1, amount("0.4"), "전방 범퍼 커버 > 표면 코팅 패키지", "rt-demo-104", 8),
            new BomExplosionSeed("bex-demo-008", "bom-demo-102", "prd-demo-mat-002", 1, amount("1.1"), "라디에이터 그릴 > 고장력 강판 코일", "rt-demo-201", 6),
            new BomExplosionSeed("bex-demo-009", "bom-demo-102", "prd-demo-mat-013", 1, amount("0.8"), "라디에이터 그릴 > 크롬 트림 스트립", "rt-demo-202", 6),
            new BomExplosionSeed("bex-demo-010", "bom-demo-102", "prd-demo-mat-014", 1, amount("1.0"), "라디에이터 그릴 > 체결 하드웨어 키트", "rt-demo-203", 6),
            new BomExplosionSeed("bex-demo-011", "bom-demo-103", "prd-demo-mat-006", 1, amount("1.4"), "도어 트림 어셈블리 > PP 레진 펠렛", "rt-demo-301", 4),
            new BomExplosionSeed("bex-demo-012", "bom-demo-103", "prd-demo-mat-011", 1, amount("1.1"), "도어 트림 어셈블리 > 실링 러버 스트립", "rt-demo-302", 4),
            new BomExplosionSeed("bex-demo-013", "bom-demo-103", "prd-demo-mat-014", 1, amount("1.0"), "도어 트림 어셈블리 > 체결 하드웨어 키트", "rt-demo-303", 4),
            new BomExplosionSeed("bex-demo-014", "bom-demo-104", "prd-demo-mat-009", 1, amount("1.0"), "램프 하우징 > 강화 글래스 인서트", "rt-demo-401", 2),
            new BomExplosionSeed("bex-demo-015", "bom-demo-104", "prd-demo-mat-010", 1, amount("0.9"), "램프 하우징 > 폴리카보네이트 렌즈 수지", "rt-demo-402", 2),
            new BomExplosionSeed("bex-demo-016", "bom-demo-104", "prd-demo-mat-016", 1, amount("0.7"), "램프 하우징 > 구조용 접착제 팩", "rt-demo-403", 2)
        );
    }

    private List<MesSeed> mesSeeds() {
        return List.of(
            new MesSeed("mes-demo-001", "qt-demo-003", "bom-demo-101", "prd-demo-fg-001", "MES-202601-001", 40, "PENDING", "4", LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), 0, 4),
            new MesSeed("mes-demo-002", "qt-demo-004", "bom-demo-102", "prd-demo-fg-002", "MES-202602-002", 32, "IN_PROGRESS", "6", LocalDate.now().minusDays(6), LocalDate.now().plusDays(1), 55, 6),
            new MesSeed("mes-demo-003", "qt-demo-005", "bom-demo-103", "prd-demo-fg-003", "MES-202602-003", 26, "COMPLETED", "8", LocalDate.now().minusDays(22), LocalDate.now().minusDays(15), 100, 8),
            new MesSeed("mes-demo-004", "qt-demo-007", "bom-demo-104", "prd-demo-fg-004", "MES-202603-004", 18, "IN_PROGRESS", "7", LocalDate.now().minusDays(3), LocalDate.now().plusDays(2), 72, 3),
            new MesSeed("mes-demo-005", "qt-demo-008", "bom-demo-101", "prd-demo-fg-001", "MES-202603-005", 12, "COMPLETED", "8", LocalDate.now().minusDays(12), LocalDate.now().minusDays(7), 100, 1)
        );
    }

    private record WarehouseSeed(String id, String internalUserId, String code, String name, String type, String location, String description, int weeksAgo) {}

    private record StockSeed(
        String id,
        String productId,
        String warehouseId,
        BigDecimal availableCount,
        BigDecimal safetyCount,
        BigDecimal reservedCount,
        BigDecimal forShipmentCount,
        String status,
        int weeksAgo
    ) {}

    private record ProductRequestSeed(
        String id,
        String approvalId,
        String code,
        String type,
        String requesterId,
        BigDecimal totalPrice,
        String status,
        String approvedBy,
        String rejectedReason,
        int daysAgo,
        List<ProductRequestItemSeed> items
    ) {
        private LocalDateTime approvedAt() {
            return approvedBy == null ? null : DemoScenarioInitializer.daysAgo(Math.max(daysAgo - 1, 0));
        }
    }

    private record ProductRequestItemSeed(
        String id,
        String productId,
        BigDecimal count,
        BigDecimal price,
        LocalDateTime preferredDeliveryDate,
        String purpose,
        String note
    ) {}

    private record ProductOrderSeed(
        String id,
        String approvalId,
        String shipmentId,
        String requestId,
        String supplierCompanyId,
        String code,
        String type,
        String requesterId,
        BigDecimal totalPrice,
        String status,
        String approvedBy,
        LocalDateTime deliveredAtDateTime,
        LocalDate actualDelivery,
        LocalDateTime dueDate,
        int daysAgo,
        int updatedDaysAgo,
        String note,
        List<ProductOrderItemSeed> items
    ) {
        private LocalDate deliveredAt() {
            return deliveredAtDateTime == null ? null : deliveredAtDateTime.toLocalDate();
        }

        private LocalDate expectedDelivery() {
            return dueDate.toLocalDate();
        }

        private LocalDateTime approvedAt() {
            return approvedBy == null ? null : DemoScenarioInitializer.daysAgo(Math.max(daysAgo - 1, 0));
        }

        private String rejectedReason() {
            return null;
        }
    }

    private record ProductOrderItemSeed(String id, String productId, BigDecimal count, BigDecimal price) {}

    private record StockLogSeed(
        String id,
        String stockId,
        BigDecimal previousCount,
        BigDecimal changeCount,
        BigDecimal currentCount,
        String movementType,
        String fromWarehouseId,
        String toWarehouseId,
        String createdById,
        String referenceCode,
        String note,
        int daysAgo
    ) {}

    private record BomSeed(
        String id,
        String productId,
        String code,
        String description,
        Integer version,
        BigDecimal leadTime,
        BigDecimal sellingPrice,
        BigDecimal originPrice,
        int weeksAgo
    ) {}

    private record BomItemSeed(String id, String bomId, String componentType, String componentId, String unit, BigDecimal count, int weeksAgo) {}

    private record RoutingSeed(String id, String bomItemId, String operationId, Integer sequence, Integer requiredTime, int weeksAgo) {}

    private record BomExplosionSeed(
        String id,
        String parentBomId,
        String componentProductId,
        Integer level,
        BigDecimal totalRequiredCount,
        String path,
        String routingId,
        int weeksAgo
    ) {}

    private record MesSeed(
        String id,
        String quotationId,
        String bomId,
        String productId,
        String mesNumber,
        Integer quantity,
        String status,
        String currentOperationId,
        LocalDate startDate,
        LocalDate endDate,
        Integer progressRate,
        int daysAgo
    ) {}
}
