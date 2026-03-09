package org.ever._4ever_be_gw.api.business;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.business.dto.hrm.UpdateProfileRequestDto;
import org.ever._4ever_be_gw.config.restclient.RestClientProvider;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "프로필", description = "프로필 API")
@ApiServerErrorResponse
public interface ProfileApi {

    public ResponseEntity<Object> getEmployeeProfile(
            @AuthenticationPrincipal EverUserPrincipal user
    );

    public ResponseEntity<Object> getEmployeeAttendanceRecords(
            @AuthenticationPrincipal EverUserPrincipal user
    );

    public ResponseEntity<Object> getTodayAttendance(
            @AuthenticationPrincipal EverUserPrincipal user
    );

    public ResponseEntity<Object> getInProgressTrainings(
            @AuthenticationPrincipal EverUserPrincipal user
    );

    public ResponseEntity<Object> getAvailableTrainings(
            @AuthenticationPrincipal EverUserPrincipal user
    );

    public ResponseEntity<Object> getCompletedTrainings(
            @AuthenticationPrincipal EverUserPrincipal user
    );

    public ResponseEntity<Object> requestTraining(
            @AuthenticationPrincipal EverUserPrincipal user,
            @RequestParam String trainingId
    );

    public ResponseEntity<Object> updateProfileThroughWebClient(
            @AuthenticationPrincipal EverUserPrincipal user,
            @RequestBody UpdateProfileRequestDto requestDto
    );

}
