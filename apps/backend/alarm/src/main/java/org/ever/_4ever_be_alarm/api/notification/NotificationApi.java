package org.ever._4ever_be_alarm.api.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.ever._4ever_be_alarm.api.common.ApiServerErrorResponse;
import org.ever._4ever_be_alarm.common.response.PageResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.NotificationMarkReadAllAndOneRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.NotificationMarkReadRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationReadResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "알림", description = "알림 조회 및 읽음 처리 API")
@ApiServerErrorResponse
public interface NotificationApi {

    @Operation(summary = "알림 목록 조회", description = "사용자 알림 목록을 페이지네이션으로 조회합니다.")
    ResponseEntity<PageResponseDto<NotificationListResponseDto>> getNotificationList(
            @PathVariable("userId") String userId,
            @RequestParam(name = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "order", required = false, defaultValue = "desc") String order,
            @RequestParam(name = "source", required = false) String source,
            @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size", required = false, defaultValue = "20") Integer size
    );

    @Operation(summary = "알림 개수 조회", description = "읽음 상태별 알림 개수를 조회합니다.")
    ResponseEntity<NotificationCountResponseDto> getNotificationCount(
            @PathVariable("userId") String userId,
            @RequestParam(name = "status", required = false, defaultValue = "UNREAD") String status
    );

    @Operation(summary = "알림 목록 읽음 처리", description = "여러 알림을 읽음 상태로 변경합니다.")
    ResponseEntity<NotificationReadResponseDto> markReadList(@RequestBody NotificationMarkReadRequestDto request);

    @Operation(summary = "전체 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 상태로 변경합니다.")
    ResponseEntity<NotificationReadResponseDto> markReadAll(@RequestBody NotificationMarkReadAllAndOneRequestDto request);

    @Operation(summary = "단일 알림 읽음 처리", description = "하나의 알림을 읽음 상태로 변경합니다.")
    ResponseEntity<NotificationReadResponseDto> markReadOne(
            @PathVariable("notificationId") String notificationId,
            @RequestBody NotificationMarkReadAllAndOneRequestDto request
    );
}
