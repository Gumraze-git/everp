//package org.ever._4ever_be_gw.mockdata.scmpp.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.ExampleObject;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import org.ever._4ever_be_gw.common.dto.stats.StatsMetricsDto;
//import org.ever._4ever_be_gw.common.dto.stats.StatsResponseDto;
//import org.ever._4ever_be_gw.common.exception.BusinessException;
//import org.ever._4ever_be_gw.common.exception.ErrorCode;
//import org.ever._4ever_be_gw.common.exception.ValidationException;
//import org.ever._4ever_be_gw.common.response.ApiResponse;
//import org.ever._4ever_be_gw.scm.mm.dto.MmSupplierUpdateRequestDto;
//import org.ever._4ever_be_gw.scm.mm.dto.SupplierCreateRequestDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.po.MmPurchaseOrderRejectRequestDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.po.PoDetailDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.po.PoItemDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.pr.MmPurchaseRequisitionCreateRequestDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.pr.MmPurchaseRequisitionRejectRequestDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.dto.pr.MmPurchaseRequisitionUpdateRequestDto;
//import org.ever._4ever_be_gw.mockdata.scmpp.service.MmService;
//import org.ever._4ever_be_gw.mockdata.scmpp.service.MmStatisticsService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import reactor.core.publisher.Mono;
//
//import java.time.LocalDate;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/scm-pp/mm")
//@Tag(name = "구매관리(MM)", description = "구매관리(MM) API")
//public class MmController {
//
//    private static final Set<String> ALLOWED_PERIODS = Set.of("week", "month", "quarter", "year");
//
//    private final MmStatisticsService mmStatisticsService;
//    private final MmService mmService;
//
//    public MmController(MmStatisticsService mmStatisticsService, MmService mmService) {
//        this.mmStatisticsService = mmStatisticsService;
//        this.mmService = mmService;
//    }
//
//    @GetMapping("/statistics")
//    @Operation(
//            summary = "MM 통계 조회",
//            description = "주간/월간/분기/연간 통계를 조회합니다. 요청 파라미터가 없으면 모든 기간이 포함됩니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공",
//                            content = @Content(
//                                    mediaType = "application/json",
//                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"OK\",\n  \"data\": {\n    \"week\": {\n      \"purchase_request_count\": {\n        \"value\": 184,\n        \"delta_rate\": 0.0728\n      },\n      \"purchase_approval_pending_count\": {\n        \"value\": 39,\n        \"delta_rate\": -0.0532\n      },\n      \"purchase_order_amount\": {\n        \"value\": 1283000000,\n        \"delta_rate\": 0.1044\n      },\n      \"purchase_order_approval_pending_count\": {\n        \"value\": 22,\n        \"delta_rate\": 0.1000\n      }\n    },\n    \"month\": {\n      \"purchase_request_count\": {\n        \"value\": 736,\n        \"delta_rate\": 0.0389\n      },\n      \"purchase_approval_pending_count\": {\n        \"value\": 161,\n        \"delta_rate\": -0.0417\n      },\n      \"purchase_order_amount\": {\n        \"value\": 5214000000,\n        \"delta_rate\": 0.0361\n      },\n      \"purchase_order_approval_pending_count\": {\n        \"value\": 94,\n        \"delta_rate\": 0.0652\n      }\n    },\n    \"quarter\": {\n      \"purchase_request_count\": {\n        \"value\": 2154,\n        \"delta_rate\": 0.0215\n      },\n      \"purchase_approval_pending_count\": {\n        \"value\": 472,\n        \"delta_rate\": -0.0186\n      },\n      \"purchase_order_amount\": {\n        \"value\": 15123000000,\n        \"delta_rate\": 0.0247\n      },\n      \"purchase_order_approval_pending_count\": {\n        \"value\": 281,\n        \"delta_rate\": 0.0426\n      }\n    },\n    \"year\": {\n      \"purchase_request_count\": {\n        \"value\": 8421,\n        \"delta_rate\": 0.0298\n      },\n      \"purchase_approval_pending_count\": {\n        \"value\": 1813,\n        \"delta_rate\": -0.0221\n      },\n      \"purchase_order_amount\": {\n        \"value\": 59876000000,\n        \"delta_rate\": 0.0312\n      },\n      \"purchase_order_approval_pending_count\": {\n        \"value\": 1103,\n        \"delta_rate\": 0.0185\n      }\n    }\n  }\n}" )
//                            )
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "400",
//                            description = "잘못된 periods",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "invalid_periods", value = "{\n  \"status\": 400,\n  \"success\": false,\n  \"message\": \"요청 파라미터 'periods' 값이 올바르지 않습니다.\",\n  \"errors\": { \"code\": 1007 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "기간 계산 실패",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "period_calc_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청을 처리할 수 없습니다. 기간 계산 중 오류가 발생했습니다.\",\n  \"errors\": { \"code\": 1010 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "서버 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\",\n  \"errors\": { \"code\": 1005, \"detail\": \"...\" }\n}"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<StatsResponseDto<StatsMetricsDto>>> getStatistics(
//
//    ) {
//        String periods = "";
//        // 파라미터 파싱 및 유효성 검증
//        List<String> requested = periods == null || periods.isBlank()
//                ? List.of("week", "month", "quarter", "year")
//                : Arrays.stream(periods.split(","))
//                .map(String::trim)
//                .map(String::toLowerCase)
//                .collect(Collectors.toList());
//
//        List<String> invalid = requested.stream()
//                .filter(p -> !ALLOWED_PERIODS.contains(p))
//                .toList();
//
//        if (periods != null && !periods.isBlank() && (!invalid.isEmpty() || requested.stream().noneMatch(ALLOWED_PERIODS::contains))) {
//            throw new BusinessException(ErrorCode.INVALID_PERIODS);
//        }
//
//        List<String> finalPeriods = requested.stream()
//                .filter(ALLOWED_PERIODS::contains)
//                .toList();
//
//        StatsResponseDto<StatsMetricsDto> data = mmStatisticsService.getStatistics(finalPeriods);
//        return ResponseEntity.ok(ApiResponse.success(data, "OK", HttpStatus.OK));
//    }
//
//    @GetMapping("/purchase-requisitions")
//    @Operation(
//            summary = "구매요청 목록 조회",
//            description = "구매요청서를 페이지네이션으로 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "권한 없음"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "검증 실패"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "서버 오류"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getPurchaseRequisitions(
//            @Parameter(description = "상태 필터: PENDING, APPROVED")
//            @RequestParam(name = "statusCode", required = false) String statusCode,
//            @Parameter(description = "요청자명 검색")
//            @RequestParam(name = "requesterName", required = false) String requesterName,
//            @Parameter(description = "요청 부서 ID 필터")
//            @RequestParam(name = "departmentId", required = false) String departmentId,
//            @Parameter(description = "생성일 시작(YYYY-MM-DD)")
//            @RequestParam(name = "createdFrom", required = false) String createdFrom,
//            @Parameter(description = "생성일 종료(YYYY-MM-DD)")
//            @RequestParam(name = "createdTo", required = false) String createdTo,
//            @Parameter(description = "정렬 필드,정렬방향")
//            @RequestParam(name = "sort", required = false) String sort,
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false) Integer page,
//            @Parameter(description = "페이지 크기(최대 200)")
//            @RequestParam(name = "size", required = false) Integer size
//    ) {
//        // 422 검증
//        List<Map<String, String>> errors = new java.util.ArrayList<>();
//        final java.time.LocalDate[] fromDateArr = {null};
//        final java.time.LocalDate[] toDateArr = {null};
//
//        if (createdFrom != null) {
//            try {
//                fromDateArr[0] = java.time.LocalDate.parse(createdFrom);
//            } catch (Exception e) {
//                errors.add(Map.of("field", "createdFrom", "reason", "INVALID_DATE"));
//            }
//        }
//        if (createdTo != null) {
//            try {
//                toDateArr[0] = java.time.LocalDate.parse(createdTo);
//            } catch (Exception e) {
//                errors.add(Map.of("field", "createdTo", "reason", "INVALID_DATE"));
//            }
//        }
//        if (size != null && size > 200) {
//            errors.add(Map.of("field", "size", "reason", "MAX_200"));
//        }
//        if (!errors.isEmpty()) {
//            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
//        }
//
//        final java.time.LocalDate fromDate = fromDateArr[0];
//        final java.time.LocalDate toDate = toDateArr[0];
//
//        // 기본값 처리
//        String effectiveSort = (sort == null || sort.isBlank()) ? "createdAt,desc" : sort;
//        int p = (page == null || page < 0) ? 0 : page;
//        int s = (size == null || size < 1) ? 20 : size;
//
//        // 403 샘플 조건: 과거 특정 기준 이전 조회는 금지 (모킹)
//        if (fromDate != null && fromDate.isBefore(java.time.LocalDate.of(2024, 1, 1))) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_RANGE);
//        }
//
//        // 성공 응답 (목업) - 50개
//        java.util.List<Map<String, Object>> content = new java.util.ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            final String id = String.valueOf(1 + i); // 상세와 정렬을 맞추기 위해 1~50 사용
//            final String requesterId = String.valueOf(123 + (i % 5));
//            final String requesterNameVal = switch (i % 5) {
//                case 0 -> "홍길동";
//                case 1 -> "김민수";
//                case 2 -> "이영희";
//                case 3 -> "박철수";
//                default -> "최수민";
//            };
//            final int itemCount = 1 + (i % 3);
//            final java.time.Instant createdAt = java.time.Instant.parse("2025-10-05T12:30:45Z").plusSeconds(60L * i);
//
//            Map<String, Object> row = new java.util.LinkedHashMap<>();
//            row.put("id", id);
//            // 변경: PR 번호 포맷을 PR-2025-001, PR-2025-002 ... 로 반환
//            row.put("prNumber", String.format("PR-2025-%03d", 1 + i));
//            row.put("requesterId", requesterId);
//            row.put("requesterName", requesterNameVal);
//            String deptId = (i % 2 == 0) ? "12" : "15";
//            String deptName = "12".equals(deptId) ? "영업1팀" : "경영지원팀";
//            row.put("departmentId", deptId);
//            row.put("departmentName", deptName);
//            // 요청일
//            row.put("requestDate", java.time.LocalDate.from(createdAt.atZone(java.time.ZoneOffset.UTC)));
//            // 추가: 납기일(desiredDeliveryDate) 반환 (샘플: 요청일 기준 +7일)
//            java.time.LocalDate desiredDeliveryDate = createdAt.atZone(java.time.ZoneOffset.UTC).toLocalDate().plusDays(7);
//            row.put("desiredDeliveryDate", desiredDeliveryDate);
//
//            row.put("createdBy", requesterId);
//            // 총 금액(totalAmount) 추가: 항목 수와 인덱스로 가중 합산 (목업)
//            long base = 250_000L;
//            long totalAmount = base * itemCount + (i % 5) * 100_000L;
//            row.put("totalAmount", totalAmount);
//            content.add(row);
//        }
//
//        // 필터 적용 (requesterName, departmentId, status, createdFrom, createdTo)
//        java.util.List<Map<String, Object>> filtered = content;
//
//        // 이름 필터링
//        if (requesterName != null && !requesterName.isBlank()) {
//            final String kw = requesterName.toLowerCase();
//            filtered = filtered.stream()
//                    .filter(m -> String.valueOf(m.get("requesterName")).toLowerCase().contains(kw))
//                    .toList();
//        }
//
//        // 부서 ID 필터링
//        if (departmentId != null && !departmentId.isEmpty()) {
//            final String did = departmentId;
//            filtered = filtered.stream()
//                    .filter(m -> did.equals(String.valueOf(m.get("departmentId"))))
//                    .toList();
//        }
//
//        // 상태 필터링
//        if (statusCode != null && !statusCode.isBlank()) {
//            final String statusUpper = statusCode.toUpperCase(Locale.ROOT);
//            filtered = filtered.stream()
//                    .filter(m -> {
//                        // 목업 데이터: ID 기반으로 상태 결정 (홀수는 PENDING, 짝수는 APPROVED)
//                        long itemId = ((Number)m.get("id")).longValue();
//                        String itemStatus = (itemId % 2 == 0) ? "APPROVED" : "PENDING";
//                        return statusUpper.equals(itemStatus);
//                    })
//                    .toList();
//        }
//
//        // 시작 날짜 필터링
//        if (fromDate != null) {
//            filtered = filtered.stream()
//                    .filter(m -> {
//                        Object requestDateObj = m.get("requestDate");
//                        if (requestDateObj instanceof java.time.LocalDate) {
//                            return !((java.time.LocalDate) requestDateObj).isBefore(fromDate);
//                        }
//                        return true; // 날짜 형식이 아니면 포함
//                    })
//                    .toList();
//        }
//
//        // 종료 날짜 필터링
//        if (toDate != null) {
//            filtered = filtered.stream()
//                    .filter(m -> {
//                        Object requestDateObj = m.get("requestDate");
//                        if (requestDateObj instanceof java.time.LocalDate) {
//                            return !((java.time.LocalDate) requestDateObj).isAfter(toDate);
//                        }
//                        return true; // 날짜 형식이 아니면 포함
//                    })
//                    .toList();
//        }
//
//        int total = filtered.size();
//        int fromIdx = Math.min(p * s, total);
//        int toIdx = Math.min(fromIdx + s, total);
//        java.util.List<Map<String, Object>> pageContent = filtered.subList(fromIdx, toIdx);
//
//        org.ever._4ever_be_gw.common.dto.PageDto pageDto = org.ever._4ever_be_gw.common.dto.PageDto.builder()
//                .number(p)
//                .size(s)
//                .totalElements(total)
//                .totalPages(s == 0 ? 0 : (int) Math.ceil((double) total / s))
//                .hasNext(p + 1 < (s == 0 ? 0 : (int) Math.ceil((double) total / s)))
//                .build();
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("content", pageContent);
//        data.put("page", pageDto);
//
//        return ResponseEntity.ok(ApiResponse.<Object>success(
//                data, "구매요청서 목록입니다.", HttpStatus.OK
//        ));
//    }
//
//    @PostMapping("/purchase-requisitions")
//    @Operation(
//            summary = "비재고성 자재 구매요청서 생성",
//            description = "요청 본문에 포함된 품목들로 비재고성 자재 구매요청서를 생성합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "201",
//                            description = "생성됨"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "400",
//                            description = "본문 형식 오류"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "검증 실패"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "서버 오류"
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> createPurchaseRequisition(
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true
//            )
//            @RequestBody MmPurchaseRequisitionCreateRequestDto request
//    ) {
//        // 목업에서는 항상 성공 응답 반환
//
//        // 성공 응답
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("purchaseRequisitionId", String.valueOf(202510120001L));
//        data.put("prNumber", "PR-NS-2025-00001");
//        data.put("departmentId", String.valueOf(12));
//        data.put("departmentName", "경영지원팀");
//        data.put("requesterId", request.getRequesterId());
//        data.put("requestDate", java.time.LocalDate.now().toString());
//        data.put("createdAt", java.time.Instant.now());
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.success(data, "비재고성 자재 구매요청서가 생성되었습니다.", HttpStatus.CREATED));
//    }
//
//    @GetMapping("/purchase-requisitions/{purchaseRequisitionId}")
//    @Operation(
//            summary = "구매요청 상세 조회",
//            description = "구매요청서 단건 상세를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 구매요청서를 조회할 권한이 없습니다.\",\n  \"errors\": { \"code\": 1011 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "리소스 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 구매요청서를 찾을 수 없습니다: purchaseId=11\",\n  \"errors\": { \"code\": 1012 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "서버 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요.\",\n  \"errors\": { \"code\": 1005 }\n}"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getPurchaseRequisitionDetail(
//            @Parameter(description = "구매요청 ID", example = "1")
//            @PathVariable("purchaseRequisitionId") String purchaseRequisitionId
//    ) {
//        // 모킹된 에러 시나리오
//        if ("403001".equals(purchaseRequisitionId)) {
//            throw new BusinessException(ErrorCode.FORBIDDEN_PURCHASE_ACCESS);
//        }
//        // ID가 비어있으면 404 처리
//        if (purchaseRequisitionId == null || purchaseRequisitionId.isEmpty()) {
//            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "purchaseId=" + purchaseRequisitionId);
//        }
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("id", purchaseRequisitionId);
//        data.put("purchaseRequisitionNumber", "PR-2024-001");
//        data.put("requesterId", "123");
//        data.put("requesterName", "김철수");
//        data.put("departmentId", "77");
//        data.put("departmentName", "생산팀");
//        // 요청일 추가 (createdAt의 날짜 기준)
//        data.put("requestDate", java.time.LocalDate.parse("2024-01-15"));
//        data.put("createdAt", java.time.Instant.parse("2024-01-15T00:00:00Z"));
//        data.put("dueDate", java.time.LocalDate.parse("2024-01-25"));
//        data.put("statusCode", "APPROVED");
//
//        java.util.List<Map<String, Object>> items = new java.util.ArrayList<>();
//        items.add(new java.util.LinkedHashMap<>() {{
//            put("itemId", "40000123");
//            put("itemName", "강판");
//            put("quantity", 500);
//            put("uomCode", "EA");
//            put("unitPrice", 5000);
//            put("amount", 2_500_000);
//        }});
//        items.add(new java.util.LinkedHashMap<>() {{
//            put("itemId", "987654321");
//            put("itemName", "볼트");
//            put("quantity", 100);
//            put("uomCode", "EA");
//            put("unitPrice", 500);
//            put("amount", 50_000);
//        }});
//        data.put("items", items);
//        data.put("totalAmount", 2_550_000);
//
//        return ResponseEntity.ok(ApiResponse.<Object>success(
//                data, "구매요청서 상세입니다.", HttpStatus.OK
//        ));
//    }
//
//    @PutMapping("/purchase-requisitions/{purchaseRequisitionId}")
//    @Operation(
//            summary = "구매요청서 수정",
//            description = "비재고성(NON_STOCK)이며 대기(PENDING) 상태인 구매요청서를 수정합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "수정 성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "구매요청서를 찾을 수 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 구매요청서를 찾을 수 없습니다: purchaseId=999999\",\n  \"errors\": { \"code\": 1012 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "409",
//                            description = "수정 불가 상태",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "conflict", value = "{\n  \"status\": 409,\n  \"success\": false,\n  \"message\": \"현재 상태에서는 수정이 허용되지 않습니다. (required: NON_STOCK & PENDING)\",\n  \"errors\": { \"code\": 1035 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "본문 검증 실패",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 본문 검증에 실패했습니다.\",\n  \"errors\": [ { \"field\": \"items[0].op\", \"reason\": \"ALLOWED_VALUES: ADD, UPDATE, REMOVE\" } ]\n}"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> updatePurchaseRequisition(
//            @Parameter(description = "구매요청 ID", example = "102345")
//            @PathVariable("purchaseRequisitionId") String purchaseRequisitionId,
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(name = "request", value = "{\n  \"dueDate\": \"2025-10-25\",\n  \"items\": [\n    { \"operation\": \"ADD\", \"itemName\": \"화이트보드 마커\", \"quantity\": 50, \"uomName\": \"EA\", \"unitPrice\": 3000, \"supplierName\": \"문구나라\", \"purpose\": \"소모품 보충\", \"note\": \"색상 혼합\" },\n    { \"operation\": \"UPDATE\", \"itemId\": \"900001\", \"quantity\": 12, \"unitPrice\": 14000 }\n  ]\n}"))
//            )
//            @RequestBody MmPurchaseRequisitionUpdateRequestDto request
//    ) {
//        // 목업에서는 항상 성공 응답 반환
//        if (!"102345".equals(purchaseRequisitionId)) {
//            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "purchaseId=" + purchaseRequisitionId);
//        }
//
//        LocalDate baseDesiredDate = LocalDate.parse("2025-10-15");
//        LocalDate effectiveDesiredDate = (request != null && request.getDueDate() != null)
//                ? request.getDueDate()
//                : baseDesiredDate;
//
//        List<Map<String, String>> errors = new ArrayList<>();
//        if (request != null && request.getDueDate() != null && !request.getDueDate().isAfter(LocalDate.now())) {
//            errors.add(Map.of("field", "desiredDeliveryDate", "reason", "PAST_DATE"));
//        }
//
//        // 초기 아이템
//        List<Map<String, Object>> items = new ArrayList<>();
//
//        Map<String, Object> baseItem1 = new LinkedHashMap<>();
//        baseItem1.put("itemId", "900001");
//        baseItem1.put("itemName", "A4 복사용지");
//        baseItem1.put("quantity", 10);
//        baseItem1.put("uomName", "BOX");
//        baseItem1.put("unitPrice", 15000L);
//        baseItem1.put("totalAmount", 150000L);
//        baseItem1.put("supplierName", "OO물산");
//        baseItem1.put("purpose", "사무실 비품 보강");
//        baseItem1.put("note", "긴급 구매");
//        items.add(baseItem1);
//
//        Map<String, Object> baseItem2 = new LinkedHashMap<>();
//        baseItem2.put("itemId", "900002");
//        baseItem2.put("itemName", "화이트보드 세정제");
//        baseItem2.put("quantity", 5);
//        baseItem2.put("uomName", "EA");
//        baseItem2.put("unitPrice", 12000L);
//        baseItem2.put("totalAmount", 60000L);
//        baseItem2.put("supplierName", "청소나라");
//        baseItem2.put("purpose", "사무실 유지보수");
//        baseItem2.put("note", null);
//        items.add(baseItem2);
//
//        // Map으로 빠르게 접근하기 위한 인덱스 생성
//        Map<String, Map<String, Object>> itemIndex = items.stream()
//                .filter(m -> m.get("itemId") != null)
//                .collect(Collectors.toMap(
//                        m -> (String) m.get("itemId"),
//                        m -> m,
//                        (a, b) -> a,
//                        LinkedHashMap::new
//                ));
//
//        // 유효성 검증
//        if (request != null && request.getItems() != null) {
//            for (int i = 0; i < request.getItems().size(); i++) {
//                var incoming = request.getItems().get(i);
//                String fieldPrefix = "items[" + i + "]";
//                String op = incoming.getOperation() == null ? null : incoming.getOperation().trim().toUpperCase();
//
//                if (op == null || op.isEmpty()) {
//                    errors.add(Map.of("field", fieldPrefix + ".op", "reason", "REQUIRED"));
//                    continue;
//                }
//                if (!Set.of("ADD", "UPDATE", "REMOVE").contains(op)) {
//                    errors.add(Map.of("field", fieldPrefix + ".op", "reason", "ALLOWED_VALUES: ADD, UPDATE, REMOVE"));
//                    continue;
//                }
//                if ("ADD".equals(op) && (incoming.getQuantity() == null || incoming.getQuantity() <= 0)) {
//                    errors.add(Map.of("field", fieldPrefix + ".quantity", "reason", "MUST_BE_POSITIVE"));
//                }
//                if (("UPDATE".equals(op) || "REMOVE".equals(op)) && (incoming.getItemId() == null)) {
//                    errors.add(Map.of("field", fieldPrefix + ".id", "reason", "REQUIRED"));
//                }
//                if ("UPDATE".equals(op) && incoming.getQuantity() != null && incoming.getQuantity() <= 0) {
//                    errors.add(Map.of("field", fieldPrefix + ".quantity", "reason", "MUST_BE_POSITIVE"));
//                }
//            }
//        }
//
//        if (!errors.isEmpty()) {
//            throw new ValidationException(ErrorCode.BODY_VALIDATION_FAILED, errors);
//        }
//
//        long nextItemIdSeq = 900003L;
//
//        if (request != null && request.getItems() != null) {
//            for (var incoming : request.getItems()) {
//                String op = incoming.getOperation() == null ? null : incoming.getOperation().trim().toUpperCase();
//                if (op == null) continue;
//
//                switch (op) {
//                    case "ADD" -> {
//                        Map<String, Object> newItem = new LinkedHashMap<>();
//                        String newItemId = String.valueOf(nextItemIdSeq++);
//                        newItem.put("itemId", newItemId);
//                        newItem.put("itemName", incoming.getItemName());
//                        newItem.put("quantity", incoming.getQuantity());
//                        newItem.put("uomName", incoming.getUomName());
//                        newItem.put("unitPrice", incoming.getUnitPrice());
//                        Long total = (incoming.getQuantity() != null && incoming.getUnitPrice() != null)
//                                ? incoming.getQuantity().longValue() * incoming.getUnitPrice()
//                                : null;
//                        newItem.put("totalAmount", total);
//                        newItem.put("supplierName", incoming.getSupplierName());
//                        newItem.put("purpose", incoming.getPurpose());
//                        newItem.put("note", incoming.getNote());
//                        items.add(newItem);
//                        itemIndex.put(newItemId, newItem);
//                    }
//                    case "UPDATE" -> {
//                        if (incoming.getItemId() == null) break;
//                        Map<String, Object> target = itemIndex.get(incoming.getItemId());
//                        if (target == null) break;
//
//                        if (incoming.getQuantity() != null) target.put("quantity", incoming.getQuantity());
//                        if (incoming.getUnitPrice() != null) target.put("unitPrice", incoming.getUnitPrice());
//                        if (incoming.getItemName() != null) target.put("itemName", incoming.getItemName());
//                        if (incoming.getUomName() != null) target.put("uomName", incoming.getUomName());
//                        if (incoming.getSupplierName() != null) target.put("supplierName", incoming.getSupplierName());
//                        if (incoming.getPurpose() != null) target.put("purpose", incoming.getPurpose());
//                        if (incoming.getNote() != null) target.put("note", incoming.getNote());
//                    }
//                    case "REMOVE" -> {
//                        if (incoming.getItemId() == null) break;
//                        items.removeIf(m -> incoming.getItemId().equals(m.get("itemId")));
//                        itemIndex.remove(incoming.getItemId());
//                    }
//                    default -> {}
//                }
//            }
//        }
//
//        // 총 금액 계산
//        long totalAmount = items.stream()
//                .mapToLong(m -> ((Number) m.get("unitPrice")).longValue() * ((Number) m.get("quantity")).intValue())
//                .sum();
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("purchaseRequisitionId", purchaseRequisitionId);
//        data.put("purchaseRequisitionNumber", "PR-2025-00001");
//        data.put("purchaseRequisitionType", "NON_STOCK");
//        data.put("statusCode", "PENDING");
//        data.put("departmentId", "12");
//        data.put("departmentName", "경영지원팀");
//        data.put("dueDate", effectiveDesiredDate);
//        data.put("totalAmount", totalAmount);
//        data.put("items", items);
//
//        return ResponseEntity.ok(ApiResponse.success(data, "구매요청서가 수정되었습니다.", HttpStatus.OK));
//    }
//
//
//    @DeleteMapping("/purchase-requisitions/{purchaseRequisitionId}")
//    @Operation(
//            summary = "구매요청서 삭제",
//            description = "대기(PENDING) 상태인 구매요청서를 삭제합니다. 삭제된 문서는 복구할 수 없습니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "삭제 성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "구매요청서 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 구매요청서를 찾을 수 없습니다: prId=999999\",\n  \"errors\": { \"code\": 1012 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "409",
//                            description = "삭제 불가 상태",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "conflict", value = "{\n  \"status\": 409,\n  \"success\": false,\n  \"message\": \"대기 상태인 구매요청서만 삭제할 수 있습니다.\",\n  \"errors\": { \"code\": 1050 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "처리 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\",\n  \"errors\": { \"code\": 1014 }\n}"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> deletePurchaseRequisition(
//            @Parameter(description = "구매요청 ID", example = "102345")
//            @PathVariable("purchaseRequisitionId") String prId
//    ) {
//        // 목업에서는 항상 성공 응답 반환
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("purchaseRequisitionId", prId);
//        data.put("purchaseRequisitionNumber", "PR-NS-2025-00001");
//        data.put("statusCode", "DELETED");
//        data.put("deletedAt", java.time.Instant.parse("2025-10-07T11:15:00Z"));
//
//        return ResponseEntity.ok(ApiResponse.success(data, "구매요청서가 삭제되었습니다.", HttpStatus.OK));
//    }
//
//    @PostMapping("/purchase-requisitions/{purchaseRequisitionId}/release")
//    @Operation(
//            summary = "구매요청서 승인",
//            description = "구매요청서를 승인(Release) 처리합니다. 승인 가능한 역할 토큰이 필요합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "승인 성공",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매요청서가 승인되었습니다.\",\n  \"data\": {\n    \"id\": 102345,\n    \"prNumber\": \"PR-NS-2025-00001\",\n    \"prType\": \"NON_STOCK\",\n    \"status\": \"APPROVED\",\n    \"origin\": \"MRP\",\n    \"originRefId\": \"MRP-2025-10-01-00123\",\n    \"requesterId\": 123,\n    \"requesterName\": \"홍길동\",\n    \"departmentId\": 12,\n    \"departmentName\": \"영업1팀\",\n    \"approvedAt\": \"2025-10-07T09:15:00Z\",\n    \"approvedBy\": 777,\n    \"approvedByName\": \"김관리자\"\n  }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "승인 권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"승인 권한이 없습니다. (required role: PR_APPROVER|PURCHASING_MANAGER|ADMIN)\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "구매요청서 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 구매요청서를 찾을 수 없습니다: prId=999999\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "승인 불가 상태",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "invalid_transition", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"해당 상태에서는 승인할 수 없습니다.\",\n  \"errors\": [ { \"field\": \"status\", \"reason\": \"INVALID_TRANSITION: DRAFT/REJECTED/VOID/APPROVED → APPROVED 불가\" } ]\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "처리 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"요청 처리 중 오류가 발생했습니다.\" }"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> releasePurchaseRequisition(
//            @Parameter(description = "구매요청 ID", example = "102345")
//            @PathVariable("purchaseRequisitionId") String prId
//    ) {
//        // 목업에서는 인증 처리 생략 (항상 성공)
//
//        if (prId == null || prId.length() < 6) {
//            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "prId=" + prId);
//        }
//        if ("102347".equals(prId)) {
//            List<Map<String, String>> errors = List.of(Map.of(
//                    "field", "status",
//                    "reason", "INVALID_TRANSITION: DRAFT/REJECTED/VOID/APPROVED → APPROVED 불가"
//            ));
//            throw new ValidationException(ErrorCode.PURCHASE_REQUEST_INVALID_TRANSITION, errors);
//        }
//        if ("102399".equals(prId)) {
//            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_APPROVAL_PROCESSING_ERROR);
//        }
//        if (!"102345".equals(prId)) {
//            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "prId=" + prId);
//        }
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("purchaseRequisitionId", prId);
//        data.put("purchaseRequisitionNumber", "PR-NS-2025-00001");
//        data.put("type", "NON_STOCK");
//        data.put("statusCode", "APPROVED");
//        data.put("requesterId", "123");
//        data.put("requesterName", "홍길동");
//        data.put("departmentId", "12");
//        data.put("departmentName", "영업1팀");
//        data.put("approvedAt", java.time.Instant.parse("2025-10-07T09:15:00Z"));
//        data.put("approvedBy", "777");
//        data.put("approvedByName", "김관리자");
//
//        return ResponseEntity.ok(ApiResponse.success(data, "구매요청서가 승인되었습니다.", HttpStatus.OK));
//    }
//
//    @PostMapping("/purchase-requisitions/{purchaseRequisitionId}/reject")
//    @Operation(
//            summary = "구매요청서 반려",
//            description = "구매요청서를 반려 처리하고 사유를 기록합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "반려 성공",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"구매요청서가 반려되었습니다.\",\n  \"data\": {\n    \"id\": 102345,\n    \"prNumber\": \"PR-NS-2025-00001\",\n    \"status\": \"REJECTED\",\n    \"origin\": \"MRP\",\n    \"originRefId\": \"MRP-2025-10-01-00123\",\n    \"requesterId\": 123,\n    \"requesterName\": \"홍길동\",\n    \"departmentId\": 12,\n    \"departmentName\": \"영업1팀\",\n    \"rejectedAt\": \"2025-10-07T10:30:00Z\",\n    \"rejectedBy\": 777,\n    \"rejectedByName\": \"김관리자\",\n    \"rejectReason\": \"예산 초과로 반려합니다.\"\n  }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "반려 권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"해당 문서를 반려할 권한이 없습니다. (required role: PR_APPROVER|PURCHASING_MANAGER|ADMIN)\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "구매요청서 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 구매요청서를 찾을 수 없습니다: prId=999999\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "반려 불가 상태 또는 본문 검증 실패",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "invalid_transition", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"해당 상태에서는 반려할 수 없습니다.\",\n  \"errors\": [ { \"field\": \"status\", \"reason\": \"INVALID_TRANSITION: DRAFT/APPROVED/REJECTED/VOID → REJECTED 불가\" } ]\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "처리 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"요청 처리 중 오류가 발생했습니다.\" }"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> rejectPurchaseRequisition(
//            @Parameter(description = "구매요청 ID", example = "102345")
//            @PathVariable("purchaseRequisitionId") String prId,
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(value = "{ \"comment\": \"예산 초과로 반려합니다.\" }"))
//            )
//            @RequestBody MmPurchaseRequisitionRejectRequestDto request
//    ) {
//        // 목업에서는 인증 처리 생략 (항상 성공)
//
//        List<Map<String, String>> errors = new java.util.ArrayList<>();
//        if (request == null || request.getComment() == null || request.getComment().isBlank()) {
//            errors.add(Map.of("field", "comment", "reason", "REQUIRED"));
//        }
//        if (!errors.isEmpty()) {
//            throw new ValidationException(ErrorCode.BODY_VALIDATION_FAILED, errors);
//        }
//
//        if (prId == null || prId.length() < 6) {
//            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "prId=" + prId);
//        }
//        if ("102347".equals(prId)) {
//            List<Map<String, String>> transitionErrors = List.of(Map.of(
//                    "field", "status",
//                    "reason", "INVALID_TRANSITION: DRAFT/APPROVED/REJECTED/VOID → REJECTED 불가"
//            ));
//            throw new ValidationException(ErrorCode.PURCHASE_REQUEST_REJECTION_INVALID_TRANSITION, transitionErrors);
//        }
//        if ("102399".equals(prId)) {
//            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_REJECTION_PROCESSING_ERROR);
//        }
//        if (!"102345".equals(prId)) {
//            throw new BusinessException(ErrorCode.PURCHASE_REQUEST_NOT_FOUND, "prId=" + prId);
//        }
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("purchaseRequisitionId", prId);
//        data.put("purchaseRequisitionIdNumber", "PR-NS-2025-00001");
//        data.put("statusCode", "REJECTED");
//        data.put("requesterId", "123");
//        data.put("requesterName", "홍길동");
//        data.put("departmentId", "12");
//        data.put("departmentName", "영업1팀");
//        data.put("rejectedAt", java.time.Instant.parse("2025-10-07T10:30:00Z"));
//        data.put("rejectedBy", "777");
//        data.put("rejectedByName", "김관리자");
//        data.put("rejectReason", request.getComment());
//
//        return ResponseEntity.ok(ApiResponse.success(data, "구매요청서가 반려되었습니다.", HttpStatus.OK));
//    }
//
//    // ---------------- Purchase Orders List ----------------
//    @GetMapping("/purchase-orders")
//    @Operation(
//            summary = "발주서 목록 조회",
//            description = "발주서를 조건에 따라 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "unauthorized", value = "{\n  \"status\": 401,\n  \"success\": false,\n  \"message\": \"인증이 필요합니다.\",\n  \"errors\": { \"code\": 1006 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 데이터를 조회할 권한이 없습니다.\",\n  \"errors\": { \"code\": 1013 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "검증 실패",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [ { \"field\": \"status\", \"reason\": \"ALLOWED_VALUES: APPROVED, PENDING, REJECTED, DELIVERED, ALL\" } ]\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "서버 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\",\n  \"errors\": { \"code\": 1014 }\n}"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getPurchaseOrders(
//            @Parameter(description = "상태 필터: PENDING, APPROVED, REJECTED, DELIVERED, ALL(전체)")
//            @RequestParam(name = "statusCode", required = false) String statusCode,
//            @Parameter(description = "공급업체명 검색")
//            @RequestParam(name = "supplierName", required = false) String supplierName,
//            @Parameter(description = "발주서 번호 검색")
//            @RequestParam(name = "purchaseOrderNumber", required = false) String purchaseOrderNumber,
//            @Parameter(description = "주문일 시작(YYYY-MM-DD)")
//            @RequestParam(name = "orderDateFrom", required = false) String orderDateFrom,
//            @Parameter(description = "주문일 종료(YYYY-MM-DD)")
//            @RequestParam(name = "orderDateTo", required = false) String orderDateTo,
//            @Parameter(description = "정렬 필드,정렬방향(orderDate|deliveryDate)")
//            @RequestParam(name = "sort", required = false) String sort,
//            @Parameter(description = "페이지 번호(0-base)")
//            @RequestParam(name = "page", required = false) Integer page,
//            @Parameter(description = "페이지 크기(최대 200)")
//            @RequestParam(name = "size", required = false) Integer size
//    ) {
//        List<Map<String, String>> errors = new java.util.ArrayList<>();
//        final java.time.LocalDate[] fromDateArr = {null};
//        final java.time.LocalDate[] toDateArr = {null};
//        final String finalStatus = statusCode != null ? statusCode.toUpperCase(Locale.ROOT) : null;
//
//        // 간단한 검증만 수행
//        if (orderDateFrom != null) {
//            try {
//                fromDateArr[0] = java.time.LocalDate.parse(orderDateFrom);
//            } catch (Exception e) {
//                errors.add(Map.of("field", "orderDateFrom", "reason", "INVALID_DATE"));
//            }
//        }
//        if (orderDateTo != null) {
//            try {
//                toDateArr[0] = java.time.LocalDate.parse(orderDateTo);
//            } catch (Exception e) {
//                errors.add(Map.of("field", "orderDateTo", "reason", "INVALID_DATE"));
//            }
//        }
//        if (size != null && size > 200) {
//            errors.add(Map.of("field", "size", "reason", "MAX_200"));
//        }
//        if (!errors.isEmpty()) {
//            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
//        }
//
//        final java.time.LocalDate fromDate = fromDateArr[0];
//        final java.time.LocalDate toDate = toDateArr[0];
//
//        String effectiveSort = (sort == null || sort.isBlank()) ? "orderDate,desc" : sort;
//        String[] sortParts = effectiveSort.split(",");
//        String sortField = sortParts[0].trim();
//        String sortDirection = sortParts.length > 1 ? sortParts[1].trim().toLowerCase(Locale.ROOT) : "desc";
//        if (!java.util.Set.of("orderDate", "deliveryDate").contains(sortField)) {
//            sortField = "orderDate";
//        }
//        if (!sortDirection.equals("asc") && !sortDirection.equals("desc")) {
//            sortDirection = "desc";
//        }
//
//        int pageIndex = (page == null || page < 0) ? 0 : page;
//        int pageSize = (size == null || size < 1) ? 10 : size;
//
//        // 목업에서는 항상 성공 응답 반환
//
//        java.util.List<Map<String, Object>> all = new java.util.ArrayList<>();
//        String[] suppliers = {"대한철강","한국알루미늄","포스코","효성중공업","현대제철","두산중공업","세아베스틸","KG동부제철","동국제강","티엠씨메탈"};
//        String[] itemsSummary = {"강판 500kg, 알루미늄 300kg","알루미늄 시트 200매","고강도 스틸 1톤","볼트 1000개","스테인리스 파이프 200개","알루미늄 판재 100매","스틸 코일 3톤","강철 빔 50개","철판 2톤","알루미늄 봉 100개"};
//        String[] statusList = {"APPROVED","PENDING","REJECTED","APPROVED","PENDING","DELIVERED","REJECTED","PENDING","APPROVED","DELIVERED"};
//        java.time.LocalDate baseDate = java.time.LocalDate.of(2024,1,18);
//        for (int i = 0; i < 50; i++) {
//            int idx = i % 10;
//            Map<String, Object> row = new LinkedHashMap<>();
//            row.put("purchaseOrderId", String.valueOf(1001 + i));
//            row.put("purchaseOrderNumber", String.format("PO-2024-%03d", i + 1));
//            row.put("supplierName", suppliers[idx]);
//            row.put("itemsSummary", itemsSummary[idx]);
//            java.time.LocalDate od = baseDate.minusDays(idx);
//            row.put("orderDate", od.toString());
//            row.put("deliveryDate", od.plusDays(7).toString());
//            row.put("totalAmount", 5_000_000 - (i * 120_000));
//            row.put("statusCode", statusList[idx]);
//            all.add(row);
//        }
//
//        java.util.List<Map<String, Object>> filtered = all;
//        if (finalStatus != null && !"ALL".equals(finalStatus)) {
//            filtered = filtered.stream().filter(m -> finalStatus.equals(m.get("statusCode"))).toList();
//        }
//        if (supplierName != null && !supplierName.isBlank()) {
//            String keyword = supplierName.toLowerCase(Locale.ROOT);
//            filtered = filtered.stream()
//                    .filter(m -> String.valueOf(m.get("supplierName")).toLowerCase(Locale.ROOT).contains(keyword))
//                    .toList();
//        }
//        if (purchaseOrderNumber != null && !purchaseOrderNumber.isBlank()) {
//            String keyword = purchaseOrderNumber.toLowerCase(Locale.ROOT);
//            filtered = filtered.stream()
//                    .filter(m -> String.valueOf(m.get("purchaseOrderNumber")).toLowerCase(Locale.ROOT).contains(keyword))
//                    .toList();
//        }
//        if (fromDate != null) {
//            filtered = filtered.stream()
//                    .filter(m -> !java.time.LocalDate.parse(String.valueOf(m.get("orderDate"))).isBefore(fromDate))
//                    .toList();
//        }
//        if (toDate != null) {
//            filtered = filtered.stream()
//                    .filter(m -> !java.time.LocalDate.parse(String.valueOf(m.get("orderDate"))).isAfter(toDate))
//                    .toList();
//        }
//
//        java.util.Comparator<Map<String, Object>> comparator;
//        if (sortField.equals("deliveryDate")) {
//            comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(String.valueOf(m.get("deliveryDate"))));
//        } else {
//            comparator = java.util.Comparator.comparing(m -> java.time.LocalDate.parse(String.valueOf(m.get("orderDate"))));
//        }
//        if (sortDirection.equals("desc")) {
//            comparator = comparator.reversed();
//        }
//        filtered = filtered.stream().sorted(comparator).toList();
//
//        int total = filtered.size();
//        int fromIdx = Math.min(pageIndex * pageSize, total);
//        int toIdx = Math.min(fromIdx + pageSize, total);
//        java.util.List<Map<String, Object>> pageContent = filtered.subList(fromIdx, toIdx);
//
//        org.ever._4ever_be_gw.common.dto.PageDto pageDto = org.ever._4ever_be_gw.common.dto.PageDto.builder()
//                .number(pageIndex)
//                .size(pageSize)
//                .totalElements(total)
//                .totalPages(pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize))
//                .hasNext(pageIndex + 1 < (pageSize == 0 ? 0 : (int) Math.ceil((double) total / pageSize)))
//                .build();
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("content", pageContent);
//        data.put("page", pageDto);
//
//        return ResponseEntity.ok(ApiResponse.success(data, "발주서 목록 조회에 성공했습니다.", HttpStatus.OK));
//    }
//
//    // ---------------- Purchase Order Detail ----------------
//    @GetMapping("/purchase-orders/{purchaseOrderId}")
//    @Operation(
//            summary = "발주서 상세 조회",
//            description = "발주서 단건 상세 정보를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공"
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "unauthorized", value = "{\n  \"status\": 401,\n  \"success\": false,\n  \"message\": \"인증이 필요합니다.\",\n  \"errors\": { \"code\": 1006 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"해당 데이터를 조회할 권한이 없습니다.\",\n  \"errors\": { \"code\": 1013 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "리소스 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 발주서를 찾을 수 없습니다: poId=11\",\n  \"errors\": { \"code\": 1015 }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "서버 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"요청 처리 중 알 수 없는 오류가 발생했습니다.\",\n  \"errors\": { \"code\": 1014 }\n}"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getPurchaseOrderDetail(
//            @Parameter(description = "발주서 ID", example = "1")
//            @PathVariable("purchaseOrderId") String purchaseOrderId
//    ) {
//        if (purchaseOrderId == null) {
//            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + null);
//        }
//
//        int poIdInt;
//        try {
//            poIdInt = Integer.parseInt(purchaseOrderId);
//            if (poIdInt < 1001 || poIdInt > 1050) {
//                throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + purchaseOrderId);
//            }
//        } catch (NumberFormatException e) {
//            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + purchaseOrderId);
//        }
//
//        // 목업 데이터 생성 (0~49 인덱스, 표시용 데이터는 10개 템플릿을 순환)
//        int idxFull = poIdInt - 1001;
//        int idx = idxFull % 10;
//        String[] suppliers = {"대한철강","한국알루미늄","포스코","효성중공업","현대제철","두산중공업","세아베스틸","KG동부제철","동국제강","티엠씨메탈"};
//        String[] supplierIds = {"501","502","503","504","505","506","507","508","509","510"};
//        String[] supplierCodes = {"SUP001","SUP002","SUP003","SUP004","SUP005","SUP006","SUP007","SUP008","SUP009","SUP010"};
//        String[] orderDates = {"2024-01-18","2024-01-17","2024-01-16","2024-01-15","2024-01-14","2024-01-13","2024-01-12","2024-01-11","2024-01-10","2024-01-09"};
//        String[] deliveryDates = {"2024-01-25","2024-01-24","2024-01-23","2024-01-22","2024-01-21","2024-01-20","2024-01-19","2024-01-18","2024-01-17","2024-01-16"};
//        String[] statusCodes = {"APPROVED","PENDING","REJECTED","APPROVED","PENDING","APPROVED","REJECTED","PENDING","APPROVED","PENDING"};
//
//        PoItemDto item1 = PoItemDto.builder()
//                .itemId(String.valueOf(101 + idx))
//                .itemName("강판")
//                .quantity(500)
//                .uomName("kg")
//                .unitPrice(8_000L)
//                .totalPrice(4_000_000L)
//                .build();
//
//        PoItemDto item2 = PoItemDto.builder()
//                .itemId(String.valueOf(201 + idx))
//                .itemName("알루미늄")
//                .quantity(300)
//                .uomName("kg")
//                .unitPrice(3_333L)
//                .totalPrice(1_000_000L)
//                .build();
//
//        java.time.LocalDate orderDate = java.time.LocalDate.parse(orderDates[idx]);
//        java.time.LocalDate deliveryDate = java.time.LocalDate.parse(deliveryDates[idx]);
//        java.time.Instant createdAt = orderDate.atTime(9, 0).atZone(java.time.ZoneOffset.UTC).toInstant();
//        java.time.Instant updatedAt = createdAt.plusSeconds(300);
//
//        PoDetailDto detail = PoDetailDto.builder()
//                .purchaseOrderId(purchaseOrderId)
//                .purchaseOrderNumber(String.format("PO-2024-%03d", 1 + idxFull))
//                .supplierId(supplierIds[idx])
//                .supplierNumber(supplierCodes[idx])
//                .supplierName(suppliers[idx])
//                .managerPhone("02-1234-5678")
//                .managerEmail("order@steel.co.kr")
//                .orderDate(orderDate)
//                .requestedDeliveryDate(deliveryDate)
//                .statusCode(statusCodes[idx])
//                .items(java.util.List.of(item1, item2))
//                .totalAmount(item1.getTotalPrice() + item2.getTotalPrice())
//                .deliveryAddress("경기도 안산시 단원구 공장로 456")
//                .note("1월 생산용 원자재 주문")
//                .createdAt(createdAt)
//                .updatedAt(updatedAt)
//                .build();
//
//        return ResponseEntity.ok(ApiResponse.success(detail, "발주서 상세 정보 조회에 성공했습니다.", HttpStatus.OK));
//    }
//
//
//    @PostMapping("/supplier")
//    @Operation(
//            summary = "공급사 등록",
//            description = "신규 공급사를 등록하고 공급사의 담당자의 계정 정보를 생성합니다."
//    )
//    public Mono<ResponseEntity<ApiResponse<SupplierCreateRequestDto>>> createVendor(
////            @RequestHeader(value = "Authorization", required = false) String authorization,
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(value = "{\n  \"supplierInfo\": {\n    \"supplierName\": \"대한철강\",\n    \"supplierEmail\": \"contact@koreasteel.com\",\n    \"supplierBaseAddress\": \"서울시 강남구 테헤란로 123\",\n    \"supplierDetailAddress\": \"B동 2층\",\n    \"category\": \"원자재\",\n    \"deliveryLeadTime\": 3\n  },\n  \"managerInfo\": {\n    \"managerName\": \"홍길동\",\n    \"managerPhone\": \"02-1234-5678\",\n    \"managerEmail\": \"contact@koreasteel.com\"\n  },\n  \"materialList\": [\n    { \"materialName\": \"철강재\", \"uomCode\": \"KG\", \"unitPrice\": 1500 },\n    { \"materialName\": \"스테인리스\", \"uomCode\": \"KG\", \"unitPrice\": 2500 },\n    { \"materialName\": \"알루미늄\", \"uomCode\": \"KG\", \"unitPrice\": 2200 }\n  ]\n}"))
//            )
//            @RequestBody SupplierCreateRequestDto requestDto
//    ) {
//        return mmService.createSupplier(requestDto)
//                .map(response -> ResponseEntity
//                        .status(HttpStatus.CREATED)
//                        .body(ApiResponse.success(
//                                response,
//                                "공급사 등록 및 담당자 계정이 생성되었습니다.",
//                                HttpStatus.CREATED
//                        )))
//                .onErrorResume(error -> {
//                    ApiResponse<SupplierCreateRequestDto> fail = ApiResponse.fail(
//                            "공급사 등록 및 담당자 계정 생성 중 오류가 발생했습니다.",
//                            HttpStatus.INTERNAL_SERVER_ERROR,
//                            error.getMessage()
//                    );
//                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(fail));
//                });
//    }
//
//    @GetMapping("/supplier")
//    @Operation(
//            summary = "공급업체 목록 조회",
//            description = "공급업체 목록을 조건에 따라 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 목록을 조회했습니다.\",\n  \"data\": {\n    \"content\": [\n      {\n        \"supplierInfo\": {\n          \"supplierName\": \"한국철강\",\n          \"supplierEmail\": \"contact@koreasteel.com\",\n          \"supplierBaseAddress\": \"서울특별시 샘플로 10\",\n          \"supplierDetailAddress\": null,\n          \"category\": \"원자재\",\n          \"deliveryLeadTime\": 3\n        },\n        \"managerInfo\": {\n          \"managerName\": \"담당자1\",\n          \"managerPhone\": \"02-1234-5678\",\n          \"managerEmail\": \"contact@koreasteel.com\"\n        }\n      }\n    ],\n    \"page\": {\n      \"number\": 0,\n      \"size\": 10,\n      \"totalElements\": 50,\n      \"totalPages\": 5,\n      \"hasNext\": true\n    }\n  }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "unauthorized", value = "{\n  \"status\": 401,\n  \"success\": false,\n  \"message\": \"인증이 필요합니다.\"\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"공급업체 조회 권한이 없습니다.\"\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "검증 실패",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 파라미터 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"status\", \"reason\": \"ALLOWED_VALUES: ACTIVE, INACTIVE, ALL\" },\n    { \"field\": \"page\", \"reason\": \"MIN_0\" },\n    { \"field\": \"size\", \"reason\": \"MAX_200\" }\n  ]\n}"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getVendors(
//            @Parameter(description = "상태 필터: ACTIVE, INACTIVE, ALL(전체)", example = "ALL")
//            @RequestParam(name = "status", required = false) String status,
//            @Parameter(description = "카테고리 필터: MATERIAL, PARTS, ETC, ALL", example = "ALL")
//            @RequestParam(name = "category", required = false) String category,
//            @Parameter(description = "페이지 번호(0-base)", example = "0")
//            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
//            @Parameter(description = "페이지 크기(최대 200)", example = "10")
//            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size
//    ) {
//        // 검증
//        List<Map<String, String>> errors = new java.util.ArrayList<>();
//        if (status != null) {
//            String st = status.toUpperCase(Locale.ROOT);
//            if (!"ALL".equals(st)) {
//                var allowed = java.util.Set.of("ACTIVE", "INACTIVE");
//                if (!allowed.contains(st)) {
//                    errors.add(Map.of("field", "status", "reason", "ALLOWED_VALUES: ACTIVE, INACTIVE, ALL"));
//                }
//            }
//        }
//        if (page != null && page < 0) {
//            errors.add(Map.of("field", "page", "reason", "MIN_0"));
//        }
//        if (size != null && size > 200) {
//            errors.add(Map.of("field", "size", "reason", "MAX_200"));
//        }
//        if (!errors.isEmpty()) {
//            throw new ValidationException(ErrorCode.VALIDATION_FAILED, errors);
//        }
//
//        // 403 모킹 트리거: category=금지
//        if ("금지".equals(category)) {
//            throw new BusinessException(ErrorCode.VENDOR_FORBIDDEN);
//        }
//
//        java.util.List<org.ever._4ever_be_gw.scm.mm.dto.MmSupplierListResponseDto> allVendors = new java.util.ArrayList<>();
//        String[] names = {"한국철강","대한전자부품","글로벌화학","한빛소재","스마트로지스틱스","태성테크","광명산업","한성전자","그린케미칼","아주금속"};
//        String[] categories = {"MATERIAL","PARTS","MATERIAL","PARTS","ETC","PARTS","MATERIAL","PARTS","MATERIAL","MATERIAL"};
//        int[] deliveryLeadDays = {3,1,5,2,0,7,6,2,9,10};
//        String[] phones = {"02-1234-5678","031-987-6543","051-555-0123","02-3456-7890","02-9999-1111","02-7777-8888","031-3333-4444","02-2222-1111","051-777-0000","032-101-2020"};
//        String[] emails = {"contact@koreasteel.com","sales@dahanelec.com","info@globalchem.co.kr","info@hanbits.com","service@smartlogistics.kr","sales@taesung.com","contact@kwangmyung.co.kr","info@hanseong.com","sales@greenchem.co.kr","contact@ajumetal.co.kr"};
//        String[] statusCodeArr = {"ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE"};
//
//        // 상태/카테고리 필터를 적용하여 DTO 생성
//        for (int i = 0; i < 50; i++) {
//            int idx = i % 10;
//            String st = statusCodeArr[idx];
//            String cat = categories[idx];
//            boolean statusOk = true;
//            if (status != null) {
//                String expectedStatus = status.toUpperCase(Locale.ROOT);
//                statusOk = "ALL".equals(expectedStatus) || expectedStatus.equals(st);
//            }
//            String catParam = (category == null) ? null : category.trim();
//            boolean categoryOk = (catParam == null || catParam.isBlank() || catParam.equalsIgnoreCase("ALL"))
//                    || catParam.equalsIgnoreCase(cat);
//            if (!statusOk || !categoryOk) continue;
//
//            var dto = new org.ever._4ever_be_gw.scm.mm.dto.MmSupplierListResponseDto();
//            dto.setStatusCode(st);
//            var sInfo = new org.ever._4ever_be_gw.scm.mm.dto.MmSupplierListResponseDto.SupplierInfo();
//            sInfo.setSupplierId(String.valueOf(1 + i));
//            sInfo.setSupplierName(names[idx]);
//            sInfo.setSupplierCode(String.format("SUP%03d", idx + 1));
//            sInfo.setSupplierEmail(emails[idx]);
//            sInfo.setSupplierPhone(phones[idx]);
//            sInfo.setSupplierBaseAddress("서울특별시 샘플로 10");
//            sInfo.setSupplierDetailAddress(null);
//            sInfo.setSupplierStatus(st);
//            sInfo.setCategory(categories[idx]);
//            sInfo.setDeliveryLeadTime(deliveryLeadDays[idx]);
//            dto.setSupplierInfo(sInfo);
//            allVendors.add(dto);
//        }
//
//        int total = allVendors.size();
//        int pageIndex = (page == null || page < 0) ? 0 : page;
//        int s = (size == null || size < 1) ? 10 : size;
//        int fromIdx = Math.min(pageIndex * s, total);
//        int toIdx = Math.min(fromIdx + s, total);
//        java.util.List<org.ever._4ever_be_gw.scm.mm.dto.MmSupplierListResponseDto> content = allVendors.subList(fromIdx, toIdx);
//
//        org.ever._4ever_be_gw.common.dto.PageDto pageDto = org.ever._4ever_be_gw.common.dto.PageDto.builder()
//                .number(pageIndex)
//                .size(s)
//                .totalElements(total)
//                .totalPages(s == 0 ? 0 : (int) Math.ceil((double) total / s))
//                .hasNext(pageIndex + 1 < (s == 0 ? 0 : (int) Math.ceil((double) total / s)))
//                .build();
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("content", content);
//        data.put("page", pageDto);
//
//        return ResponseEntity.ok(ApiResponse.<Object>success(
//                data, "공급업체 목록을 조회했습니다.", HttpStatus.OK
//        ));
//    }
//
//    @GetMapping("/supplier/{supplierId}")
//    @Operation(
//            summary = "공급업체 상세 조회",
//            description = "공급업체 상세 정보를 조회합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "성공",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 상세 정보를 조회했습니다.\",\n  \"data\": {\n    \"supplierInfo\": {\n      \"supplierName\": \"한국철강\",\n      \"supplierEmail\": \"contact@koreasteel.com\",\n      \"supplierBaseAddress\": \"서울특별시 샘플로 10\",\n      \"supplierDetailAddress\": null,\n      \"category\": \"원자재\",\n      \"deliveryLeadTime\": 3\n    },\n    \"managerInfo\": {\n      \"managerName\": \"담당자1\",\n      \"managerPhone\": \"02-1234-5678\",\n      \"managerEmail\": \"contact@koreasteel.com\"\n    }\n  }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "unauthorized", value = "{\n  \"status\": 401,\n  \"success\": false,\n  \"message\": \"인증이 필요합니다.\"\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{\n  \"status\": 403,\n  \"success\": false,\n  \"message\": \"공급업체 조회 권한이 없습니다.\"\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "리소스 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{\n  \"status\": 404,\n  \"success\": false,\n  \"message\": \"해당 공급업체를 찾을 수 없습니다.\"\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "서버 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{\n  \"status\": 500,\n  \"success\": false,\n  \"message\": \"공급업체 조회 처리 중 오류가 발생했습니다.\"\n}"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> getVendorDetail(
//            @Parameter(description = "공급업체 ID", example = "1")
//            @PathVariable("supplierId") String supplierId
//    ) {
//        // 모킹 트리거들
//        if (supplierId != null && "403001".equals(supplierId)) {
//            throw new BusinessException(ErrorCode.VENDOR_FORBIDDEN);
//        }
//        if (supplierId != null && "500001".equals(supplierId)) {
//            throw new BusinessException(ErrorCode.VENDOR_PROCESSING_ERROR);
//        }
//
//        int supplierIdInt;
//        try {
//            supplierIdInt = Integer.parseInt(supplierId);
//            if (supplierIdInt < 1 || supplierIdInt > 50) {
//                throw new BusinessException(ErrorCode.VENDOR_NOT_FOUND);
//            }
//        } catch (NumberFormatException e) {
//            throw new BusinessException(ErrorCode.VENDOR_NOT_FOUND);
//        }
//
//        int idx = (supplierIdInt - 1) % 10;
//        String[] names = {"한국철강","대한전자부품","글로벌화학","한빛소재","스마트로지스틱스","태성테크","광명산업","한성전자","그린케미칼","아주금속"};
//        String[] categories = {"MATERIAL","PARTS","MATERIAL","PARTS","ETC","PARTS","MATERIAL","PARTS","MATERIAL","MATERIAL"};
//        int[] leadDays = {3,1,5,2,0,7,6,2,9,10};
//        String[] phones = {"02-1234-5678","031-987-6543","051-555-0123","02-3456-7890","02-9999-1111","02-7777-8888","031-3333-4444","02-2222-1111","051-777-0000","032-101-2020"};
//        String[] emails = {"contact@koreasteel.com","sales@dahanelec.com","info@globalchem.co.kr","info@hanbits.com","service@smartlogistics.kr","sales@taesung.com","contact@kwangmyung.co.kr","info@hanseong.com","sales@greenchem.co.kr","contact@ajumetal.co.kr"};
//        String[] statusCodeArr = {"ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE","ACTIVE","INACTIVE","ACTIVE"};
//
//        var dto = new org.ever._4ever_be_gw.scm.mm.dto.MmSupplierDetailResponseDto();
//        String st = statusCodeArr[idx];
//        dto.setStatusCode(st);
//        var sInfo = new org.ever._4ever_be_gw.scm.mm.dto.MmSupplierDetailResponseDto.SupplierInfo();
//        sInfo.setSupplierId(String.valueOf(supplierId));
//        sInfo.setSupplierCode(String.format("SUP%03d", idx + 1));
//        sInfo.setSupplierName(names[idx]);
//        sInfo.setSupplierEmail(emails[idx]);
//        sInfo.setSupplierPhone(phones[idx]);
//        sInfo.setSupplierBaseAddress("서울특별시 샘플로 10");
//        sInfo.setSupplierDetailAddress(null);
//        sInfo.setSupplierStatus(st);
//        sInfo.setCategory(categories[idx]);
//        sInfo.setDeliveryLeadTime(leadDays[idx]);
//        dto.setSupplierInfo(sInfo);
//
//        var mInfo = new org.ever._4ever_be_gw.scm.mm.dto.MmSupplierDetailResponseDto.ManagerInfo();
//        mInfo.setManagerName("담당자" + (idx + 1));
//        mInfo.setManagerPhone(phones[idx]);
//        mInfo.setManagerEmail(emails[idx]);
//        dto.setManagerInfo(mInfo);
//
//        return ResponseEntity.ok(ApiResponse.<Object>success(
//                dto, "공급업체 상세 정보를 조회했습니다.", HttpStatus.OK
//        ));
//    }
//
//    @PatchMapping("/supplier/{supplierId}")
//    @Operation(
//            summary = "공급업체 정보 수정",
//            description = "공급업체 기본 정보를 수정합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "수정 성공",
//            content = @Content(mediaType = "application/json",
//                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 정보를 수정했습니다.\",\n  \"data\": {\n    \"supplierId\": 1,\n    \"vendorCode\": \"V-001\",\n    \"companyName\": \"대한철강\",\n    \"category\": \"원자재\",\n    \"address\": \"서울특별시 강남구 테헤란로 123 B동 2층\",\n    \"leadTimeDays\": 3,\n    \"materialList\": [\"철강재\", \"스테인리스\"],\n    \"statusCode\": \"ACTIVE\",\n    \"managerName\": \"홍길동\",\n    \"managerPosition\": \"영업팀장\",\n    \"managerPhone\": \"010-1234-5678\",\n    \"managerEmail\": \"contact@koreasteel.com\",\n    \"createdAt\": \"2025-10-07T00:00:00Z\",\n    \"updatedAt\": \"2025-10-13T12:00:00Z\"\n  }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"공급업체 수정 권한이 없습니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "공급업체 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"수정할 공급업체를 찾을 수 없습니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "검증 실패",
//            content = @Content(mediaType = "application/json",
//                    examples = @ExampleObject(name = "validation_failed", value = "{\n  \"status\": 422,\n  \"success\": false,\n  \"message\": \"요청 본문 검증에 실패했습니다.\",\n  \"errors\": [\n    { \"field\": \"managerName\", \"reason\": \"FIELD_NOT_EDITABLE_BY_ADMIN\" },\n    { \"field\": \"managerPhone\", \"reason\": \"FIELD_NOT_EDITABLE_BY_ADMIN\" }\n  ]\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "처리 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"공급업체 정보 수정 처리 중 오류가 발생했습니다.\" }"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> updateVendor(
//            @PathVariable("supplierId") String supplierId,
////            @RequestHeader(value = "Authorization", required = false) String authorization,
//            @RequestBody(required = false) MmSupplierUpdateRequestDto request
//    ) {
////        if (authorization == null || authorization.isBlank()) {
////            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
////        }
////
////        String token = authorization.trim().toUpperCase(Locale.ROOT);
////        if (!token.contains("PR_APPROVER") && !token.contains("PURCHASING_MANAGER") && !token.contains("ADMIN")) {
////            throw new BusinessException(ErrorCode.VENDOR_UPDATE_FORBIDDEN);
////        }
////        if (token.contains("ERROR")) {
////            throw new BusinessException(ErrorCode.VENDOR_UPDATE_PROCESSING_ERROR);
////        }
//
//        java.util.List<Map<String, String>> errors = new java.util.ArrayList<>();
//        if (request != null) {
//            var mInfo = request.getManagerInfo();
//            if (mInfo != null) {
//                if (mInfo.getManagerName() != null) {
//                    errors.add(Map.of("field", "managerName", "reason", "FIELD_NOT_EDITABLE_BY_ADMIN"));
//                }
//                if (mInfo.getManagerPhone() != null) {
//                    errors.add(Map.of("field", "managerPhone", "reason", "FIELD_NOT_EDITABLE_BY_ADMIN"));
//                }
//                if (mInfo.getManagerEmail() != null) {
//                    errors.add(Map.of("field", "managerEmail", "reason", "FIELD_NOT_EDITABLE_BY_ADMIN"));
//                }
//            }
//            if (request.getStatusCode() != null && !java.util.Set.of("ACTIVE", "INACTIVE").contains(request.getStatusCode())) {
//                errors.add(Map.of("field", "statusCode", "reason", "ALLOWED_VALUES: ACTIVE, INACTIVE"));
//            }
//            var sInfo = request.getSupplierInfo();
//            if (sInfo != null && sInfo.getDeliveryLeadTime() != null && sInfo.getDeliveryLeadTime() < 0) {
//                errors.add(Map.of("field", "deliveryLeadTime", "reason", "MUST_BE_POSITIVE_OR_ZERO"));
//            }
//        }
//
//
//        if (supplierId == null) {
//            throw new BusinessException(ErrorCode.VENDOR_UPDATE_NOT_FOUND);
//        }
//
//        int supplierIdInt;
//        try {
//            supplierIdInt = Integer.parseInt(supplierId);
//            if (supplierIdInt < 1 || supplierIdInt > 200) {
//                throw new BusinessException(ErrorCode.VENDOR_UPDATE_NOT_FOUND);
//            }
//        } catch (NumberFormatException e) {
//            throw new BusinessException(ErrorCode.VENDOR_UPDATE_NOT_FOUND);
//        }
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("vendorId", supplierId);
//        data.put("vendorCode", "V-001");
//        var sInfo = request != null ? request.getSupplierInfo() : null;
//        String companyName = (sInfo != null && sInfo.getSupplierName() != null) ? sInfo.getSupplierName() : "대한철강";
//        data.put("companyName", companyName);
//        data.put("category", (sInfo != null && sInfo.getCategory() != null) ? sInfo.getCategory() : "원자재");
//        String addrBase = (sInfo != null && sInfo.getSupplierBaseAddress() != null) ? sInfo.getSupplierBaseAddress() : "서울특별시 강남구 테헤란로 123";
//        String addrDetail = (sInfo != null && sInfo.getSupplierDetailAddress() != null) ? sInfo.getSupplierDetailAddress() : null;
//        String composedAddr = addrBase + (addrDetail != null && !addrDetail.isBlank() ? (" " + addrDetail) : "");
//        data.put("address", composedAddr);
//        Integer lead = (sInfo != null) ? sInfo.getDeliveryLeadTime() : null;
//        data.put("leadTimeDays", lead != null ? lead : 3);
//        if (request != null && request.getMaterialList() != null) {
//            java.util.List<String> names = request.getMaterialList().stream()
//                    .map(mi -> mi != null ? String.valueOf(mi.getMaterialName()) : null)
//                    .filter(java.util.Objects::nonNull)
//                    .toList();
//            data.put("materialList", names);
//        } else {
//            data.put("materialList", java.util.List.of("철강재", "스테인리스"));
//        }
//        data.put("statusCode", request != null && request.getStatusCode() != null ? request.getStatusCode() : "ACTIVE");
//        data.put("managerName", "홍길동");
//        data.put("managerPosition", "영업팀장");
//        data.put("managerPhone", "010-1234-5678");
//        data.put("managerEmail", "contact@koreasteel.com");
//        data.put("createdAt", java.time.Instant.parse("2025-10-07T00:00:00Z"));
//        data.put("updatedAt", java.time.Instant.parse("2025-10-13T12:00:00Z"));
//
//        return ResponseEntity.ok(ApiResponse.success(data, "공급업체 정보를 수정했습니다.", HttpStatus.OK));
//    }
//
//    @PostMapping("/purchase-orders/{purchaseOrderId}/approve")
//    @Operation(
//            summary = "발주서 승인",
//            description = "발주서의 상태를 대기 → 승인으로 변경합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "승인 성공",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"발주서를 승인했습니다.\",\n  \"data\": {\n    \"id\": 1001,\n    \"poNumber\": \"PO-2024-001\",\n    \"statusCode\": \"APPROVED\",\n    \"approvedAt\": \"2025-10-07T09:15:00Z\",\n    \"approvedBy\": \"홍길동\"\n  }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "승인 권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"발주서 승인 권한이 없습니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "발주서 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 발주서를 찾을 수 없습니다: poId=1001\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "409",
//                            description = "승인 불가 상태",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "conflict", value = "{ \"status\": 409, \"success\": false, \"message\": \"현재 상태에서는 승인할 수 없습니다. (status=PENDING만 승인 가능)\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "처리 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"승인 처리 중 오류가 발생했습니다.\" }"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> approvePurchaseOrder(
//            @Parameter(description = "발주서 ID", example = "1001")
//            @PathVariable("purchaseOrderId") String poId
//    ) {
//        // 목업에서는 인증 처리 생략 (항상 성공)
//
//        // 유효한 발주서 ID 범위 (목업 데이터 기준: 1001~1050)
//        if (poId == null) {
//            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + poId);
//        }
//
//        int poIdInt;
//        try {
//            poIdInt = Integer.parseInt(poId);
//            if (poIdInt < 1001 || poIdInt > 1050) {
//                throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + poId);
//            }
//        } catch (NumberFormatException e) {
//            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + poId);
//        }
//
//        // 현재 상태 확인: PENDING만 승인 가능
//        // 규칙: (poId-1001) % 5 in {1,4} → PENDING (목록 생성 규칙과 일치)
//        int mod = (poIdInt - 1001) % 5;
//        boolean isPending = (mod == 1 || mod == 4);
//        if (!isPending) {
//            throw new BusinessException(ErrorCode.PURCHASE_ORDER_INVALID_TRANSITION);
//        }
//
//        int poIdValue = Integer.parseInt(poId);
//        int idxFull = poIdValue - 1001;
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("id", poId);
//        data.put("poNumber", String.format("PO-2024-%03d", idxFull + 1));
//        data.put("statusCode", "APPROVED");
//        // statusLabel 제거
//        data.put("approvedAt", java.time.Instant.parse("2025-10-07T09:15:00Z"));
//        data.put("approvedBy", "홍길동");
//
//        return ResponseEntity.ok(ApiResponse.success(data, "발주서를 승인했습니다.", HttpStatus.OK));
//    }
//
//    @PostMapping("/purchase-orders/{purchaseOrderId}/reject")
//    @Operation(
//            summary = "발주서 반려",
//            description = "발주서의 상태를 반려로 변경합니다.",
//            responses = {
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "200",
//                            description = "반려 성공",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"발주서를 반려했습니다.\",\n  \"data\": {\n    \"id\": 1001,\n    \"poNumber\": \"PO-2024-001\",\n    \"statusCode\": \"REJECTED\",\n    \"rejectedAt\": \"2025-10-14T10:00:00Z\",\n    \"rejectedBy\": \"홍길동\",\n    \"reason\": \"납기일 미확정\"\n  }\n}"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "401",
//                            description = "인증 필요",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "403",
//                            description = "반려 권한 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"발주서 반려 권한이 없습니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "404",
//                            description = "발주서 없음",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 발주서를 찾을 수 없습니다: poId=999999\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "422",
//                            description = "본문 검증 실패",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "reason_required", value = "{ \"status\": 422, \"success\": false, \"message\": \"반려 사유를 입력해야 합니다.\" }"))
//                    ),
//                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
//                            responseCode = "500",
//                            description = "처리 오류",
//                            content = @Content(mediaType = "application/json",
//                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"발주서 반려 처리 중 오류가 발생했습니다.\" }"))
//                    )
//            }
//    )
//    public ResponseEntity<ApiResponse<Object>> rejectPurchaseOrder(
//            @Parameter(description = "발주서 ID", example = "1001")
//            @PathVariable("purchaseOrderId") String poId,
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    required = true,
//                    content = @Content(mediaType = "application/json",
//                            examples = @ExampleObject(value = "{ \"reason\": \"납기일 미확정\" }"))
//            )
//            @RequestBody MmPurchaseOrderRejectRequestDto request
//    ) {
//
//        if (poId == null) {
//            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + poId);
//        }
//
//        int poIdInt;
//        try {
//            poIdInt = Integer.parseInt(poId);
//            if (poIdInt < 1001 || poIdInt > 1050) {
//                throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + poId);
//            }
//        } catch (NumberFormatException e) {
//            throw new BusinessException(ErrorCode.PURCHASE_ORDER_NOT_FOUND, "poId=" + poId);
//        }
//
//        if (request == null || request.getReason() == null || request.getReason().isBlank()) {
//            throw new BusinessException(ErrorCode.PURCHASE_ORDER_REJECTION_REASON_REQUIRED);
//        }
//
//        int idxFull = poIdInt - 1001;
//
//        Map<String, Object> data = new LinkedHashMap<>();
//        data.put("id", poId);
//        data.put("poNumber", String.format("PO-2024-%03d", idxFull + 1));
//        data.put("statusCode", "REJECTED");
//        // statusLabel 제거
//        data.put("rejectedAt", java.time.Instant.parse("2025-10-14T10:00:00Z"));
//        data.put("rejectedBy", "홍길동");
//        data.put("reason", request.getReason());
//
//        return ResponseEntity.ok(ApiResponse.success(data, "발주서를 반려했습니다.", HttpStatus.OK));
//    }
//
//    @GetMapping("/status-codes")
//    @Operation(
//            summary = "구매 상태 드롭다운 ",
//            description = "구매 상태 드롭다운 목록(APPROVAL, PENDING, REJECTED)을 반환합니다."
//    )
//    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getStatusCodes() {
//        List<Map<String, Object>> statusCodes = new ArrayList<>();
//
//        statusCodes.add(Map.of("id", 1, "code", "APPROVAL", "name", "승인됨"));
//        statusCodes.add(Map.of("id", 2, "code", "PENDING", "name", "대기 중"));
//        statusCodes.add(Map.of("id", 3, "code", "REJECTED", "name", "반려됨"));
//
//        return ResponseEntity.ok(ApiResponse.success(null, "구매요청 상태 코드 조회 성공",HttpStatus.OK));
//    }
//
//
////    @PostMapping("/vendors/{supplierId}/account")
////    @Operation(
////            summary = "공급업체 계정 생성",
////            description = "공급업체 계정을 생성하고 임시 비밀번호가 포함된 초대 이메일을 발송합니다.",
////            responses = {
////                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
////                            responseCode = "200",
////                            description = "성공",
////                            content = @Content(mediaType = "application/json",
////                                    examples = @ExampleObject(name = "success", value = "{\n  \"status\": 200,\n  \"success\": true,\n  \"message\": \"공급업체 계정이 생성되고 초대 이메일이 발송되었습니다.\",\n  \"data\": {\n    \"supplierId\": 101,\n    \"vendorCode\": \"SUP-2025-0001\",\n    \"email\": \"contact@everp.com\",\n    \"tempPassword\": \"Abc12345!\",\n    \"invitedAt\": \"2025-10-13T10:05:00Z\"\n  }\n}"))
////                    ),
////                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
////                            responseCode = "401",
////                            description = "인증 필요",
////                            content = @Content(mediaType = "application/json",
////                                    examples = @ExampleObject(name = "unauthorized", value = "{ \"status\": 401, \"success\": false, \"message\": \"인증이 필요합니다.\" }"))
////                    ),
////                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
////                            responseCode = "403",
////                            description = "권한 없음",
////                            content = @Content(mediaType = "application/json",
////                                    examples = @ExampleObject(name = "forbidden", value = "{ \"status\": 403, \"success\": false, \"message\": \"계정 생성 권한이 없습니다.\" }"))
////                    ),
////                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
////                            responseCode = "404",
////                            description = "공급업체 없음",
////                            content = @Content(mediaType = "application/json",
////                                    examples = @ExampleObject(name = "not_found", value = "{ \"status\": 404, \"success\": false, \"message\": \"해당 공급업체를 찾을 수 없습니다.\" }"))
////                    ),
////                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
////                            responseCode = "409",
////                            description = "이미 계정 생성됨",
////                            content = @Content(mediaType = "application/json",
////                                    examples = @ExampleObject(name = "conflict", value = "{ \"status\": 409, \"success\": false, \"message\": \"이미 계정이 발급된 공급업체입니다.\" }"))
////                    ),
////                    @io.swagger.v3.oas.annotations.responses.ApiResponse(
////                            responseCode = "500",
////                            description = "처리 오류",
////                            content = @Content(mediaType = "application/json",
////                                    examples = @ExampleObject(name = "server_error", value = "{ \"status\": 500, \"success\": false, \"message\": \"초대 이메일 발송 중 오류가 발생했습니다.\" }"))
////                    )
////            }
////    )
////    public ResponseEntity<ApiResponse<Object>> inviteVendorAccount(
////            @PathVariable("supplierId") Long supplierId,
////            @RequestHeader(value = "Authorization", required = false) String authorization
////    ) {
////        if (authorization == null || authorization.isBlank()) {
////            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
////        }
////
////        String token = authorization.trim().toUpperCase(Locale.ROOT);
////        if (!token.contains("PR_APPROVER") && !token.contains("PURCHASING_MANAGER") && !token.contains("ADMIN")) {
////            throw new BusinessException(ErrorCode.VENDOR_ACCOUNT_FORBIDDEN);
////        }
////        if (token.contains("ERROR")) {
////            throw new BusinessException(ErrorCode.VENDOR_ACCOUNT_PROCESSING_ERROR);
////        }
////
////        if (supplierId.equals(999L)) {
////            throw new BusinessException(ErrorCode.VENDOR_ACCOUNT_ALREADY_EXISTS);
////        }
////        if (supplierId == null || supplierId < 1 || supplierId > 200) {
////            throw new BusinessException(ErrorCode.VENDOR_NOT_FOUND);
////        }
////
////        String baseEmail = "contact@koreasteel.com";
////        String accountEmail = baseEmail.substring(0, baseEmail.indexOf('@')) + "@everp.com";
////
////        Map<String, Object> data = new LinkedHashMap<>();
////        data.put("supplierId", supplierId);
////        data.put("vendorCode", "SUP-2025-0001");
////        data.put("managerEmail", accountEmail);
////        data.put("tempPassword", "Abc12345!");
////        data.put("invitedAt", java.time.Instant.parse("2025-10-13T10:05:00Z"));
////
////        return ResponseEntity.ok(ApiResponse.success(data, "공급업체 계정이 생성되고 초대 이메일이 발송되었습니다.", HttpStatus.OK));
////    }
//}
