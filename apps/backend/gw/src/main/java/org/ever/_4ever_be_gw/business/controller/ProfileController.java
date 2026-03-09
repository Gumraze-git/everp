package org.ever._4ever_be_gw.business.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.business.dto.hrm.UpdateProfileRequestDto;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.restclient.RestClientProvider;
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

    private final RestClientProvider restClientProvider;

    @GetMapping
    public ResponseEntity<Object> getEmployeeProfile(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {
        String userId = user.getUserId();
        String userType = user.getUserType();
        var client = restClientProvider.getRestClient(ApiClientKey.BUSINESS);
        var scmClient = restClientProvider.getRestClient(ApiClientKey.SCM_PP);

        ResponseEntity<Object> result;

        switch (userType.toLowerCase()) {
            case "customer":
                result = fetchEntity(client.get().uri("/hrm/customer-users/{customerUserId}/profile", userId));
                break;

            case "supplier":
                result = fetchEntity(scmClient.get().uri("/scm-pp/mm/supplier-users/{userId}/profile", userId));
                break;

            case "internal":
            default:
                result = fetchEntity(client.get().uri("/hrm/internal-users/{internalUserId}/profile", userId));
                break;
        }

        return result;
    }

    @GetMapping("/attendance-records")
    public ResponseEntity<Object> getEmployeeAttendanceRecords(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {

        String internalUserId = user.getUserId();

        var client = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

        return fetchEntity(client.get().uri("/hrm/internal-users/{internalUserId}/attendance-history-items", internalUserId));
    }

    @GetMapping("/today-attendance")
    public ResponseEntity<Object> getTodayAttendance(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {
        String internalUserId = user.getUserId();

        var client = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

        return fetchEntity(client.get().uri("/hrm/internal-users/{internalUserId}/today-attendance", internalUserId));
    }

    @GetMapping("/training-items/in-progress")
    public ResponseEntity<Object> getInProgressTrainings(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {
        String internalUserId = user.getUserId();

        var client = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

        return fetchEntity(client.get().uri("/hrm/internal-users/{internalUserId}/in-progress-training-items", internalUserId));
    }

    @GetMapping("/training-items/available")
    public ResponseEntity<Object> getAvailableTrainings(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {
        String internalUserId = user.getUserId();

        var client = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

        return fetchEntity(client.get().uri("/hrm/internal-users/{internalUserId}/available-training-items", internalUserId));
    }

    @GetMapping("/training-items/completed")
    public ResponseEntity<Object> getCompletedTrainings(
            @AuthenticationPrincipal EverUserPrincipal user
    ) {

        String internalUserId = user.getUserId();
        var client = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

        return fetchEntity(client.get().uri("/hrm/internal-users/{internalUserId}/completed-training-items", internalUserId));
    }

    @PostMapping("/training-enrollments")
    public ResponseEntity<Object> requestTraining(
            @AuthenticationPrincipal EverUserPrincipal user,
            @RequestParam String trainingId
    ) {
        String internalUserId = user.getUserId();

        var client = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

        return fetchEntity(client.post()
                .uri("/hrm/internal-users/{internalUserId}/training-enrollments", internalUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("programId", trainingId)));
    }

    @PatchMapping
    public ResponseEntity<Object> updateProfileThroughWebClient(
            @AuthenticationPrincipal EverUserPrincipal user,
            @RequestBody UpdateProfileRequestDto requestDto
    ) {
        String internalUserId = user.getUserId();

        var client = restClientProvider.getRestClient(ApiClientKey.BUSINESS);

        return fetchEntity(client.patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/hrm/internal-users/{internalUserId}/profile")
                        .build(internalUserId))
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestDto));
    }

    private ResponseEntity<Object> fetchEntity(org.springframework.web.client.RestClient.RequestHeadersSpec<?> requestSpec) {
        ResponseEntity<Object> response = requestSpec.retrieve().toEntity(Object.class);
        return response != null ? response : ResponseEntity.noContent().build();
    }

}
