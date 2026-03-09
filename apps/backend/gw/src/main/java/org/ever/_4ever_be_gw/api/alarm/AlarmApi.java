package org.ever._4ever_be_gw.api.alarm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationMarkReadRequestDto;
import org.ever._4ever_be_gw.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_gw.common.dto.validation.AllowedValues;
import org.ever._4ever_be_gw.common.dto.validation.ValidUuidV7;
import org.ever._4ever_be_gw.config.security.principal.EverJwtAuthenticationToken;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림", description = "알림(Notification) API")
@ApiServerErrorResponse
public interface AlarmApi {

    @Operation(summary = "알림 목록 조회", description = "알림 목록을 페이징/정렬/필터와 함께 조회합니다.")
    ResponseEntity<Object> getNotificationList(
            @AuthenticationPrincipal EverUserPrincipal principal,
            EverJwtAuthenticationToken authentication,
            @AllowedValues(
                    allowedValues = {"createdAt"},
                    ignoreCase = true,
                    message = "sortBy는 createdAt만 허용됩니다."
            )
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt")
            String sortBy,
            @AllowedValues(
                    allowedValues = {"asc", "desc"},
                    ignoreCase = true,
                    message = "order는 asc 또는 desc만 허용됩니다."
            )
            @RequestParam(name = "order", required = false, defaultValue = "desc")
            String order,
            @AllowedValues(
                    allowedValues = {"PR", "SD", "IM", "FCM", "HRM", "PP", "CUS", "SUP"},
                    ignoreCase = true,
                    message = "유효하지 않은 source 값입니다."
            )
            @RequestParam(name = "source", required = false)
            String source,
            @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
            @RequestParam(name = "page", required = false, defaultValue = "0")
            Integer page,
            @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
            @Max(value = 100, message = "페이지 크기는 최대 100까지 가능합니다.")
            @RequestParam(name = "size", required = false, defaultValue = "20")
            Integer size
    );

    @Operation(summary = "알림 갯수 조회", description = "상태별(READ/UNREAD) 알림 갯수를 조회합니다.")
    ResponseEntity<Object> getNotificationCount(
            @AuthenticationPrincipal EverUserPrincipal principal,
            EverJwtAuthenticationToken authentication,
            @AllowedValues(
                    allowedValues = {"READ", "UNREAD"},
                    ignoreCase = true,
                    message = "유효하지 않은 status 값입니다. 허용값: READ, UNREAD"
            )
            @RequestParam(name = "status", required = false)
            String status
    );

    @Operation(summary = "알림 구독 요청", description = "SSE를 통해 실시간 알림을 구독합니다.")
    SseEmitter subscribe(
            @AuthenticationPrincipal EverUserPrincipal principal,
            EverJwtAuthenticationToken authentication,
            HttpServletRequest request
    );

    @Operation(summary = "알림 읽음 처리(목록)", description = "주어진 알림 ID 목록을 읽음 처리합니다.")
    ResponseEntity<Object> markReadList(
            @AuthenticationPrincipal EverUserPrincipal principal,
            EverJwtAuthenticationToken authentication,
            @Valid @RequestBody NotificationMarkReadRequestDto notificationMarkReadRequestDto
    );

    @Operation(summary = "알림 읽음 처리(전체)", description = "모든 알림을 읽음 처리합니다.")
    ResponseEntity<Object> markReadAll(
            @AuthenticationPrincipal EverUserPrincipal principal,
            EverJwtAuthenticationToken authentication
    );

    @Operation(summary = "알림 읽음 처리(단일)", description = "특정 알림을 읽음 처리합니다.")
    ResponseEntity<Object> markReadOne(
            @AuthenticationPrincipal EverUserPrincipal principal,
            EverJwtAuthenticationToken authentication,
            @PathVariable("notificationId") @ValidUuidV7 String notificationId
    );
}
