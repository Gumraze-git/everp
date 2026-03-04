package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.hrm.UpdateProfileRequestDto;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "프로필", description = "프로필 API")
@RestController
@RequestMapping("/business/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final WebClientProvider webClientProvider;

    @GetMapping
    public ResponseEntity<Object> getEmployeeProfile(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {
        String userId = user.getUserId();
        String userType = user.getUserType();
        var client = webClientProvider.getWebClient(ApiClientKey.BUSINESS);
        var scmClient = webClientProvider.getWebClient(ApiClientKey.SCM_PP);

        Object result;

        switch (userType.toLowerCase()) {
            case "customer":
                // 1번: 고객사 조회
                result = client.get()
                        .uri("/hrm/customers/profile/{customerUserId}", userId)
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();
                break;

            case "supplier":
                // 2번: 공급사 조회
                result = scmClient.get()
                        .uri("/api/scm-pp/mm/users/supplier/{userId}/profile", userId)
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();
                break;

            case "internal":
            default:
                // 3번: 기존 내부 직원 조회
                result = client.get()
                        .uri("/hrm/employees/profile/{internelUserId}", userId)
                        .retrieve()
                        .bodyToMono(Object.class)
                        .block();
                break;
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/attendance-records")
    public ResponseEntity<Object> getEmployeeAttendanceRecords(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {

        String internelUserId = user.getUserId();

        var client = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        Object result = client.get()
                .uri("/hrm/employees/attendance-records/{internelUserId}", internelUserId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/today-attendance")
    public ResponseEntity<Object> getTodayAttendance(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {
        String internelUserId = user.getUserId();

        var client = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        Object result = client.get()
                .uri("/hrm/employees/today-attendance/{internelUserId}", internelUserId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/trainings/in-progress")
    public ResponseEntity<Object> getInProgressTrainings(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {
        String internelUserId = user.getUserId();

        var client = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        Object result = client.get()
                .uri("/hrm/employees/trainings/in-progress/{internelUserId}", internelUserId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/trainings/available")
    public ResponseEntity<Object> getAvailableTrainings(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {
        String internelUserId = user.getUserId();

        var client = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        Object result = client.get()
                .uri("/hrm/employees/trainings/available/{internelUserId}", internelUserId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/trainings/completed")
    public ResponseEntity<Object> getCompletedTrainings(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {

        String internelUserId = user.getUserId();
        var client = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        Object result = client.get()
                .uri("/hrm/employees/trainings/completed/{internelUserId}", internelUserId)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    @PostMapping("trainings/request")
    public ResponseEntity<Object> requestTraining(
            @AuthenticationPrincipal EverUserPrincipal user,
            @RequestParam String trainingId
    ) {
        String internelUserId = user.getUserId();

        var client = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        // WebClient 요청에서 쿼리 파라미터를 넘기는 부분
        Object result = client.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/hrm/internelUser/program")
                        .queryParam("programId", trainingId)
                        .queryParam("internelUserId", internelUserId)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }

    @PostMapping("employees/profile/update")
    public ResponseEntity<Object> updateProfileThroughWebClient(
            @AuthenticationPrincipal EverUserPrincipal user,
            @RequestBody UpdateProfileRequestDto requestDto
    ) {
        String internelUserId = user.getUserId();

        var client = webClientProvider.getWebClient(ApiClientKey.BUSINESS);

        Object result = client.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/hrm/employees/profile/{internelUserId}")
                        .build(internelUserId))  // PathVariable의 값을 전달
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

        return ResponseEntity.ok(result);
    }


}
