//package org.ever._4ever_be_gw.mockdata.scmpp.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import org.ever._4ever_be_gw.common.dto.PageDto;
//import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
//import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.ProductionSalesOrderDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.ReadyToShipSalesOrderDto;
//import org.ever._4ever_be_gw.common.response.ApiResponse;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.*;
//import org.ever._4ever_be_gw.common.exception.BusinessException;
//import org.ever._4ever_be_gw.common.exception.ErrorCode;
//import org.ever._4ever_be_gw.scm.PeriodStatDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.inventory.*;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.warehouse.UpdateWarehouseRequestDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.warehouse.WarehouseDetailDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.warehouse.WarehouseDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.warehouse.WarehouseStatisticDto;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/scm-pp")
//@Tag(name = "재고관리(IM)", description = "재고 관리 API")
//public class ImController {
//
//    private static final Set<String> INVENTORY_STAT_PERIODS = Set.of("week", "month", "quarter", "year");
//    private static final String[] ITEM_CATEGORIES = {"원자재", "부품", "반제품", "완제품", "소모품"};
//    private static final String[] WAREHOUSE_NAMES = {"본사창고", "부산창고", "인천창고", "광주창고", "대구창고"};
//    private static final String[] WAREHOUSE_TYPES = {"원자재창고", "부품창고", "완제품창고"};
//    private static final String[] UOM_NAMES = {"EA", "KG", "M", "L", "SET"};
//    private static final String[] ITEM_NAMES = {"강판", "알루미늄", "볼트", "너트", "패널", "모터", "엔진", "전자회로", "배터리", "철판"};
//    private static final String[] SUPPLIER_NAMES = {"대한철강", "한국알루미늄", "포스코", "효성중공업", "현대제철", "두산중공업", "세아베스틸", "KG동부제철", "동국제강", "티엠씨메탈"};
//    private static final String[] CUSTOMER_NAMES = {"삼성전자", "LG전자", "현대자동차", "기아자동차", "SK하이닉스", "포스코", "한화", "롯데케미칼", "대우조선해양", "두산인프라코어"};
//    private static final Random random = new Random(1);  // Fixed seed for reproducible results
//
//
//    @GetMapping("/iv/shortage/count/critical/statistic")
//    @Operation(
//            summary = "재고 부족 관리 통계",
//            description = "긴급 및 주의 재고 부족 품목 수를 주간, 월간, 분기, 연간 단위로 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<StatsResponseDto<StatsMetricsDto>>> getShortageStatistics() {
//        StatsResponseDto.StatsResponseDtoBuilder<StatsMetricsDto> builder = StatsResponseDto.<StatsMetricsDto>builder();
//
//        // 주간 통계
//        builder.week(StatsMetricsDto.builder()
//                .put("total_emergency", PeriodStatDto.builder().value(8L).deltaRate(new BigDecimal("0.12")).build())
//                .put("total_warning", PeriodStatDto.builder().value(15L).deltaRate(new BigDecimal("0.08")).build())
//                .build());
//
//        // 월간 통계
//        builder.month(StatsMetricsDto.builder()
//                .put("total_emergency", PeriodStatDto.builder().value(32L).deltaRate(new BigDecimal("0.09")).build())
//                .put("total_warning", PeriodStatDto.builder().value(60L).deltaRate(new BigDecimal("0.07")).build())
//                .build());
//
//        // 분기별 통계
//        builder.quarter(StatsMetricsDto.builder()
//                .put("total_emergency", PeriodStatDto.builder().value(90L).deltaRate(new BigDecimal("0.06")).build())
//                .put("total_warning", PeriodStatDto.builder().value(180L).deltaRate(new BigDecimal("0.05")).build())
//                .build());
//
//        // 연간 통계
//        builder.year(StatsMetricsDto.builder()
//                .put("total_emergency", PeriodStatDto.builder().value(360L).deltaRate(new BigDecimal("0.04")).build())
//                .put("total_warning", PeriodStatDto.builder().value(720L).deltaRate(new BigDecimal("0.03")).build())
//                .build());
//
//        StatsResponseDto<StatsMetricsDto> response = builder.build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "재고 부족 통계 정보를 조회했습니다.", HttpStatus.OK));
//    }
//
//
//    @GetMapping("/iv/shortage/preview")
//    @Operation(
//            summary = "부족 재고 간단 조회",
//            description = "재고 부족 목록을 간략하게 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getShortageItemsPreview() {
//        List<ShortageItemPreviewDto> items = Arrays.asList(
//                ShortageItemPreviewDto.builder()
//                        .itemId("1")
//                        .itemName("강판")
//                        .currentStock(50)
//                        .uomName("EA")
//                        .safetyStock(100)
//                        .statusCode("URGENT")
//                        .build(),
//                ShortageItemPreviewDto.builder()
//                        .itemId("2")
//                        .itemName("알루미늄 프로파일")
//                        .currentStock(25)
//                        .uomName("M")
//                        .safetyStock(50)
//                        .statusCode("CAUTION")
//                        .build(),
//                ShortageItemPreviewDto.builder()
//                        .itemId("3")
//                        .itemName("스테인리스 파이프")
//                        .currentStock(8)
//                        .uomName("EA")
//                        .safetyStock(20)
//                        .statusCode("URGENT")
//                        .build()
//        );
//
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", items);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "재고 부족 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//    @GetMapping("/iv/stock-transfers")
//    @Operation(
//            summary = "재고이동 목록 조회",
//            description = "재고이동 목록을 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getStockTransferList(
//    ) {
//        List<StockTransferDto> items = Arrays.asList(
//                StockTransferDto.builder()
//                        .type("입고")
//                        .quantity(50)
//                        .uomName("EA")
//                        .itemName("스테인리스 스틸 파이프")
//                        .workDate(LocalDateTime.parse("2024-01-15T09:15"))
//                        .managerName("김구매")
//                        .build(),
//                StockTransferDto.builder()
//                        .type("출고")
//                        .quantity(200)
//                        .uomName("EA")
//                        .itemName("볼트 M8x20")
//                        .workDate(LocalDateTime.parse("2024-01-15T09:15"))
//                        .managerName("이생산")
//                        .build(),
//                StockTransferDto.builder()
//                        .type("입고")
//                        .quantity(100)
//                        .uomName("M")
//                        .itemName("알루미늄 프로파일")
//                        .workDate(LocalDateTime.parse("2024-01-15T09:15"))
//                        .managerName("김구매")
//                        .build(),
//                StockTransferDto.builder()
//                        .type("출고")
//                        .quantity(10)
//                        .uomName("EA")
//                        .itemName("베어링 6205")
//                        .workDate(LocalDateTime.parse("2024-01-15T09:15"))
//                        .managerName("이생산")
//                        .build(),
//                StockTransferDto.builder()
//                        .type("입고")
//                        .quantity(50)
//                        .uomName("EA")
//                        .itemName("박우진")
//                        .workDate(LocalDateTime.parse("2024-01-15T09:15"))
//                        .managerName("권오윤")
//                        .build()
//        );
//
//        PageDto pageInfo = PageDto.builder()
//                .number(0)
//                .size(5)
//                .totalElements(5)
//                .totalPages(1)
//                .hasNext(false)
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", items);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "재고 이력 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    @PostMapping("/iv/stock-transfers")
//    @Operation(
//            summary = "창고간 재고 이동 생성",
//            description = "창고간 재고 이동을 생성합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Void>> createStockTransfer(
//            @RequestBody CreateStockTransferDto request
//    ) {
//        return ResponseEntity.ok(ApiResponse.success(null, "창고간 재고 이동이 완료되었습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/iv/warehouses/statistic")
//    @Operation(
//            summary = "창고 관리 통계",
//            description = "창고 관리 통계를 주간, 월간, 분기, 연간 단위로 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<StatsResponseDto<WarehouseStatisticDto>>> getWarehouseStatistics() {
//
//        StatsResponseDto.StatsResponseDtoBuilder<WarehouseStatisticDto> builder = StatsResponseDto.<WarehouseStatisticDto>builder();
//
//        // 주간
//        builder.week(WarehouseStatisticDto.builder()
//                .totalWarehouse(WarehouseStatisticDto.TotalWarehouseDto.builder()
//                        .value("15")
//                        .delta_rate(1)
//                        .build())
//                .inOperationWarehouse(WarehouseStatisticDto.InOperationWarehouseDto.builder()
//                        .value(13)
//                        .delta_rate(1)
//                        .build())
//                .build());
//
//        // 월간
//        builder.month(WarehouseStatisticDto.builder()
//                .totalWarehouse(WarehouseStatisticDto.TotalWarehouseDto.builder()
//                        .value("16")
//                        .delta_rate(1)
//                        .build())
//                .inOperationWarehouse(WarehouseStatisticDto.InOperationWarehouseDto.builder()
//                        .value(14)
//                        .delta_rate(1)
//                        .build())
//                .build());
//
//        // 분기
//        builder.quarter(WarehouseStatisticDto.builder()
//                .totalWarehouse(WarehouseStatisticDto.TotalWarehouseDto.builder()
//                        .value("17")
//                        .delta_rate(1)
//                        .build())
//                .inOperationWarehouse(WarehouseStatisticDto.InOperationWarehouseDto.builder()
//                        .value(15)
//                        .delta_rate(1)
//                        .build())
//                .build());
//
//        // 연간
//        builder.year(WarehouseStatisticDto.builder()
//                .totalWarehouse(WarehouseStatisticDto.TotalWarehouseDto.builder()
//                        .value("18")
//                        .delta_rate(1)
//                        .build())
//                .inOperationWarehouse(WarehouseStatisticDto.InOperationWarehouseDto.builder()
//                        .value(16)
//                        .delta_rate(1)
//                        .build())
//                .build());
//
//        StatsResponseDto<WarehouseStatisticDto> response = builder.build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "창고 현황을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//    @GetMapping("/iv/warehouses/{warehouseId}")
//    @Operation(
//            summary = "창고 상세 조회",
//            description = "창고 상세 정보를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<WarehouseDetailDto>> getWarehouseDetail(
//            @Parameter(name = "warehouseId", description = "창고 ID")
//            @PathVariable String warehouseId
//    ) {
//        WarehouseDetailDto response = WarehouseDetailDto.builder()
//                .warehouseInfo(WarehouseDetailDto.WarehouseInfoDto.builder()
//                        .warehouseName("제1창고")
//                        .warehouseNumber("WH-A")
//                        .warehouseType("원자재")
//                        .statusCode("ACTIVE")
//                        .location("경기도 안산시 단원구 중앙대로 123")
//                        .description("원자재 전용 창고입니다.")
//                        .build())
//                .manager(WarehouseDetailDto.WarehouseManagerDto.builder()
//                        .managerId("123")
//                        .managerName("김창고")
//                        .managerPhoneNumber("031-123-4567")
//                        .managerEmail("kim@example.com")
//                        .build())
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "창고 상세 정보를 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/iv/warehouses")
//    @Operation(
//            summary = "창고 목록 조회",
//            description = "창고 목록을 조회합니다. (페이징 지원)"
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getWarehouseList(
//            @Parameter(name = "page", description = "페이지 번호")
//            @RequestParam(required = false, defaultValue = "0") int page,
//            @Parameter(name = "size", description = "페이지 크기")
//            @RequestParam(required = false, defaultValue = "10") int size
//    ) {
//        List<WarehouseDto> items = new ArrayList<>();
//        for (int i = 1; i <= 20; i++) {
//            items.add(WarehouseDto.builder()
//                    .warehouseId(String.valueOf(i))
//                    .warehouseNumber("WH-" + (char)('A' + (i % 5)))
//                    .warehouseName("창고" + i)
//                    .statusCode(i % 2 == 0 ? "ACTIVE" : "INACTIVE")
//                    .warehouseType(i % 3 == 0 ? "원자재" : i % 3 == 1 ? "완제품" : "부품")
//                    .location("경기도 안산시 단원구 중앙대로 " + (100 + i))
//                    .manager("담당자" + i)
//                    .managerPhone("010-1000-" + String.format("%04d", i))
//                    .build());
//        }
//
//        int total = items.size();
//        int fromIdx = Math.min(page * size, total);
//        int toIdx = Math.min(fromIdx + size, total);
//        List<WarehouseDto> pageContent = items.subList(fromIdx, toIdx);
//
//        PageDto pageInfo = PageDto.builder()
//                .number(page)
//                .size(size)
//                .totalElements(total)
//                .totalPages((int) Math.ceil((double) total / size))
//                .hasNext(toIdx < total)
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", pageContent);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "창고 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    @PostMapping("/iv/warehouses")
//    @Operation(
//            summary = "창고 추가",
//            description = "새로운 창고를 추가합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Void>> createWarehouse(
//            @RequestBody CreateWarehouseDto request
//    ) {
//        return ResponseEntity.ok(ApiResponse.success(null, "창고가 추가되었습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/iv/items/{itemId}")
//    @Operation(
//            summary = "재고 상세 조회",
//            description = "재고 상세 정보를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<ItemDetailDto>> getItemDetail(
//            @Parameter(name = "itemId", description = "품목 ID")
//            @PathVariable String itemId
//    ) {
//        List<ItemDetailDto.StockMovementDto> stockMovements = Arrays.asList(
//                ItemDetailDto.StockMovementDto.builder()
//                        .type("입고")
//                        .quantity(50)
//                        .uomName("EA")
//                        .from(null)
//                        .to("제1창고 (A-01-01)")
//                        .movementDate(LocalDateTime.parse("2024-01-15T09:15"))
//                        .managerName("김구매")
//                        .referenceNumber("TR-2024-001")
//                        .note("정기 구매입고")
//                        .build(),
//                ItemDetailDto.StockMovementDto.builder()
//                        .type("이동")
//                        .quantity(20)
//                        .uomName("EA")
//                        .from("제1창고 (A-01-01)")
//                        .to("제2창고 (C-02-05)")
//                        .movementDate(LocalDateTime.parse("2024-01-15T09:15"))
//                        .managerName("이관리")
//                        .referenceNumber("TR-2024-002")
//                        .note("생산 라인 공급을 위한 이동")
//                        .build(),
//                ItemDetailDto.StockMovementDto.builder()
//                        .type("출고")
//                        .quantity(30)
//                        .uomName("EA")
//                        .from("제1창고 (A-01-01)")
//                        .to(null)
//                        .movementDate(LocalDateTime.parse("2024-01-15T09:15"))
//                        .managerName("박생산")
//                        .referenceNumber("WO-2024-001")
//                        .note("제품 생산을 위한 출고")
//                        .build()
//        );
//
//        ItemDetailDto response = ItemDetailDto.builder()
//                .itemId(itemId)
//                .itemNumber("SS-PIPE-001")
//                .itemName("스테인리스 스틸 파이프")
//                .category("원자재")
//                .supplierCompanyName("스테인리스코리아")
//                .statusCode("NORMAL")
//                .currentStock(150)
//                .safetyStock(30)
//                .uomName("EA")
//                .unitPrice(25000)
//                .totalAmount(3750000)
//                .warehouseId("123")
//                .warehouseName("제1창고")
//                .warehouseNumber("A-01-01")
//                .location("서울시 금천구")
//                .lastModified(LocalDateTime.parse("2024-01-15T09:15"))
//                .description("고품질 스테인리스 스틸 파이프, 내식성 우수")
//                .stockMovements(stockMovements)
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "재고 상세 정보를 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/iv/statistic")
//    @Operation(
//            summary = "IM 통계 조회",
//            description = "재고 및 입출고 현황 통계를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<StatsResponseDto<StatsMetricsDto>>> getInventoryStatistic(
//    ) {
//        String periods = null;
//        List<String> requested = List.of("week", "month", "quarter", "year");
//
//        List<String> invalid = requested.stream().filter(p -> !INVENTORY_STAT_PERIODS.contains(p)).toList();
//        if (periods != null && !periods.isBlank() && (!invalid.isEmpty() || requested.stream().noneMatch(INVENTORY_STAT_PERIODS::contains))) {
//            throw new BusinessException(ErrorCode.INVALID_PERIODS);
//        }
//
//        List<String> finalPeriods = requested.stream().filter(INVENTORY_STAT_PERIODS::contains).toList();
//
//        StatsResponseDto.StatsResponseDtoBuilder<StatsMetricsDto> builder = StatsResponseDto.<StatsMetricsDto>builder();
//
//        if (finalPeriods.contains("week")) {
//            builder.week(StatsMetricsDto.builder()
//                    .put("total_stock", PeriodStatDto.builder().value(240_000_000L).deltaRate(new BigDecimal("0.082")).build())
//                    .put("store_complete", PeriodStatDto.builder().value(156L).deltaRate(new BigDecimal("0.12")).build())
//                    .put("store_pending", PeriodStatDto.builder().value(23L).deltaRate(new BigDecimal("0.05")).build())
//                    .put("delivery_complete", PeriodStatDto.builder().value(89L).deltaRate(new BigDecimal("0.07")).build())
//                    .put("delivery_pending", PeriodStatDto.builder().value(14L).deltaRate(new BigDecimal("-0.03")).build())
//                    .build());
//        }
//        if (finalPeriods.contains("month")) {
//            builder.month(StatsMetricsDto.builder()
//                    .put("total_stock", PeriodStatDto.builder().value(955_000_000L).deltaRate(new BigDecimal("0.096")).build())
//                    .put("store_complete", PeriodStatDto.builder().value(612L).deltaRate(new BigDecimal("0.087")).build())
//                    .put("store_pending", PeriodStatDto.builder().value(88L).deltaRate(new BigDecimal("0.042")).build())
//                    .put("delivery_complete", PeriodStatDto.builder().value(374L).deltaRate(new BigDecimal("0.058")).build())
//                    .put("delivery_pending", PeriodStatDto.builder().value(57L).deltaRate(new BigDecimal("-0.021")).build())
//                    .build());
//        }
//        if (finalPeriods.contains("quarter")) {
//            builder.quarter(StatsMetricsDto.builder()
//                    .put("total_stock", PeriodStatDto.builder().value(2_865_000_000L).deltaRate(new BigDecimal("0.074")).build())
//                    .put("store_complete", PeriodStatDto.builder().value(1_845L).deltaRate(new BigDecimal("0.069")).build())
//                    .put("store_pending", PeriodStatDto.builder().value(275L).deltaRate(new BigDecimal("0.038")).build())
//                    .put("delivery_complete", PeriodStatDto.builder().value(1_118L).deltaRate(new BigDecimal("0.049")).build())
//                    .put("delivery_pending", PeriodStatDto.builder().value(171L).deltaRate(new BigDecimal("-0.027")).build())
//                    .build());
//        }
//        if (finalPeriods.contains("year")) {
//            builder.year(StatsMetricsDto.builder()
//                    .put("total_stock", PeriodStatDto.builder().value(11_540_000_000L).deltaRate(new BigDecimal("0.061")).build())
//                    .put("store_complete", PeriodStatDto.builder().value(7_312L).deltaRate(new BigDecimal("0.055")).build())
//                    .put("store_pending", PeriodStatDto.builder().value(1_142L).deltaRate(new BigDecimal("0.033")).build())
//                    .put("delivery_complete", PeriodStatDto.builder().value(4_572L).deltaRate(new BigDecimal("0.044")).build())
//                    .put("delivery_pending", PeriodStatDto.builder().value(682L).deltaRate(new BigDecimal("-0.019")).build())
//                    .build());
//        }
//
//        StatsResponseDto<StatsMetricsDto> response = builder.build();
//        return ResponseEntity.ok(ApiResponse.success(response, "재고 및 입출고 현황을 조회했습니다.", HttpStatus.OK));
//    }
//
//    @PatchMapping("/sales-orders/{salesOrderId}/status")
//    @Operation(
//            summary ="배송중으로 상태 변경",
//            description = "출고 준비완료를 배송중 상태로 변경합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<SalesOrderStatusDto>> updateOrderStatus(
//            @Parameter(name = "salesOrderId", description = "주문 ID")
//            @PathVariable String salesOrderId
//    ) {
//        SalesOrderStatusDto response = SalesOrderStatusDto.builder()
//                .salesOrderId(salesOrderId)
//                .salesOrderCode("SO-2024-001")
//                .status("DELIVERING")
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "주문 상태가 배송중 변경되었습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/sales-orders/ready-to-ship/{salesOrderId}")
//    @Operation(
//            summary = "출고 준비완료 상세보기",
//            description = "출고 준비 완료된 주문의 상세 정보를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<ReadyToShipDetailDto>> getReadyToShipDetail(
//            @Parameter(name = "salesOrderId", description = "주문 ID")
//            @PathVariable String salesOrderId
//    ) {
//        List<ReadyToShipDetailDto.OrderItemDto> orderItems = Arrays.asList(
//                ReadyToShipDetailDto.OrderItemDto.builder()
//                        .itemName("볼트")
//                        .quantity(500)
//                        .uomName("EA")
//                        .build(),
//                ReadyToShipDetailDto.OrderItemDto.builder()
//                        .itemName("베어링 6205")
//                        .quantity(20)
//                        .uomName("EA")
//                        .build()
//        );
//
//        ReadyToShipDetailDto response = ReadyToShipDetailDto.builder()
//                .salesOrderId(salesOrderId)
//                .salesOrderNumber("SO-2024-002")
//                .customerCompanyName("현대건설")
//                .dueDate(LocalDateTime.parse("2024-01-15T09:15"))
//                .statusCode("READY_FOR_SHIPMENT")
//                .orderItems(orderItems)
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "출고 준비 완료 주문 상세를 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/sales-orders/production/{salesOrderId}")
//    @Operation(
//            summary = "생산중 상세보기",
//            description = "생산 중인 주문의 상세 정보를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<ReadyToShipDetailDto>> getProductionOrderDetail(
//            @Parameter(name = "salesOrderId", description = "주문 ID")
//            @PathVariable String salesOrderId
//    ) {
//        List<ReadyToShipDetailDto.OrderItemDto> orderItems = Arrays.asList(
//                ReadyToShipDetailDto.OrderItemDto.builder()
//                        .itemName("스테인리스 파이프")
//                        .quantity(100)
//                        .uomName("EA")
//                        .build(),
//                ReadyToShipDetailDto.OrderItemDto.builder()
//                        .itemName("알루미늄 프로파일")
//                        .quantity(50)
//                        .uomName("M")
//                        .build()
//        );
//
//        ReadyToShipDetailDto response = ReadyToShipDetailDto.builder()
//                .salesOrderId(salesOrderId)
//                .salesOrderNumber("SO-2024-001")
//                .customerCompanyName("대한제철")
//                .dueDate(LocalDateTime.parse("2024-01-15T09:15"))
//                .statusCode("IN_PRODUCTION")
//                .orderItems(orderItems)
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "주문 상세 정보를 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/iv/inventory-items")
//    @Operation(
//            summary = "재고 목록 조회",
//            description = "재고 목록을 조회합니다. 타입(WAREHOUSE_NAME, ITEM_NAME)과 키워드로 검색, 상태코드(ALL, NORMAL, CAUTION, URGENT) 필터링 가능"
//    )
//    public ResponseEntity<ApiResponse<Object>> getInventoryItems(
//            @Parameter(description = "검색 타입: WAREHOUSE_NAME 또는 ITEM_NAME")
//            @RequestParam(name = "type", required = false) String type,
//            @Parameter(description = "검색 키워드")
//            @RequestParam(name = "keyword", required = false) String keyword,
//            @Parameter(description = "재고 상태: ALL, NORMAL, CAUTION, URGENT")
//            @RequestParam(name = "statusCode", required = false, defaultValue = "ALL") String statusCode,
//            @Parameter(description = "페이지 번호")
//            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
//            @Parameter(description = "페이지 크기")
//            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
//    ) {
//        List<InventoryItemDto> allItems = new ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            String itemCategory = ITEM_CATEGORIES[i % ITEM_CATEGORIES.length];
//            String warehouse = WAREHOUSE_NAMES[i % WAREHOUSE_NAMES.length];
//            String warehouseType = WAREHOUSE_TYPES[i % WAREHOUSE_TYPES.length];
//            String uom = UOM_NAMES[i % UOM_NAMES.length];
//            String itemNameValue = ITEM_NAMES[i % ITEM_NAMES.length] + " " + (i + 1);
//
//            int currentStock = 20 + (i * 5) % 300;
//            int safetyStock = 100;
//            String statusValue;
//            if (currentStock < safetyStock * 0.5) statusValue = "URGENT";
//            else if (currentStock < safetyStock) statusValue = "CAUTION";
//            else statusValue = "NORMAL";
//            int unitPrice = 5000 + (i * 1000) % 30000;
//
//            allItems.add(InventoryItemDto.builder()
//                    .itemId(String.valueOf(1001 + i))
//                    .itemNumber("ITEM-" + (1001 + i))
//                    .itemName(itemNameValue)
//                    .category(itemCategory)
//                    .currentStock(currentStock)
//                    .safetyStock(safetyStock)
//                    .uomName(uom)
//                    .unitPrice(unitPrice)
//                    .totalAmount(currentStock * unitPrice)
//                    .warehouseName(warehouse)
//                    .warehouseType(warehouseType)
//                    .statusCode(statusValue)
//                    .build());
//        }
//
//        // 필터링
//        List<InventoryItemDto> filteredItems = allItems.stream()
//                .filter(item -> {
//                    if (statusCode == null || statusCode.equalsIgnoreCase("ALL")) return true;
//                    return item.getStatusCode().equalsIgnoreCase(statusCode.trim());
//                })
//                .filter(item -> {
//                    if (type == null || keyword == null || keyword.isBlank()) return true;
//                    if (type.equalsIgnoreCase("WAREHOUSE_NAME")) {
//                        return item.getWarehouseName().toLowerCase().contains(keyword.toLowerCase().trim());
//                    } else if (type.equalsIgnoreCase("ITEM_NAME")) {
//                        return item.getItemName().toLowerCase().contains(keyword.toLowerCase().trim());
//                    }
//                    return true;
//                })
//                .collect(Collectors.toList());
//
//        // 페이지네이션
//        int fromIndex = Math.min(page * size, filteredItems.size());
//        int toIndex = Math.min(fromIndex + size, filteredItems.size());
//        List<InventoryItemDto> pagedItems = filteredItems.subList(fromIndex, toIndex);
//
//        PageDto pageInfo = PageDto.builder()
//                .number(page)
//                .size(size)
//                .totalElements(filteredItems.size())
//                .totalPages((int) Math.ceil((double) filteredItems.size() / size))
//                .hasNext(toIndex < filteredItems.size())
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", pagedItems);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "재고 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//    @GetMapping("/iv/shortage")
//    @Operation(
//            summary = "부족 재고 목록 조회",
//            description = "부족 재고 목록을 조회합니다. (statusCode=ALL, URGENT, CAUTION으로 필터링 가능)",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getShortageItems(
//            @Parameter(description = "재고 상태: ALL, URGENT 또는 CAUTION")
//            @RequestParam(name = "statusCode", required = false, defaultValue = "ALL") String statusCode,
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
//            @Parameter(description = "페이지 크기")
//            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
//    ) {
//        List<ShortageItemDetailDto> allItems = new ArrayList<>();
//
//        for (int i = 0; i < 50; i++) {
//            String itemCategory = ITEM_CATEGORIES[i % ITEM_CATEGORIES.length];
//            String warehouse = WAREHOUSE_NAMES[i % WAREHOUSE_NAMES.length];
//            String warehouseNumber = "WH-" + (100 + i % 5);
//            String uom = UOM_NAMES[i % UOM_NAMES.length];
//            String itemNameValue = ITEM_NAMES[i % ITEM_NAMES.length] + " " + (i + 1);
//
//            Random random = new Random();
//
//            int safetyStock = 100 + random.nextInt(50); // 100~149
//            int currentStock = random.nextInt(150);     // 0~149
//            if (currentStock < 0) currentStock = 5;
//
//            String statusValue = currentStock < safetyStock * 0.5 ? "URGENT" : "CAUTION";
//
//            int unitPrice = 5000 + (i * 1000) % 30000;
//
//            ShortageItemDetailDto item = ShortageItemDetailDto.builder()
//                    .itemId(String.valueOf(1001 + i))
//                    .itemName(itemNameValue)
//                    .itemNumber("ITEM-" + (1001 + i))
//                    .category(itemCategory)
//                    .currentStock(currentStock)
//                    .safetyStock(safetyStock)
//                    .uomName(uom)
//                    .unitPrice(unitPrice)
//                    .totalAmount(currentStock * unitPrice)
//                    .warehouseName(warehouse)
//                    .warehouseNumber(warehouseNumber)
//                    .statusCode(statusValue)
//                    .build();
//
//            allItems.add(item);
//        }
//
//        // 필터링 (ALL이면 전체, 아니면 해당 상태만)
//        List<ShortageItemDetailDto> filteredItems = allItems.stream()
//                .filter(item -> {
//                    if (statusCode == null || statusCode.equalsIgnoreCase("ALL")) return true;
//                    return item.getStatusCode().equalsIgnoreCase(statusCode.trim());
//                })
//                .collect(Collectors.toList());
//
//        int fromIndex = Math.min(page * size, filteredItems.size());
//        int toIndex = Math.min(fromIndex + size, filteredItems.size());
//        List<ShortageItemDetailDto> pagedItems = filteredItems.subList(fromIndex, toIndex);
//
//        PageDto pageInfo = PageDto.builder()
//                .number(page)
//                .size(size)
//                .totalElements(filteredItems.size())
//                .totalPages((int) Math.ceil((double) filteredItems.size() / size))
//                .hasNext(toIndex < filteredItems.size())
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", pagedItems);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "부족 재고 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//    @GetMapping("/purchase-orders/receiving")
//    @Operation(
//            summary = "입고 대기 목록 조회"
//    )
//    public ResponseEntity<ApiResponse<Object>> getPendingPurchaseOrders(
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
//            @Parameter(description = "페이지 크기")
//            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
//    ) {
//
//        // --- 더미 데이터 생성 ---
//        List<PendingPurchaseOrderDto> allOrders = new ArrayList<>();
//        LocalDateTime baseDate = LocalDateTime.now();
//
//        for (int i = 0; i < 50; i++) {
//            String supplier = SUPPLIER_NAMES[i % SUPPLIER_NAMES.length];
//
//            LocalDateTime orderDate = baseDate.minusDays(i % 30);
//            LocalDateTime dueDate = orderDate.plusDays(7 + (i % 14)); // Due date 1~3주 후
//            int totalAmount = 500000 + (i * 100000) % 4500000;
//
//            allOrders.add(PendingPurchaseOrderDto.builder()
//                    .purchaseOrderId(String.valueOf(2001 + i))
//                    .purchaseOrderNumber("PO-2025-" + String.format("%04d", i + 1))
//                    .supplierCompanyName(supplier)
//                    .orderDate(orderDate)
//                    .dueDate(dueDate)
//                    .totalAmount(totalAmount)
//                    .statusCode("RECEIVING")
//                    .build());
//        }
//
//        // --- 특정 날짜까지만 필터링 (2025-10-21T22:18 이전) ---
//        LocalDateTime cutoffDate = LocalDateTime.of(2025, 10, 21, 22, 18);
//        List<PendingPurchaseOrderDto> filteredOrders = allOrders.stream()
//                .filter(order -> order.getOrderDate().isBefore(cutoffDate) || order.getOrderDate().isEqual(cutoffDate))
//                .collect(Collectors.toList());
//
//        // --- 페이지네이션 ---
//        int fromIndex = Math.min(page * size, filteredOrders.size());
//        int toIndex = Math.min(fromIndex + size, filteredOrders.size());
//        List<PendingPurchaseOrderDto> pagedOrders = filteredOrders.subList(fromIndex, toIndex);
//
//        PageDto pageInfo = PageDto.builder()
//                .number(page)
//                .size(size)
//                .totalElements(filteredOrders.size())
//                .totalPages((int) Math.ceil((double) filteredOrders.size() / size))
//                .hasNext(toIndex < filteredOrders.size())
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", pagedOrders);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "입고대기 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//    @GetMapping("/purchase-orders/received")
//    @Operation(
//            summary = "입고 완료 목록 조회",
//            description = "입고 완료된 발주 목록을 조회합니다. (기간 필터 가능)",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getReceivedPurchaseOrders(
//            @Parameter(description = "시작 날짜 (yyyy-MM-dd)")
//            @RequestParam(name = "startDate", required = false) String startDateStr,
//            @Parameter(description = "종료 날짜 (yyyy-MM-dd)")
//            @RequestParam(name = "endDate", required = false) String endDateStr,
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
//            @Parameter(description = "페이지 크기")
//            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
//    ) {
//        List<ReceivedPurchaseOrderDto> allOrders = new ArrayList<>();
//        LocalDateTime baseDate = LocalDateTime.now();
//
//        for (int i = 0; i < 50; i++) {
//            String supplier = SUPPLIER_NAMES[i % SUPPLIER_NAMES.length];
//            LocalDateTime orderDate = baseDate.minusDays(30 + (i % 90));
//            LocalDateTime receivedDate = orderDate.plusDays(7 + (i % 14));
//            int totalAmount = 500000 + (i * 100000) % 4500000;
//
//            allOrders.add(ReceivedPurchaseOrderDto.builder()
//                    .purchaseOrderId(String.valueOf(3001 + i))
//                    .purchaseOrderNumber("PO-2025-" + String.format("%04d", 500 + i))
//                    .supplierCompanyName(supplier)
//                    .orderDate(orderDate)
//                    .dueDate(receivedDate)
//                    .totalAmount(totalAmount)
//                    .statusCode("RECEIVED")
//                    .build());
//        }
//
//        // 날짜 파라미터 파싱
//        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate startDate = null;
//        LocalDate endDate = null;
//
//        try {
//            if (startDateStr != null && !startDateStr.isBlank()) {
//                startDate = LocalDate.parse(startDateStr, dateFormatter);
//            }
//            if (endDateStr != null && !endDateStr.isBlank()) {
//                endDate = LocalDate.parse(endDateStr, dateFormatter);
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(
//                    ApiResponse.fail("날짜 형식이 올바르지 않습니다. (예: 2025-10-01)", HttpStatus.BAD_REQUEST, null)
//            );
//        }
//
//        // orderDate 기준으로 날짜 필터링
//        LocalDate finalStartDate = startDate;
//        LocalDate finalEndDate = endDate;
//        List<ReceivedPurchaseOrderDto> filteredOrders = allOrders.stream()
//                .filter(order -> {
//                    LocalDate orderDateOnly = order.getOrderDate().toLocalDate();
//                    boolean afterStart = (finalStartDate == null) || !orderDateOnly.isBefore(finalStartDate);
//                    boolean beforeEnd = (finalEndDate == null) || !orderDateOnly.isAfter(finalEndDate);
//                    return afterStart && beforeEnd;
//                })
//                .collect(Collectors.toList());
//
//        // 페이지네이션
//        int fromIndex = Math.min(page * size, filteredOrders.size());
//        int toIndex = Math.min(fromIndex + size, filteredOrders.size());
//        List<ReceivedPurchaseOrderDto> pagedOrders = filteredOrders.subList(fromIndex, toIndex);
//
//        PageDto pageInfo = PageDto.builder()
//                .number(page)
//                .size(size)
//                .totalElements(filteredOrders.size())
//                .totalPages((int) Math.ceil((double) filteredOrders.size() / size))
//                .hasNext(toIndex < filteredOrders.size())
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", pagedOrders);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "입고 완료 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//
//    @GetMapping("/sales-orders/production")
//    @Operation(
//            summary = "생산중 목록 조회",
//            description = "생산 중인 판매 주문 목록을 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getProductionSalesOrders(
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
//            @Parameter(description = "페이지 크기")
//            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
//    ) {
//        // Generate mock data
//        List<ProductionSalesOrderDto> allOrders = new ArrayList<>();
//        LocalDateTime baseDate = LocalDateTime.now();
//
//        for (int i = 0; i < 50; i++) {
//            String customer = CUSTOMER_NAMES[i % CUSTOMER_NAMES.length];
//
//            // Vary the dates
//            LocalDateTime orderDate = baseDate.minusDays(15 + (i % 45)); // Order date is 15-60 days ago
//            LocalDateTime dueDate = orderDate.plusDays(3 + (i % 7)); // Production starts 3-10 days after order
//
//            int progress = 10 + (i * 2) % 90; // Production progress 10%-100%
//            int totalAmount = 2000000 + (i * 500000) % 8000000;
//
//            ProductionSalesOrderDto order = ProductionSalesOrderDto.builder()
//                    .salesOrderId(String.valueOf(4001 + i))
//                    .salesOrderNumber("SO-2025-" + String.format("%04d", i + 1))
//                    .customerName(customer)
//                    .orderDate(orderDate)
//                    .dueDate(dueDate)
//                    .progress(progress)
//                    .totalAmount(totalAmount)
//                    .statusCode("IN_PRODUCTION")
//                    .build();
//
//            allOrders.add(order);
//        }
//
//        // Apply pagination
//        int fromIndex = Math.min(page * size, allOrders.size());
//        int toIndex = Math.min(fromIndex + size, allOrders.size());
//        List<ProductionSalesOrderDto> pagedOrders = allOrders.subList(fromIndex, toIndex);
//
//        // Create response
//        PageDto pageInfo = PageDto.builder()
//                .number(page)
//                .size(size)
//                .totalElements(allOrders.size())
//                .totalPages((int) Math.ceil((double) allOrders.size() / size))
//                .hasNext(toIndex < allOrders.size())
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", pagedOrders);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "생산중 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/sales-orders/ready-to-ship")
//    @Operation(
//            summary = "출고 준비완료 목록 조회",
//            description = "출고 준비가 완료된 판매 주문 목록을 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getReadyToShipSalesOrders(
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
//            @Parameter(description = "페이지 크기")
//            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
//    ) {
//
//        // Generate mock data
//        List<ReadyToShipSalesOrderDto> allOrders = new ArrayList<>();
//        LocalDateTime baseDate = LocalDateTime.now();
//
//        for (int i = 0; i < 50; i++) {
//            String customer = CUSTOMER_NAMES[i % CUSTOMER_NAMES.length];
//
//            // Vary the dates
//            LocalDateTime orderDate = baseDate.minusDays(45 + (i % 45)); // Order date is 45-90 days ago
//            LocalDateTime productionCompletionDate = orderDate.plusDays(20 + (i % 15)); // Production completed 20-35 days after order
//            LocalDateTime readyToShipDate = productionCompletionDate.plusDays(2 + (i % 5)); // Ready to ship 2-7 days after production completion
//
//            int totalAmount = 2000000 + (i * 500000) % 8000000;
//
//            ReadyToShipSalesOrderDto order = ReadyToShipSalesOrderDto.builder()
//                    .salesOrderId(String.valueOf(5001 + i))
//                    .salesOrderNumber("SO-2025-" + String.format("%04d", 500 + i))
//                    .customerName(customer)
//                    .orderDate(orderDate)
//                    .productionCompletionDate(productionCompletionDate)
//                    .dueDate(readyToShipDate)
//                    .totalAmount(totalAmount)
//                    .statusCode("READY_TO_SHIP")
//                    .build();
//
//            allOrders.add(order);
//        }
//
//        // Apply pagination
//        int fromIndex = Math.min(page * size, allOrders.size());
//        int toIndex = Math.min(fromIndex + size, allOrders.size());
//        List<ReadyToShipSalesOrderDto> pagedOrders = allOrders.subList(fromIndex, toIndex);
//
//        // Create response
//        PageDto pageInfo = PageDto.builder()
//                .number(page)
//                .size(size)
//                .totalElements(allOrders.size())
//                .totalPages((int) Math.ceil((double) allOrders.size() / size))
//                .hasNext(toIndex < allOrders.size())
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", pagedOrders);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "출고 준비완료 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    //창고 드롭다운 API 추가
//    @GetMapping("/iv/warehouses/dropdown")
//    @Operation(
//            summary = "창고 드롭다운 목록 조회",
//            description = "창고 드롭다운용 목록을 반환합니다."
//    )
//    public ResponseEntity<ApiResponse> getWarehouses(@RequestParam(required = false) String warehouseId) {
//        List<Map<String, String>> warehouses = Arrays.asList(
//                Map.of("warehouseId", "1", "warehouseName", "제1창고 (원자재)","warehouseNumber","A-01-01"),
//                Map.of("warehouseId", "2", "warehouseName", "제2창고 (완제품)","warehouseNumber","B-01-01"),
//                Map.of("warehouseId", "3", "warehouseName", "제3창고 (부품)","warehouseNumber","C-01-01"),
//                Map.of("warehouseId", "4", "warehouseName", "냉동창고 (특수보관)","warehouseNumber","A-03-02"),
//                Map.of("warehouseId", "5", "warehouseName", "임시창고 (임시보관)","warehouseNumber","C-03-04")
//        );
//
//        List<Map<String, String>> filtered = warehouses.stream()
//                .filter(w -> !w.get("warehouseId").equals(warehouseId))
//                .collect(Collectors.toList());
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("warehouses", filtered);
//        return ResponseEntity.ok(ApiResponse.success(response, "창고 드롭다운 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//    @PatchMapping("/iv/items/{itemId}/safety-stock")
//    @Operation(
//            summary = "안전재고 수정",
//            description = "품목의 안전재고를 수정합니다."
//    )
//    public ResponseEntity<ApiResponse<Void>> updateSafetyStock(
//            @Parameter(name = "itemId", description = "품목 ID")
//            @PathVariable String itemId,
//            @Parameter(name = "safetyStock", description = "수정할 안전재고")
//            @RequestParam int safetyStock
//    ) {
//        // 요청값만 잘 맞으면 200 반환 (실제 로직 없음)
//        return ResponseEntity.ok(ApiResponse.success(null, "안전재고가 수정되었습니다.", HttpStatus.OK));
//    }
//
//    // 자재 추가 API
//    @PostMapping("/iv/items")
//    @Operation(
//            summary = "자재(품목) 추가",
//            description = "자재(품목)를 추가합니다."
//    )
//    public ResponseEntity<ApiResponse<Void>> addInventoryItem(
//            @RequestBody AddInventoryItemRequestDto request
//    ) {
//        // itemId, supplierCompanyId, safetyStock, currentStock, warehouseId
//        // 요청값만 잘 받으면 200 반환
//        return ResponseEntity.ok(ApiResponse.success(null, "자재가 추가되었습니다.", HttpStatus.OK));
//    }
//
//    // 자재추가를 위한 토글 API
//    @GetMapping("/iv/items/toggle")
//    @Operation(
//            summary = "자재(품목) 토글 목록",
//            description = "자재(품목) 추가를 위한 토글 목록을 반환합니다."
//    )
//    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getItemToggleList() {
//        List<Map<String, Object>> items = new ArrayList<>();
//        for (int i = 1; i <= 10; i++) {
//            items.add(Map.of(
//                    "itemId", String.valueOf(i),
//                    "itemName", "품목" + i,
//                    "supplierCompanyId", "SUP-" + i,
//                    "supplierCompanyName", SUPPLIER_NAMES[(i - 1) % SUPPLIER_NAMES.length],
//                    "uomName", UOM_NAMES[(i - 1) % UOM_NAMES.length],
//                    "unitPrice", 1000 * i
//            ));
//        }
//        return ResponseEntity.ok(ApiResponse.success(items, "자재 토글 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    // 창고 추가시 담당자 토글 API
//    @GetMapping("/iv/warehouses/managers/toggle")
//    @Operation(
//            summary = "창고 담당자 토글 목록",
//            description = "창고 추가를 위한 담당자 토글 목록을 반환합니다."
//    )
//    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getWarehouseManagerToggleList() {
//        List<Map<String, String>> managers = Arrays.asList(
//                Map.of("managerId", "1", "managerName", "김창고", "managerEmail", "kim@example.com", "managerPhone", "010-1111-2222"),
//                Map.of("managerId", "2", "managerName", "이관리", "managerEmail", "lee@example.com", "managerPhone", "010-2222-3333"),
//                Map.of("managerId", "3", "managerName", "박담당", "managerEmail", "park@example.com", "managerPhone", "010-3333-4444")
//        );
//        return ResponseEntity.ok(ApiResponse.success(managers, "창고 담당자 토글 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    // 창고 수정 API
//    @PutMapping("/iv/warehouses/{warehouseId}")
//    @Operation(
//            summary = "창고 정보 수정",
//            description = "창고 정보를 수정합니다."
//    )
//    public ResponseEntity<ApiResponse<Void>> updateWarehouse(
//            @PathVariable String warehouseId,
//            @RequestBody UpdateWarehouseRequestDto request
//    ) {
//        // warehouseName, warehouseType, location, managerId, warehouseStatusCode
//        return ResponseEntity.ok(ApiResponse.success(null, "창고 정보가 수정되었습니다.", HttpStatus.OK));
//    }
//    //판매제품 토글
//    @GetMapping("/product/item/toggle")
//    @Operation(
//            summary = "판매 제품 토글"
//    )
//    public ResponseEntity<ApiResponse<ProductMultipleResponseDto>> getItemCategoryProducts() {
//
//            // 목업 데이터 생성
//            List<ProductMultipleResponseDto.ProductDto> mockProducts = new ArrayList<>();
//
//            for (int i = 1; i <= 10; i++) {
//                mockProducts.add(ProductMultipleResponseDto.ProductDto.builder()
//                        .itemId("ITEM-" + i)
//                        .itemNumber("ITEM-" + String.format("%03d", i))
//                        .itemName("판매 제품 " + i)
//                        .uomName("EA")
//                        .unitPrice(BigDecimal.valueOf(1000 + (i * 100)))
//                        .build());
//            }
//
//            ProductMultipleResponseDto response = ProductMultipleResponseDto.builder()
//                    .products(mockProducts)
//                    .build();
//
//            return ResponseEntity.ok(ApiResponse.success(response, "목업 데이터 조회 성공", HttpStatus.OK));
//
//
//    }
//
//}
