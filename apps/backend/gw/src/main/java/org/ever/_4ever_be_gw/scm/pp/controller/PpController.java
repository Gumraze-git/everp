package org.ever._4ever_be_gw.scm.pp.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.ever._4ever_be_gw.scm.pp.dto.BomCreateRequestDto;
import org.ever._4ever_be_gw.scm.pp.dto.MrpRunConvertRequestDto;
import org.ever._4ever_be_gw.scm.pp.dto.QuotationConfirmRequestDto;
import org.ever._4ever_be_gw.scm.pp.dto.QuotationSimulateRequestDto;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "생산관리(PP)", description = "생산 관리 API")
@RestController
@RequestMapping("/scm-pp/pp")
@RequiredArgsConstructor
public class PpController {

    private final WebClientProvider webClientProvider;

    // BOM 생성
    @PostMapping("/boms")
    public ResponseEntity<Object> createBom(@RequestBody BomCreateRequestDto requestDto) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                    .post()
                    .uri("/scm-pp/pp/boms")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestDto)
                    .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // BOM 목록 조회
    @GetMapping("/boms")
    public ResponseEntity<Object> getBomList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/boms")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // BOM 상세 조회
    @GetMapping("/boms/{bomId}")
    public ResponseEntity<Object> getBomDetail(@PathVariable String bomId) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/boms/{bomId}", bomId)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // BOM 수정
    @PatchMapping("/boms/{bomId}")
    public ResponseEntity<Object> updateBom(@PathVariable String bomId, @RequestBody BomCreateRequestDto requestDto) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .patch()
                .uri("/scm-pp/pp/boms/{bomId}", bomId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MES 목록 조회
    @GetMapping("/mes")
    public ResponseEntity<Object> getMesList(
            @RequestParam(required = false) String quotationId,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/mes")
                        .queryParam("quotationId", quotationId)
                        .queryParam("status", status)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MES 상태 조회
    @GetMapping("/mes/status/toggle")
    public ResponseEntity<Object> getMesStatusDetail() {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/mes/status/toggle")
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MES 상세 조회
    @GetMapping("/mes/{mesId}")
    public ResponseEntity<Object> getMesDetail(@PathVariable String mesId) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/mes/{mesId}", mesId)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MES 시작
    @PutMapping("/mes/{mesId}/start")
    public ResponseEntity<Object> startMes(
            @PathVariable String mesId,
            @AuthenticationPrincipal EverUserPrincipal principal
    ) {
        String requesterId = principal.getUserId();
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/mes/{mesId}/start")
                        .queryParam("requesterId", requesterId)
                        .build(mesId))
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // 공정 시작
    @PutMapping("/mes/{mesId}/operations/{operationId}/start")
    public ResponseEntity<Object> startOperation(
            @PathVariable String mesId,
            @PathVariable String operationId,
            @AuthenticationPrincipal EverUserPrincipal everUserPrincipal) {

        String managerId = everUserPrincipal.getUserId();

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/mes/{mesId}/operations/{logId}/start")
                        .queryParam("managerId", managerId)
                        .build(mesId, operationId))
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // 공정 완료
    @PutMapping("/mes/{mesId}/operations/{operationId}/complete")
    public ResponseEntity<Object> completeOperation(
            @PathVariable String mesId,
            @PathVariable String operationId) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mes/{mesId}/operations/{logId}/complete", mesId, operationId)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MES 완료
    @PutMapping("/mes/{mesId}/complete")
    public ResponseEntity<Object> completeMes(
            @PathVariable String mesId,
            @AuthenticationPrincipal EverUserPrincipal principal
            ) {
        String requesterId = principal.getUserId();

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/mes/{mesId}/complete")
                        .queryParam("requesterId", requesterId)
                        .build(mesId))
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MRP → MRP_RUN 계획주문 전환
    @PostMapping("/mrp/convert")
    @PreAuthorize("hasAnyAuthority('PP_USER', 'PP_ADMIN', 'ALL_ADMIN')")
    public ResponseEntity<Object> convertToMrpRun(@RequestBody MrpRunConvertRequestDto requestDto) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/pp/mrp/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MRP 계획주문 목록 조회
    @GetMapping("/mrp/runs")
    public ResponseEntity<Object> getMrpRunList(
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(required = false) String quotationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/mrp/runs")
                        .queryParam("status", status)
                        .queryParam("quotationId", quotationId)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MRP 계획주문 승인
    @PutMapping("/mrp/runs/{mrpRunId}/approve")
    public ResponseEntity<Object> approveMrpRun(@PathVariable String mrpRunId) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mrp/runs/{mrpRunId}/approve", mrpRunId)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MRP 계획주문 거부
    @PutMapping("/mrp/runs/{mrpRunId}/reject")
    public ResponseEntity<Object> rejectMrpRun(@PathVariable String mrpRunId) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mrp/runs/{mrpRunId}/reject", mrpRunId)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MRP 계획주문 입고 처리
    @PutMapping("/mrp/runs/{mrpRunId}/receive")
    public ResponseEntity<Object> receiveMrpRun(@PathVariable String mrpRunId) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .put()
                .uri("/scm-pp/pp/mrp/runs/{mrpRunId}/receive", mrpRunId)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // 견적 목록 조회 (그룹핑)
    @GetMapping("/quotations")
    public ResponseEntity<Object> getQuotationList(
            @RequestParam(defaultValue = "ALL") String statusCode,
            @RequestParam(defaultValue = "ALL") String availableStatusCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/quotations")
                        .queryParam("availableStatus", availableStatusCode)
                        .queryParam("statusCode", statusCode)
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // 견적 시뮬레이션
    @PostMapping("/quotations/simulate")
    @PreAuthorize("hasAnyAuthority('PP_USER', 'PP_ADMIN', 'ALL_ADMIN')")
    public ResponseEntity<Object> simulateQuotations(
            @RequestBody QuotationSimulateRequestDto requestDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/quotations/simulate")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MPS 프리뷰 생성
    @PostMapping("/quotations/preview")
    public ResponseEntity<Object> previewMps(@RequestBody List<String> quotationIds) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/pp/quotations/preview")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(quotationIds)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // 견적 확정
    @PostMapping("/quotations/confirm")
    @PreAuthorize("hasAnyAuthority('PP_USER', 'PP_ADMIN', 'ALL_ADMIN')")
    public ResponseEntity<Object> confirmQuotations(@RequestBody QuotationConfirmRequestDto requestDto) {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .post()
                .uri("/scm-pp/pp/quotations/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // 견적 상태 토글
    @GetMapping("/status/toggle")
    public ResponseEntity<Object> getQuotationsStatusToggle() {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/quotations/status/toggle")
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // 견적 상태 토글
    @GetMapping("mrp/available/status/toggle")
    public ResponseEntity<Object> getMrpStatusToggle() {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/quotations/mrp/available/status/toggle")
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }


    // 견적 체크 토글
    @GetMapping("/available/status/toggle")
    public ResponseEntity<Object> getQuotationAvailableStatusToggle() {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/quotations/available/status/toggle")
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MPS 조회 (주차별)
    @GetMapping("/quotations/mps")
    public ResponseEntity<Object> getMps(
            @RequestParam String bomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/quotations/mps")
                        .queryParam("bomId", bomId)
                        .queryParam("startDate", startDate)
                        .queryParam("endDate", endDate)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // 견적 체크 토글
    @GetMapping("/mps/boms/toggle")
    public ResponseEntity<Object> getMpsBomToggle() {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/quotations/boms/toggle")
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    //MRP 견적 목록 조회 토글
    @GetMapping("/mrp/quotations/toggle")
    public ResponseEntity<Object> getMpsQuotationToggle() {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/quotations/mrp/quotations/toggle")
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }


    //MRP 계획주문 상태 조회
    @GetMapping("/mrp/runs/status/toggle")
    public ResponseEntity<Object> getMpsRunsToggle() {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/mrp/runs/status/toggle")
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    //MRP 계획주문 상태 조회
    @GetMapping("/mrp/runs/quotations/toggle")
    public ResponseEntity<Object> getMpsRunsQuotationsToggle() {
        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/mrp/runs/quotations/toggle")
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    // MRP 조회 (자재 조달 계획)
    @GetMapping("/quotations/mrp")
    public ResponseEntity<Object> getMrp(
            @RequestParam(required = false) String bomId,
            @RequestParam(required = false) String quotationId,
            @RequestParam(required = false, defaultValue = "ALL") String availableStatusCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/quotations/mrp")
                        .queryParam("bomId", bomId)
                        .queryParam("quotationId", quotationId)
                        .queryParam("availableStatusCode", availableStatusCode)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    /**
     * Product ID와 이름 맵 조회 (페이징, 외부 API)
     */
    @GetMapping("/products")
    public ResponseEntity<Object> getProductMap() {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/boms/products")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    /**
     * Product 상세 정보 조회 (외부 API)
     */
    @GetMapping("/products/{productId}")
    public ResponseEntity<Object> getProductDetail(@PathVariable String productId) {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri("/scm-pp/pp/boms/products/{productId}", productId)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    /**
     * Product ID와 이름 맵 조회 (페이징, 외부 API)
     */
    @GetMapping("/operations")
    public ResponseEntity<Object> getOperationsMap() {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/boms/operations")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }

    @GetMapping("/statistic")
    @io.swagger.v3.oas.annotations.Operation(
            summary = "PP 통계 조회",
            description = "생산관리의 통계를 반환합니다. 생산중인 품목, 완료된 생산, 완제품 개수 포함"
    )
    public ResponseEntity<Object> getPpStatistic() {

        try {
            ResponseEntity<Object> result = webClientProvider.getWebClient(ApiClientKey.SCM_PP)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/scm-pp/pp/statistic")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(response -> {
                        return response.bodyToMono(String.class)
                                .map(body -> ResponseEntity.status(response.statusCode())
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .body((Object)body));
                    })
                    .block();
            return result;
        } catch (WebClientResponseException ex) {
            return ResponseEntity.status(ex.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(ex.getResponseBodyAsString());
        }
    }
}
