package org.ever._4ever_be_alarm.notification.adapter.web.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.common.response.ApiResponse;
import org.ever._4ever_be_alarm.common.response.PageResponseDto;
import org.ever._4ever_be_alarm.common.validation.AllowedValues;
import org.ever._4ever_be_alarm.common.validation.ValidUuidV7;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.NotificationMarkReadAllAndOneRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.request.NotificationMarkReadRequestDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationReadResponseDto;
import org.ever._4ever_be_alarm.notification.domain.port.in.NotificationQueryUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationQueryUseCase notificationQueryUseCase;

    /**
     * 알림 목록 조회
     */
    @GetMapping("/list/{userId}")
    public ResponseEntity<ApiResponse<PageResponseDto<NotificationListResponseDto>>> getNotificationList(
        @ValidUuidV7
        @PathVariable("userId")
        String userId,
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
    ) {
        log.info(
            "[API] 알림 목록 조회 요청 시작 - userId: {}, sortBy: {}, order: {}, source: {}, page: {}, size: {}",
            userId, sortBy, order, source, page, size);

        try {
            PageResponseDto<NotificationListResponseDto> result = notificationQueryUseCase.getNotificationPage(
                userId, sortBy, order, source, page, size
            );

            log.info("[API] 알림 목록 조회 성공 - userId: {}, totalElements: {}, totalPages: {}",
                userId, result.getPage().getTotalElements(), result.getPage().getTotalPages());
            return ResponseEntity.ok(
                ApiResponse.success(result, "알림 목록을 조회했습니다.", HttpStatus.OK)
            );

        } catch (Exception e) {
            log.error("[API] 알림 목록 조회 실패 - userId: {}, Error: {}",
                userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 알림 갯수 조회
     */
    @GetMapping("/count/{userId}")
    public ResponseEntity<ApiResponse<NotificationCountResponseDto>> getNotificationCount(
        @ValidUuidV7
        @PathVariable("userId")
        String userId,
        @AllowedValues(
            allowedValues = {"READ", "UNREAD"},
            ignoreCase = true,
            message = "유효하지 않은 status 값입니다. 허용값: READ, UNREAD"
        )
        @RequestParam(name = "status", required = false, defaultValue = "UNREAD")
        String status
    ) {
        log.info("[API] 알림 갯수 조회 요청 시작 - userId: {}, status: {}", userId, status);

        try {
            NotificationCountResponseDto response = notificationQueryUseCase.getNotificationCount(
                userId, status
            );

            log.info("[API] 알림 갯수 조회 성공 - userId: {}, status: {}, count: {}",
                userId, status, response.getCount());
            return ResponseEntity.ok(
                ApiResponse.success(response, "알림 개수를 조회했습니다.", HttpStatus.OK)
            );

        } catch (Exception e) {
            log.error("[API] 알림 갯수 조회 실패 - userId: {}, status: {}, Error: {}",
                userId, status, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 알림 읽음 처리 (목록)
     */
    @PatchMapping("/list/read")
    public ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadList(
        @Valid
        @RequestBody
        NotificationMarkReadRequestDto request
    ) {
        log.info("[API] 알림 읽음 처리 요청 시작 - userId: {}, notificationCount: {}",
            request.getUserId(), request.getNotificationIds().size());

        try {
            NotificationReadResponseDto response = notificationQueryUseCase.markAsReadList(
                request.getUserId(),
                request.getNotificationIds()
            );

            log.info("[API] 알림 읽음 처리 성공 - userId: {}, processedCount: {}, totalRequested: {}",
                request.getUserId(), response.getProcessedCount(),
                request.getNotificationIds().size());
            return ResponseEntity.ok(
                ApiResponse.success(response, "알림 읽음 처리가 완료되었습니다.", HttpStatus.OK)
            );

        } catch (Exception e) {
            log.error("[API] 알림 읽음 처리 실패 - userId: {}, notificationCount: {}, Error: {}",
                request.getUserId(), request.getNotificationIds().size(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 알림 읽음 처리 (전체)
     */
    @PatchMapping("/all/read")
    public ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadAll(
        @Valid
        @RequestBody
        NotificationMarkReadAllAndOneRequestDto request
    ) {
        String userId = request.getUserId();
        log.info("[API] 전체 알림 읽음 처리 요청 시작 - userId: {}", userId);

        try {
            NotificationReadResponseDto response = notificationQueryUseCase.markAsReadAll(userId);

            log.info("[API] 전체 알림 읽음 처리 성공 - userId: {}, processedCount: {}",
                userId, response.getProcessedCount());
            return ResponseEntity.ok(
                ApiResponse.success(response, "전체 알림 읽음 처리가 완료되었습니다.", HttpStatus.OK)
            );

        } catch (Exception e) {
            log.error("[API] 전체 알림 읽음 처리 실패 - userId: {}, Error: {}",
                userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 알림 읽음 처리 (단일)
     */
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationReadResponseDto>> markReadOne(
        @ValidUuidV7
        @PathVariable("notificationId")
        String notificationId,
        @Valid
        @RequestBody
        NotificationMarkReadAllAndOneRequestDto request
    ) {
        String userId = request.getUserId();
        log.info("[API] 단일 알림 읽음 처리 요청 시작 - userId: {}, notificationId: {}",
            userId, notificationId);

        try {
            NotificationReadResponseDto response = notificationQueryUseCase.markAsReadOne(userId,
                notificationId);

            log.info("[API] 단일 알림 읽음 처리 성공 - userId: {}, notificationId: {}, processedCount: {}",
                userId, notificationId, response.getProcessedCount());
            return ResponseEntity.ok(
                ApiResponse.success(response, "알림 읽음 처리가 완료되었습니다.", HttpStatus.OK)
            );

        } catch (Exception e) {
            log.error("[API] 단일 알림 읽음 처리 실패 - userId: {}, notificationId: {}, Error: {}",
                userId, notificationId, e.getMessage(), e);
            throw e;
        }
    }
}
