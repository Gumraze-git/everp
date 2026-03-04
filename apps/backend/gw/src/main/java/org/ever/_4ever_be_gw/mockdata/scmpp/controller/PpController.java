//package org.ever._4ever_be_gw.mockdata.scmpp.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import java.math.BigDecimal;
//import org.ever._4ever_be_gw.common.dto.PageDto;
//import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
//import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
//import org.ever._4ever_be_gw.common.exception.BusinessException;
//import org.ever._4ever_be_gw.common.exception.ErrorCode;
//import org.ever._4ever_be_gw.common.response.ApiResponse;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.*;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.bom.BomCreateRequestDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.bom.BomDetailDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.bom.BomListItemDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.mes.MesWorkOrderDetailDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.mes.MesWorkOrderDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.mrp.MrpOrderDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.mrp.MrpRequestBodyDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.mrp.MrpRequestSummaryDto;
//import org.ever._4ever_be_gw.scm.PeriodStatDto;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.temporal.WeekFields;
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/scm-pp/pp")
//@Tag(name = "생산관리(PP)", description = "생산 관리 API")
//public class PpController {
//    @PostMapping("/boms")
//    @Operation(
//            summary = "BOM 생성",
//            description = "새로운 BOM을 생성합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> createBom(
//            @RequestBody BomCreateRequestDto request
//    ) {
//        Map<String, Object> response = new HashMap<>();
//        response.put("bomId", "1asd");
//        response.put("bomCode", "BOM-001");
//
//        return ResponseEntity.ok(ApiResponse.success(response, "BOM이 성공적으로 생성되었습니다.", HttpStatus.OK));
//    }
//
//    private static final Set<String> ALLOWED_PERIODS = Set.of("week", "month", "quarter", "year");
//
//    @GetMapping("/statistics")
//    @Operation(
//            summary = "PP 통계 조회",
//            description = "생산 진행 현황 및 BOM 관련 통계를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<StatsResponseDto<StatsMetricsDto>>> getProductionStatistics(
//    ) {
//        List<String> ALLOWED_PERIODS = List.of("week", "month", "quarter", "year");
//
//        String periods = "";
//        // periods 파라미터 없으면 기본적으로 전체 조회
//        List<String> requested = (periods == null || periods.isBlank())
//                ? ALLOWED_PERIODS
//                : Arrays.stream(periods.split(","))
//                .map(String::trim)
//                .map(String::toLowerCase)
//                .toList();
//
//        List<String> invalid = requested.stream().filter(p -> !ALLOWED_PERIODS.contains(p)).toList();
//        if (periods != null && !periods.isBlank() && (!invalid.isEmpty() || requested.stream().noneMatch(ALLOWED_PERIODS::contains))) {
//            throw new BusinessException(ErrorCode.INVALID_PERIODS);
//        }
//
//        List<String> finalPeriods = requested.stream().filter(ALLOWED_PERIODS::contains).toList();
//
//        StatsResponseDto.StatsResponseDtoBuilder<StatsMetricsDto> builder = StatsResponseDto.<StatsMetricsDto>builder();
//
//        // week
//        if (finalPeriods.contains("week")) {
//            builder.week(StatsMetricsDto.builder()
//                    .put("production_in_progress", PeriodStatDto.builder().value(42L).deltaRate(new BigDecimal("0.087")).build())
//                    .put("production_completed", PeriodStatDto.builder().value(35L).deltaRate(new BigDecimal("0.062")).build())
//                    .put("bom_count", PeriodStatDto.builder().value(18L).deltaRate(new BigDecimal("0.045")).build())
//                    .build());
//        }
//
//        // month
//        if (finalPeriods.contains("month")) {
//            builder.month(StatsMetricsDto.builder()
//                    .put("production_in_progress", PeriodStatDto.builder().value(168L).deltaRate(new BigDecimal("0.094")).build())
//                    .put("production_completed", PeriodStatDto.builder().value(147L).deltaRate(new BigDecimal("0.078")).build())
//                    .put("bom_count", PeriodStatDto.builder().value(72L).deltaRate(new BigDecimal("0.052")).build())
//                    .build());
//        }
//
//        // quarter
//        if (finalPeriods.contains("quarter")) {
//            builder.quarter(StatsMetricsDto.builder()
//                    .put("production_in_progress", PeriodStatDto.builder().value(498L).deltaRate(new BigDecimal("0.081")).build())
//                    .put("production_completed", PeriodStatDto.builder().value(462L).deltaRate(new BigDecimal("0.069")).build())
//                    .put("bom_count", PeriodStatDto.builder().value(210L).deltaRate(new BigDecimal("0.048")).build())
//                    .build());
//        }
//
//        // year
//        if (finalPeriods.contains("year")) {
//            builder.year(StatsMetricsDto.builder()
//                    .put("production_in_progress", PeriodStatDto.builder().value(2045L).deltaRate(new BigDecimal("0.073")).build())
//                    .put("production_completed", PeriodStatDto.builder().value(1912L).deltaRate(new BigDecimal("0.061")).build())
//                    .put("bom_count", PeriodStatDto.builder().value(865L).deltaRate(new BigDecimal("0.041")).build())
//                    .build());
//        }
//
//        StatsResponseDto<StatsMetricsDto> response = builder.build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "생산 통계 정보를 조회했습니다.", HttpStatus.OK));
//    }
//
//
//
//
//    @GetMapping("/boms")
//    @Operation(
//            summary = "BOM 목록 조회",
//            description = "BOM 목록을 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getBomList(
//            @Parameter(name = "page", description = "페이지 번호")
//            @RequestParam(required = false, defaultValue = "0") int page,
//            @Parameter(name = "size", description = "페이지 크기")
//            @RequestParam(required = false, defaultValue = "10") int size
//    ) {
//        List<BomListItemDto> items = Arrays.asList(
//                BomListItemDto.builder()
//                        .bomId("1")
//                        .bomNumber("BOM-001")
//                        .itemId("1")
//                        .itemCode("PRD-001")
//                        .itemName("스마트폰 케이스")
//                        .version("v1.2")
//                        .status("활성")
//                        .lastModifiedAt(LocalDateTime.parse("2024-01-20T00:00:00"))
//                        .build(),
//                BomListItemDto.builder()
//                        .bomId("2")
//                        .bomNumber("BOM-002")
//                        .itemId("2")
//                        .itemCode("PRD-002")
//                        .itemName("무선 이어폰")
//                        .version("v2.0")
//                        .status("활성")
//                        .lastModifiedAt(LocalDateTime.parse("2024-01-18T00:00:00"))
//                        .build()
//        );
//
//        PageDto pageInfo = PageDto.builder()
//                .number(page)
//                .size(size)
//                .totalElements(2)
//                .totalPages(1)
//                .hasNext(false)
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", items);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "BOM 목록 조회 성공", HttpStatus.OK));
//    }
//
//    @GetMapping("/boms/{bomId}")
//    @Operation(
//            summary = "BOM 상세 조회",
//            description = "BOM 상세 정보를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<BomDetailDto>> getBomDetail(
//            @Parameter(name = "bomId", description = "BOM ID")
//            @PathVariable String bomId
//    ) {
//        List<BomCreateRequestDto.ComponentDto> components = Arrays.asList(
//                BomCreateRequestDto.ComponentDto.builder()
//                        .itemId("1")
//                        .itemNumber("MAT-001")
//                        .itemName("TPU 소재")
//                        .quantity(1)
//                        .uomName("EA")
//                        .level("Level 1")
//                        .supplierCompanyName("공급사 C")
//                        .operationId("1")
//                        .operationName("사출성형")
//                        .build(),
//                BomCreateRequestDto.ComponentDto.builder()
//                        .itemId("2")
//                        .itemNumber("MAT-002")
//                        .itemName("실리콘 패드")
//                        .quantity(2)
//                        .uomName("EA")
//                        .level("Level 2")
//                        .supplierCompanyName("공급사 D")
//                        .operationId("2")
//                        .operationName("조립")
//                        .build(),
//                BomCreateRequestDto.ComponentDto.builder()
//                        .itemId("3")
//                        .itemNumber("MAT-003")
//                        .itemName("포장재")
//                        .quantity(1)
//                        .uomName("SET")
//                        .level("Level 1")
//                        .supplierCompanyName("공급사 C")
//                        .operationId("3")
//                        .operationName("검사")
//                        .build()
//        );
//
//        Map<String, List<BomDetailDto.LevelComponentDto>> levelStructure = new HashMap<>();
//
//        List<BomDetailDto.LevelComponentDto> level1 = Arrays.asList(
//                BomDetailDto.LevelComponentDto.builder()
//                        .itemId("1")
//                        .itemNumber("MAT-001")
//                        .itemName("TPU 소재")
//                        .quantity(1)
//                        .uomName("EA")
//                        .build(),
//                BomDetailDto.LevelComponentDto.builder()
//                        .itemId("3")
//                        .itemNumber("MAT-003")
//                        .itemName("포장재")
//                        .quantity(1)
//                        .uomName("EA")
//                        .build()
//        );
//
//        List<BomDetailDto.LevelComponentDto> level2 = Collections.singletonList(
//                BomDetailDto.LevelComponentDto.builder()
//                        .itemId("2")
//                        .itemNumber("MAT-002")
//                        .itemName("실리콘 패드")
//                        .quantity(2)
//                        .uomName("EA")
//                        .build()
//        );
//
//        levelStructure.put("Level 1", level1);
//        levelStructure.put("Level 2", level2);
//
//        List<BomCreateRequestDto.RoutingDto> routing = Arrays.asList(
//                BomCreateRequestDto.RoutingDto.builder()
//                        .sequence(10)
//                        .operationId("1")
//                        .operationName("사출성형")
//                        .runTime(5)
//                        .build(),
//                BomCreateRequestDto.RoutingDto.builder()
//                        .sequence(20)
//                        .operationId("2")
//                        .operationName("조립")
//                        .runTime(3)
//                        .build(),
//                BomCreateRequestDto.RoutingDto.builder()
//                        .sequence(30)
//                        .operationId("3")
//                        .operationName("포장")
//                        .runTime(2)
//                        .build()
//        );
//
//        BomDetailDto response = BomDetailDto.builder()
//                .bomId(bomId)
//                .bomNumber("BOM-001")
//                .productId("1")
//                .productNumber("PRD-001")
//                .productName("스마트폰 케이스")
//                .version("v1.2")
//                .status("활성")
//                .lastModifiedAt(LocalDateTime.parse("2024-01-20T00:00:00"))
//                .components(components)
//                .levelStructure(levelStructure)
//                .routing(routing)
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "BOM 상세 조회 성공", HttpStatus.OK));
//    }
//
//
//    @DeleteMapping("/boms/{bomId}")
//    @Operation(
//            summary = "BOM 삭제",
//            description = "BOM을 삭제합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공",
//                            content = @Content(
//                                    mediaType = "application/json",
//                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"BOM이 성공적으로 삭제되었습니다.\",\n  \"data\": null\n}")
//                            )
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Void>> deleteBom(
//            @Parameter(name = "bomId", description = "BOM ID")
//            @PathVariable String bomId
//    ) {
//        return ResponseEntity.ok(ApiResponse.success(null, "BOM이 성공적으로 삭제되었습니다.", HttpStatus.OK));
//    }
//
//    @PostMapping("/mrp/request-summary")
//    @Operation(
//            summary = "MRP 자재 구매 요청 리스트",
//            description = "MRP 자재 구매 요청 목록을 생성합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<MrpRequestSummaryDto>> getMrpRequestSummary(
//            @RequestBody MrpRequestBodyDto request
//    ) {
//        List<MrpRequestSummaryDto.MrpItemDto> items = Arrays.asList(
//                MrpRequestSummaryDto.MrpItemDto.builder()
//                        .mrpId("1")
//                        .quotationNumber("Q-2024-001")
//                        .itemName("스테인리스 스틸")
//                        .quantity(400)
//                        .unitPrice(1200)
//                        .totalAmount(480000)
//                        .supplierCompanyName("포스코")
//                        .dueDate("2024-02-08")
//                        .status("계획")
//                        .build(),
//                MrpRequestSummaryDto.MrpItemDto.builder()
//                        .mrpId("2")
//                        .quotationNumber("Q-2024-002")
//                        .itemName("구리선")
//                        .quantity(600)
//                        .unitPrice(800)
//                        .totalAmount(480000)
//                        .supplierCompanyName("LS전선")
//                        .dueDate("2024-02-09")
//                        .status("계획")
//                        .build(),
//                MrpRequestSummaryDto.MrpItemDto.builder()
//                        .mrpId("3")
//                        .quotationNumber("Q-2024-003")
//                        .itemName("베어링 6205")
//                        .quantity(100)
//                        .unitPrice(15000)
//                        .totalAmount(1500000)
//                        .supplierCompanyName("SKF코리아")
//                        .dueDate("2024-02-07")
//                        .status("계획")
//                        .build(),
//                MrpRequestSummaryDto.MrpItemDto.builder()
//                        .mrpId("4")
//                        .quotationNumber("Q-2024-001")
//                        .itemName("알루미늄 프로파일")
//                        .quantity(300)
//                        .unitPrice(2500)
//                        .totalAmount(750000)
//                        .supplierCompanyName("한국알루미늄")
//                        .dueDate("2024-02-10")
//                        .status("계획")
//                        .build()
//        );
//
//        MrpRequestSummaryDto response = MrpRequestSummaryDto.builder()
//                .selectedOrderCount(4)
//                .totalExpectedAmount(3210000)
//                .requestDate("2025-10-13")
//                .items(items)
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "구매 요청 요약을 계산했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/mrp/orders")
//    @Operation(
//            summary = "MRP 순소요 목록 조회",
//            description = "MRP 순소요 목록을 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getMrpOrders(
//            @Parameter(name = "productId", description = "제품 ID")
//            @RequestParam(required = false) String productId,
//            @Parameter(name = "quotationId", description = "견적 ID")
//            @RequestParam(required = false) String quotationId,
//            @Parameter(name = "availableStatusCode", description = "가용 상태 코드 (ALL, INSUFFICIENT, SUFFICIENT)")
//            @RequestParam(required = false) String availableStatusCode,
//            @Parameter(name = "page", description = "페이지 번호")
//            @RequestParam(required = false, defaultValue = "0") int page,
//            @Parameter(name = "size", description = "페이지 크기")
//            @RequestParam(required = false, defaultValue = "10") int size
//    ) {
//        List<MrpOrderDto> items = new ArrayList<>();
//        String[] itemNames = {"스테인리스 스틸", "구리선", "베어링 6205", "볼트 M8x20", "알루미늄 프로파일"};
//        String[] suppliers = {"포스코", "LS전선", "SKF코리아", "동양볼트", "한국알루미늄"};
//        String[] availableCodes = {"INSUFFICIENT", "SUFFICIENT"};
//
//        for (int i = 0; i < 30; i++) {
//            items.add(
//                    MrpOrderDto.builder()
//                            .itemId(String.valueOf(i + 1))
//                            .itemName(itemNames[i % itemNames.length])
//                            .requiredQuantity(100 + i * 10)
//                            .currentStock(50 + i * 5)
//                            .safetyStock(20 + i % 10)
//                            .availableStock(30 + i * 3)
//                            .availableStatusCode(availableCodes[i % availableCodes.length])
//                            .shortageQuantity((i % 2 == 0) ? (50 + i * 2) : null)
//                            .itemType("구매품")
//                            .procurementStartDate("2024-02-" + String.format("%02d", (i % 28) + 1))
//                            .expectedArrivalDate("2024-02-" + String.format("%02d", ((i + 5) % 28) + 1))
//                            .supplierCompanyName(suppliers[i % suppliers.length])
//                            .build()
//            );
//        }
//
//        if(availableStatusCode.equals("ALL")) {
//            availableStatusCode = "";
//        }
//
//        // availableStatusCode 필터링
//        if (availableStatusCode != null && !availableStatusCode.isBlank()) {
//            String code = availableStatusCode.toUpperCase();
//            items.removeIf(item -> !code.equals(item.getAvailableStatusCode()));
//        }
//
//        // 페이징
//        int total = items.size();
//        int fromIdx = page * size;
//        int toIdx = Math.min(fromIdx + size, total);
//        List<MrpOrderDto> pageContent = fromIdx < total ? items.subList(fromIdx, toIdx) : Collections.emptyList();
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
//        return ResponseEntity.ok(ApiResponse.success(response, "자재 조달 계획을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//
//
//
//    @GetMapping("/mrp/planned-orders/detail/{mrpId}")
//    @Operation(
//            summary = "MRP 계획 주문 상세 조회",
//            description = "MRP 계획 주문 상세 정보를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<PlannedOrderDetailDto>> getPlannedOrderDetail(
//            @Parameter(name = "mrpId", description = "계획 주문 ID")
//            @PathVariable String mrpId
//    ) {
//        List<PlannedOrderDetailDto.OrderItemDto> orderItems = Arrays.asList(
//                PlannedOrderDetailDto.OrderItemDto.builder()
//                        .itemId("1")
//                        .itemName("강판")
//                        .quantity(500)
//                        .uomName("EA")
//                        .unitPrice(5000)
//                        .build(),
//                PlannedOrderDetailDto.OrderItemDto.builder()
//                        .itemId("2")
//                        .itemName("볼트")
//                        .quantity(100)
//                        .uomName("EA")
//                        .unitPrice(500)
//                        .build()
//        );
//
//        PlannedOrderDetailDto response = PlannedOrderDetailDto.builder()
//                .mrpId(mrpId)
//                .quotationId("1")
//                .quotationCode("Q-2024-001")
//                .requesterId("1")
//                .requesterName("김철수")
//                .departmentName("생산팀")
//                .requestDate("2024-01-15")
//                .desiredDueDate("2024-01-25")
//                .status("승인")
//                .orderItems(orderItems)
//                .totalAmount(2500000)
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "계획 주문 요청 상세를 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/mrp/planned-orders/list")
//    @Operation(
//            summary = "MRP 계획 주문 목록 조회",
//            description = "MRP 계획 주문 목록을 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getPlannedOrderList(
//            @Parameter(name = "statusCode", description = "계획 주문 상태 (ALL, PENDING, PLANNED, APPROVED, REJECTED)")
//            @RequestParam(required = false) String statusCode,
//            @Parameter(name = "page", description = "페이지 번호")
//            @RequestParam(required = false, defaultValue = "0") int page,
//            @Parameter(name = "size", description = "페이지 크기")
//            @RequestParam(required = false, defaultValue = "10") int size
//    ) {
//        String[] statusCodes = {"PENDING", "PLANNED", "APPROVED", "REJECTED"};
//        String[] itemNames = {"스테인리스 스틸", "구리선", "베어링 6205", "볼트 M8x20", "알루미늄 프로파일"};
//        String[] quotationNumbers = {"Q-2024-001", "Q-2024-002", "Q-2024-003", "Q-2024-004"};
//        List<PlannedOrderListItemDto> items = new ArrayList<>();
//
//        for (int i = 0; i < 30; i++) {
//            items.add(
//                    PlannedOrderListItemDto.builder()
//                            .mrpId(String.valueOf(i + 1))
//                            .quotationId(String.valueOf((i % 4) + 1))
//                            .quotationNumber(quotationNumbers[i % quotationNumbers.length])
//                            .itemId(String.valueOf((i % 5) + 1))
//                            .itemName(itemNames[i % itemNames.length])
//                            .quantity(100 + i * 10)
//                            .procurementStartDate("2024-02-" + String.format("%02d", (i % 28) + 1))
//                            .statusCode(statusCodes[i % statusCodes.length])
//                            .build()
//            );
//        }
//
//        if (statusCode != null && !statusCode.equalsIgnoreCase("ALL")) {
//            String filterCode = statusCode.toUpperCase();
//            items.removeIf(item -> !filterCode.equals(item.getStatusCode()));
//        }
//
//        int total = items.size();
//        int fromIdx = page * size;
//        int toIdx = Math.min(fromIdx + size, total);
//        List<PlannedOrderListItemDto> pageContent = fromIdx < total ? items.subList(fromIdx, toIdx) : Collections.emptyList();
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
//        return ResponseEntity.ok(ApiResponse.success(response, "계획 주문 요청 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/mps/plans")
//    @Operation(
//            summary = "제품별 MPS 계획 조회",
//            description = "단일 제품에 대해 MPS(주간 생산계획) 정보를 조회합니다."
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getMpsPlan(
//            @Parameter(name = "productId", description = "제품 ID")
//            @RequestParam String productId,
//            @Parameter(name = "startDate", description = "시작일(yyyy-MM-dd)")
//            @RequestParam String startDate,
//            @Parameter(name = "endDate", description = "종료일(yyyy-MM-dd)")
//            @RequestParam String endDate,
//            @Parameter(name = "page", description = "페이지 번호")
//            @RequestParam(required = false, defaultValue = "0") int page,
//            @Parameter(name = "size", description = "페이지 크기")
//            @RequestParam(required = false, defaultValue = "7") int size
//    ) {
//        LocalDate start = LocalDate.parse(startDate);
//        LocalDate end = LocalDate.parse(endDate);
//
//        List<String> weeks = new ArrayList<>();
//        LocalDate firstWeekStart = start.minusWeeks(3);
//        LocalDate current = firstWeekStart;
//        while (!current.isAfter(end)) {
//            int weekOfMonth = current.get(WeekFields.ISO.weekOfMonth());
//            int month = current.getMonthValue();
//            weeks.add(month + "월 " + weekOfMonth + "주차");
//            current = current.plusWeeks(1);
//        }
//
//        int totalWeeks = weeks.size();
//        int fromIdx = page * size;
//        int toIdx = Math.min(fromIdx + size, totalWeeks);
//        List<String> pageWeeks = fromIdx < totalWeeks ? weeks.subList(fromIdx, toIdx) : Collections.emptyList();
//
//        List<Integer> demand = new ArrayList<>();
//        List<Integer> requiredInventory = new ArrayList<>();
//        List<Integer> productionNeeded = new ArrayList<>();
//        List<Integer> plannedProduction = new ArrayList<>();
//
//        for (int i = 0; i < pageWeeks.size(); i++) {
//            if (pageWeeks.get(i).equals("null")) {
//                demand.add(null);
//                requiredInventory.add(null);
//                productionNeeded.add(null);
//                plannedProduction.add(null);
//            } else {
//                if (i < 3) {
//                    demand.add(null);
//                    requiredInventory.add(null);
//                    productionNeeded.add(null);
//                    plannedProduction.add(null);
//                } else {
//                    demand.add(13 + i);
//                    requiredInventory.add(8 + i);
//                    productionNeeded.add(11 + i);
//                    plannedProduction.add(11 + i);
//                }
//            }
//        }
//
//        while (demand.size() < size) {
//            demand.add(null);
//            requiredInventory.add(null);
//            productionNeeded.add(null);
//            plannedProduction.add(null);
//            pageWeeks.add("null");
//        }
//
//        // 순서 보장: LinkedHashMap 사용
//        Map<String, Object> result = new LinkedHashMap<>();
//        result.put("productId", productId);
//        result.put("startDate", startDate);
//        result.put("endDate", endDate);
//        result.put("periods", pageWeeks);
//        result.put("demand", demand);
//        result.put("requiredInventory", requiredInventory);
//        result.put("productionNeeded", productionNeeded);
//        result.put("plannedProduction", plannedProduction);
//
//        PageDto pageInfo = PageDto.builder()
//                .number(page)
//                .size(size)
//                .totalElements(totalWeeks)
//                .totalPages((int) Math.ceil((double) totalWeeks / size))
//                .hasNext(toIdx < totalWeeks)
//                .build();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", result);
//        response.put("page", pageInfo);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "제품별 MPS 계획을 조회했습니다.", HttpStatus.OK));
//    }
//
//    @PostMapping("/quotations/simulate")
//    @Operation(
//            summary = "여러 견적에 대한 ATP + MPS + MRP 시뮬레이션 실행",
//            description = "여러 견적 ID를 받아 각 견적에 대한 ATP, MPS, MRP 시뮬레이션을 실행합니다."
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuotationSimulationList(
//            @RequestBody List<String> quotationIds,
//            @RequestParam(required = false, defaultValue = "0") int page,
//            @RequestParam(required = false, defaultValue = "10") int size
//    ) {
//        List<QuotationSimulationDto> simulations = new ArrayList<>();
//
//        // 목업 데이터 3개 생성
//        for (int i = 1; i <= 3; i++) {
//
//            // ✅ shortage 2개 생성
//            List<QuotationSimulationDto.ShortageItemDto> shortages = new ArrayList<>();
//            for (int j = 1; j <= 2; j++) {
//                shortages.add(
//                        QuotationSimulationDto.ShortageItemDto.builder()
//                                .itemId(i + "-" + j)
//                                .itemName("자재" + j)
//                                .requiredQuantity(100 * j)
//                                .currentStock(40 * j)
//                                .shortQuantity(60 * j)
//                                .build()
//                );
//            }
//
//            simulations.add(
//                    QuotationSimulationDto.builder()
//                            .quotationId("Q-2024-00" + i)
//                            .quotationNumber("Q-2024-00" + i)
//                            .customerCompanyId(String.valueOf(i))
//                            .customerCompanyName(i == 1 ? "현대자동차" : i == 2 ? "기아자동차" : "삼성전자")
//                            .productId(String.valueOf(i))
//                            .productName(i == 1 ? "도어패널" : i == 2 ? "Hood Panel" : "Fender Panel")
//                            .requestQuantity(500 * i)
//                            .requestDueDate("2024-02-" + (10 + i))
//                            .simulation(QuotationSimulationDto.SimulationResultDto.builder()
//                                    .status(i % 2 == 0 ? "SUCCESS" : "FAIL")
//                                    .availableQuantity(130 * i)
//                                    .shortageQuantity(370 * i)
//                                    .suggestedDueDate("2024-03-" + (10 + i))
//                                    .generatedAt("2025-10-08T12:00:00Z")
//                                    .build())
//                            .shortages(shortages)
//                            .build()
//            );
//        }
//
//        // 페이징
//        int total = simulations.size();
//        int fromIdx = page * size;
//        int toIdx = Math.min(fromIdx + size, total);
//        List<QuotationSimulationDto> pageContent = fromIdx < total ? simulations.subList(fromIdx, toIdx) : Collections.emptyList();
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
//        return ResponseEntity.ok(ApiResponse.success(response, "견적 시뮬레이션 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//    @PostMapping("/quotations/preview")
//    @Operation(
//            summary = "견적 제안납기 프리뷰 목록 조회",
//            description = "여러 견적 ID에 대한 제안 납기 계획 프리뷰를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuotationPreviewList(
//            @RequestBody List<String> quotationIds,
//            @Parameter(name = "page", description = "페이지 번호")
//            @RequestParam(required = false, defaultValue = "0") int page,
//            @Parameter(name = "size", description = "페이지 크기")
//            @RequestParam(required = false, defaultValue = "10") int size
//    ) {
//        List<DueDatePreviewDto> previews = new ArrayList<>();
//
//        String[] customers = {"현대자동차", "기아자동차", "삼성전자"};
//        String[] products = {"도어패널", "Hood Panel", "Fender Panel"};
//        String[] dueDates = {"2024-03-10", "2024-03-15", "2024-03-20"};
//
//        for (int i = 0; i < 3; i++) {
//            List<DueDatePreviewDto.WeekPlanDto> weeks = Arrays.asList(
//                    DueDatePreviewDto.WeekPlanDto.builder()
//                            .week("2024-02-3W")
//                            .demand(0)
//                            .requiredQuantity(0)
//                            .productionQuantity(300 + i * 10)
//                            .mps(300 + i * 10)
//                            .build(),
//                    DueDatePreviewDto.WeekPlanDto.builder()
//                            .week("2024-02-4W")
//                            .demand(500 + i * 10)
//                            .requiredQuantity(500 + i * 10)
//                            .productionQuantity(200 + i * 10)
//                            .mps(200 + i * 10)
//                            .build(),
//                    DueDatePreviewDto.WeekPlanDto.builder()
//                            .week("2024-03-1W")
//                            .demand(0)
//                            .requiredQuantity(0)
//                            .productionQuantity(0)
//                            .mps(0)
//                            .build(),
//                    DueDatePreviewDto.WeekPlanDto.builder()
//                            .week("2024-03-2W")
//                            .demand(0)
//                            .requiredQuantity(0)
//                            .productionQuantity(0)
//                            .mps(0)
//                            .build()
//            );
//
//            previews.add(
//                    DueDatePreviewDto.builder()
//                            .quotationNumber("Q-2024-00" + (i + 1))
//                            .customerCompanyName(customers[i])
//                            .productName(products[i])
//                            .confirmedDueDate(dueDates[i])
//                            .weeks(weeks)
//                            .build()
//            );
//        }
//
//        // 페이징
//        int total = previews.size();
//        int fromIdx = page * size;
//        int toIdx = Math.min(fromIdx + size, total);
//        List<DueDatePreviewDto> pageContent = fromIdx < total ? previews.subList(fromIdx, toIdx) : Collections.emptyList();
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
//        return ResponseEntity.ok(ApiResponse.success(response, "견적 제안납기 프리뷰 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/mes/work-orders")
//    @Operation(
//            summary = "MES 작업 목록 조회",
//            description = "MES(Manufacturing Execution System) 작업 목록을 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Map<String, Object>>> getMesWorkOrders(
//            @Parameter(name = "status", description = "작업 상태")
//            @RequestParam(required = false) String status,
//            @Parameter(name = "quotationId", description = "견적 ID")
//            @RequestParam(required = false) String quotationId,
//            @Parameter(name = "page", description = "페이지 번호")
//            @RequestParam(required = false, defaultValue = "0") int page,
//            @Parameter(name = "size", description = "페이지 크기")
//            @RequestParam(required = false, defaultValue = "20") int size
//    ) {
//        List<MesWorkOrderDto> items = Arrays.asList(
//                MesWorkOrderDto.builder()
//                        .mesId("1")
//                        .mesNumber("WO-2024-001")
//                        .productId("1")
//                        .productName("산업용 모터 5HP")
//                        .quantity(50)
//                        .uomName("EA")
//                        .quotationId("1")
//                        .quotationNumber("Q-2024-001")
//                        .status("IN_PROGRESS")
//                        .currentOperation("OP30")
//                        .startDate("2024-01-15")
//                        .endDate("2024-02-10")
//                        .progressRate(65)
//                        .sequence(Arrays.asList("OP10", "OP20", "OP30", "OP40", "OP50", "OP60"))
//                        .build(),
//                MesWorkOrderDto.builder()
//                        .mesId("2")
//                        .mesNumber("WO-2024-002")
//                        .productId("2")
//                        .productName("알루미늄 프레임")
//                        .quantity(100)
//                        .uomName("EA")
//                        .quotationId("2")
//                        .quotationNumber("Q-2024-002")
//                        .status("PLANNED")
//                        .currentOperation("OP10")
//                        .startDate("2024-01-20")
//                        .endDate("2024-02-15")
//                        .progressRate(0)
//                        .sequence(Arrays.asList("OP10", "OP20", "OP30", "OP40", "OP50", "OP60"))
//                        .build()
//        );
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("content", items);
//        response.put("page", page);
//        response.put("size", size);
//        response.put("totalElements", 2);
//        response.put("totalPages", 1);
//
//        return ResponseEntity.ok(ApiResponse.success(response, "성공적으로 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/mes/work-orders/{mesId}")
//    @Operation(
//            summary = "MES 작업 상세 조회",
//            description = "MES 작업 상세 정보를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<MesWorkOrderDetailDto>> getMesWorkOrderDetail(
//            @Parameter(name = "mesId", description = "MES 작업 ID")
//            @PathVariable String mesId
//    ) {
//        List<MesWorkOrderDetailDto.OperationDto> operations = Arrays.asList(
//                MesWorkOrderDetailDto.OperationDto.builder()
//                        .operationNumber("OP10")
//                        .operationName("재료 준비")
//                        .sequence(1)
//                        .statusCode("COMPLETED")
//                        .startedAt("09:00")
//                        .finishedAt("10:30")
//                        .durationHours(3.5)
//                        .manager(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
//                        .build(),
//                MesWorkOrderDetailDto.OperationDto.builder()
//                        .operationNumber("OP20")
//                        .operationName("가공")
//                        .sequence(2)
//                        .statusCode("COMPLETED")
//                        .startedAt("10:30")
//                        .finishedAt("14:00")
//                        .durationHours(3.5)
//                        .manager(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
//                        .build(),
//                MesWorkOrderDetailDto.OperationDto.builder()
//                        .operationNumber("OP30")
//                        .operationName("조립")
//                        .sequence(3)
//                        .statusCode("IN_PROGRESS")
//                        .startedAt("14:00")
//                        .finishedAt(null)
//                        .durationHours(null)
//                        .manager(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
//                        .build(),
//                MesWorkOrderDetailDto.OperationDto.builder()
//                        .operationNumber("OP40")
//                        .operationName("테스트")
//                        .sequence(4)
//                        .statusCode("PENDING")
//                        .startedAt(null)
//                        .finishedAt(null)
//                        .durationHours(null)
//                        .manager(MesWorkOrderDetailDto.AssigneeDto.builder().id(501L).name("김작업").build())
//                        .build()
//        );
//
//        MesWorkOrderDetailDto response = MesWorkOrderDetailDto.builder()
//                .mesId("1")
//                .mesNumber("WO-2024-001")
//                .productId("1")
//                .productName("산업용 모터 5HP")
//                .quantity(50)
//                .uomName("EA")
//                .progressPercent(65)
//                .statusCode("IN_PROGRESS")
//                .plan(MesWorkOrderDetailDto.PlanInfo.builder().startDate("2024-01-15").dueDate("2024-02-10").build())
//                .currentOperation("OP30")
//                .operations(operations)
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(response, "작업 지시 상세를 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/quotations")
//@Operation(
//    summary = "견적 목록 조회",
//    description = "견적 목록을 조회합니다.",
//    responses = {
//        @io.swagger.v3.oas.annotations.responses.ApiResponse(
//            responseCode = "200",
//            description = "성공"
//        )
//    }
//)
//public ResponseEntity<ApiResponse<Map<String, Object>>> getQuotationList(
//    @Parameter(name = "startDate", description = "요청일자 시작(yyyy-MM-dd)")
//    @RequestParam(required = false) String startDate,
//    @Parameter(name = "endDate", description = "요청일자 종료(yyyy-MM-dd)")
//    @RequestParam(required = false) String endDate,
//    @Parameter(name = "stockStatusCode", description = "가용재고 상태 (ALL, UNCHECKED, CHECKED)")
//    @RequestParam(required = false) String stockStatusCode,
//    @Parameter(name = "statusCode", description = "견적 상태 (ALL, NEW, CONFIRMED)")
//    @RequestParam(required = false) String statusCode,
//    @Parameter(name = "page", description = "페이지 번호")
//    @RequestParam(required = false, defaultValue = "0") int page,
//    @Parameter(name = "size", description = "페이지 크기")
//    @RequestParam(required = false, defaultValue = "10") int size
//) {
//    // 목데이터 생성
//    List<Map<String, Object>> quotations = new ArrayList<>();
//    String[] customers = {"현대자동차", "기아자동차", "삼성전자", "LG전자"};
//    String[] products = {"도어패널", "Hood Panel", "Fender Panel", "Trunk Lid"};
//    String[] statuses = {"NEW", "CONFIRMED"};
//    String[] stockStatuses = {"UNCHECKED", "CHECKED"};
//
//    for (int i = 0; i < 50; i++) {
//        Map<String, Object> q = new LinkedHashMap<>();
//        q.put("quotationNumber", String.format("Q-2024-%03d", i + 1));
//        q.put("customerCompanyName", customers[i % customers.length]);
//        q.put("product", products[i % products.length]);
//        q.put("requestQuantity", ((i + 1) * 10) + "EA");
//        LocalDateTime reqDate = LocalDateTime.of(2024, 2, 10, 0, 0).plusDays(i % 20);
//        q.put("requestDate", reqDate.toLocalDate().toString());
//        q.put("stockStatusCode", stockStatuses[i % stockStatuses.length]);
//        q.put("suggestedDueDate", (i % 2 == 0) ? reqDate.plusDays(5).toLocalDate().toString() : "-");
//        q.put("statusCode", statuses[i % statuses.length]);
//        quotations.add(q);
//    }
//        if(stockStatusCode.equals("ALL"))
//            stockStatusCode = null;
//
//        if(statusCode.equals("ALL"))
//            statusCode = null;
//    // 필터링
//    if (startDate != null) {
//        quotations.removeIf(q -> LocalDateTime.parse(q.get("requestDate").toString() + "T00:00:00")
//            .isBefore(LocalDateTime.parse(startDate + "T00:00:00")));
//    }
//    if (endDate != null) {
//        quotations.removeIf(q -> LocalDateTime.parse(q.get("requestDate").toString() + "T00:00:00")
//            .isAfter(LocalDateTime.parse(endDate + "T00:00:00")));
//    }
//    if (stockStatusCode!= null) {
//        String finalStockStatusCode = stockStatusCode;
//        quotations.removeIf(q -> !finalStockStatusCode.equalsIgnoreCase(q.get("stockStatusCode").toString()));
//    }
//    if (statusCode != null) {
//        String finalStatusCode = statusCode;
//        quotations.removeIf(q -> !finalStatusCode.equalsIgnoreCase(q.get("statusCode").toString()));
//    }
//
//    // 페이징
//    int total = quotations.size();
//    int fromIdx = page * size;
//    int toIdx = Math.min(fromIdx + size, total);
//    List<Map<String, Object>> pageContent = fromIdx < total ? quotations.subList(fromIdx, toIdx) : Collections.emptyList();
//
//    PageDto pageInfo = PageDto.builder()
//        .number(page)
//        .size(size)
//        .totalElements(total)
//        .totalPages((int) Math.ceil((double) total / size))
//        .hasNext(toIdx < total)
//        .build();
//
//    Map<String, Object> response = new HashMap<>();
//    response.put("content", pageContent);
//    response.put("page", pageInfo);
//
//    return ResponseEntity.ok(ApiResponse.success(response, "견적 목록을 조회했습니다.", HttpStatus.OK));
//}
//
//    @GetMapping("mps/toggle/products")
//    @Operation(
//            summary = "MPS 드롭다운용 아이템 목록 조회",
//            description = "MPS 화면의 제품 선택 드롭다운에 사용될 아이템 목록을 조회합니다."
//    )
//    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getMpsItemList() {
//        List<Map<String, String>> items = new ArrayList<>();
//
//        items.add(Map.of("productId", "1", "productName", "도어패널"));
//        items.add(Map.of("productId", "2", "productName", "Hood Panel"));
//        items.add(Map.of("productId", "3", "productName", "Fender Panel"));
//        items.add(Map.of("productId", "4", "productName", "Trunk Lid"));
//        items.add(Map.of("productId", "5", "productName", "Roof Panel"));
//
//        return ResponseEntity.ok(ApiResponse.success(items, "MPS 드롭다운 아이템 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//
//    //  제품 목록 (Product)
//    @GetMapping("/mrp/toggle/products")
//    @Operation(summary = "MPS 제품 토글 목록", description = "MPS 화면에서 사용할 제품 목록(토글)을 반환합니다.")
//    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getMpsProductToggles() {
//        List<Map<String, String>> products = List.of(
//                Map.of("id", "P-001", "name", "도어패널"),
//                Map.of("id", "P-002", "name", "Hood Panel"),
//                Map.of("id", "P-003", "name", "Fender Panel"),
//                Map.of("id", "P-004", "name", "Trunk Lid"),
//                Map.of("id", "P-005", "name", "Roof Panel")
//        );
//        return ResponseEntity.ok(ApiResponse.success(products, "MPS 제품 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    // 견적 목록 (Quotation)
//    @GetMapping("/mrp/toggle/quotations")
//    @Operation(summary = "MPS 견적 토글 목록", description = "MPS 화면에서 사용할 견적 목록(토글)을 반환합니다.")
//    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getMpsQuotationToggles() {
//        List<Map<String, String>> quotations = List.of(
//                Map.of("id", "Q-2024-001", "name", "현대자동차"),
//                Map.of("id", "Q-2024-002", "name", "기아자동차"),
//                Map.of("id", "Q-2024-003", "name", "삼성전자"),
//                Map.of("id", "Q-2024-004", "name", "LG전자")
//        );
//        return ResponseEntity.ok(ApiResponse.success(quotations, "MPS 견적 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    //가용 상태 코드 (Available Status)
//    @GetMapping("/mrp/toggle/status-codes")
//    @Operation(summary = "MPS 가용 상태 코드 목록", description = "MPS 화면에서 사용할 가용 상태 코드 목록(ALL, INSUFFICIENT, SUFFICIENT)을 반환합니다.")
//    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getMpsAvailableStatusCodes() {
//        List<Map<String, String>> availableStatuses = List.of(
//                Map.of("전체", "ALL"),
//                Map.of("부족", "INSUFFICIENT"),
//                Map.of("충족", "SUFFICIENT")
//        );
//        return ResponseEntity.ok(ApiResponse.success(availableStatuses, "MPS 가용 상태 코드 목록을 조회했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/mrp/toggle/planned-order-list-status-codes")
//    @Operation(summary = "MRP 계획 주문 상태 코드 목록", description = "MRP 계획 주문 상태 코드(ALL, PENDING, PLANNED, APPROVED, REJECTED) 목록을 반환합니다.")
//    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getPlannedOrderStatusCodes() {
//        List<Map<String, String>> statusCodes = List.of(
//                Map.of("전체","ALL"),
//                Map.of("대기", "PENDING"),
//                Map.of("계획", "PLANNED"),
//                Map.of("승인", "APPROVED"),
//                Map.of("반려", "REJECTED")
//        );
//        return ResponseEntity.ok(ApiResponse.success(statusCodes, "MRP 계획 주문 상태 코드 목록을 조회했습니다.", HttpStatus.OK));
//    }
//}
