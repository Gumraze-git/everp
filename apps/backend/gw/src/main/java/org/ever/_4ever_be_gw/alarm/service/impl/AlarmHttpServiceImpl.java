package org.ever._4ever_be_gw.alarm.service.impl;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto.NotificationMarkReadOneRequest;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationFcmTokenRequestDto;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationMarkReadRequestDto;
import org.ever._4ever_be_gw.alarm.service.AlarmHttpService;
import org.ever._4ever_be_gw.alarm.util.AlarmDtoConverter;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.common.exception.handler.ProblemDetailFactory;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.restclient.RestClientProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmHttpServiceImpl implements AlarmHttpService {

    private final RestClientProvider restClientProvider;

    @Override
    public ResponseEntity<Object> getNotificationList(
        String userId,
        String sortBy,
        String order,
        String source,
        Integer page,
        Integer size
    ) {
        log.debug("알림 목록 조회 요청 - userId: {}, sortBy: {}, order: {}, source: {}, page: {}, size: {}",
            userId, sortBy, order, source, page, size);

        AlarmServerRequestDto.NotificationListRequest request = AlarmDtoConverter.toServerRequest(
            UUID.fromString(userId), sortBy, order, source, page, size
        );

        return execute(
            "알림 목록 조회",
            () -> alarmClient().get()
                .uri(uriBuilder -> uriBuilder.path("/notifications/list/{userId}")
                    .queryParamIfPresent("sortBy", Optional.ofNullable(request.getSortBy()))
                    .queryParamIfPresent("order", Optional.ofNullable(request.getOrder()))
                    .queryParamIfPresent("source", Optional.ofNullable(request.getSource()))
                    .queryParamIfPresent("page", Optional.ofNullable(request.getPage()))
                    .queryParamIfPresent("size", Optional.ofNullable(request.getSize()))
                    .build(userId))
                .retrieve()
                .toEntity(Object.class)
        );
    }

    @Override
    public ResponseEntity<Object> getNotificationCount(String userId, String status) {
        log.debug("알림 갯수 조회 요청 - userId: {}, status: {}", userId, status);

        AlarmServerRequestDto.NotificationCountRequest request = AlarmDtoConverter.toCountServerRequest(
            UUID.fromString(userId), status
        );

        return execute(
            "알림 갯수 조회",
            () -> alarmClient().get()
                .uri(uriBuilder -> uriBuilder.path("/notifications/count/{userId}")
                    .queryParamIfPresent("status", Optional.ofNullable(request.getStatus()))
                    .build(request.getUserId()))
                .retrieve()
                .toEntity(Object.class)
        );
    }

    @Override
    public ResponseEntity<Object> markReadList(
        String userId,
        NotificationMarkReadRequestDto notificationMarkReadRequestDto
    ) {
        log.debug("알림 읽음 처리 요청 - userId: {}, notificationIds: {}",
            userId, notificationMarkReadRequestDto.getNotificationId());

        AlarmServerRequestDto.NotificationMarkReadRequest request = AlarmDtoConverter.toMarkReadServerRequest(
            UUID.fromString(userId),
            notificationMarkReadRequestDto.getNotificationId()
        );

        return execute(
            "알림 읽음 처리",
            () -> alarmClient().patch()
                .uri("/notifications/list/read")
                .body(request)
                .retrieve()
                .toEntity(Object.class)
        );
    }

    @Override
    public ResponseEntity<Object> markReadAll(String userId) {
        log.debug("전체 알림 읽음 처리 요청 - userId: {}", userId);

        AlarmServerRequestDto.NotificationMarkReadAllRequest request =
            AlarmServerRequestDto.NotificationMarkReadAllRequest.builder()
                .userId(UUID.fromString(userId))
                .build();

        return execute(
            "전체 알림 읽음 처리",
            () -> alarmClient().patch()
                .uri("/notifications/all/read")
                .body(request)
                .retrieve()
                .toEntity(Object.class)
        );
    }

    @Override
    public ResponseEntity<Object> markReadOne(String userId, String notificationId) {
        log.debug("단일 알림 읽음 처리 요청 - userId: {}, notificationId: {}", userId, notificationId);

        AlarmServerRequestDto.NotificationMarkReadOneRequest request = NotificationMarkReadOneRequest.builder()
            .userId(UUID.fromString(userId))
            .build();

        return execute(
            "단일 알림 읽음 처리",
            () -> alarmClient().patch()
                .uri("/notifications/{notificationId}/read", notificationId)
                .body(request)
                .retrieve()
                .toEntity(Object.class)
        );
    }

    @Override
    public ResponseEntity<Object> registerFcmToken(
        String userId,
        NotificationFcmTokenRequestDto notificationFcmTokenRequestDto
    ) {
        log.debug("FCM 토큰 등록 요청 - userId: {}, token: {}", userId, notificationFcmTokenRequestDto.getToken());

        AlarmServerRequestDto.NotificationFcmTokenRequest request = AlarmDtoConverter.toFcmTokenServerRequest(
            UUID.fromString(userId),
            notificationFcmTokenRequestDto.getToken(),
            notificationFcmTokenRequestDto.getDeviceId(),
            notificationFcmTokenRequestDto.getDeviceType()
        );

        return execute(
            "FCM 토큰 등록",
            () -> alarmClient().post()
                .uri("/device-tokens/register")
                .body(request)
                .retrieve()
                .toEntity(Object.class)
        );
    }

    private ResponseEntity<Object> execute(String operation, RequestExecutor executor) {
        try {
            ResponseEntity<Object> response = executor.execute();
            return response != null ? response : ResponseEntity.noContent().build();
        } catch (RestClientResponseException ex) {
            logWebClientError(operation, ex);
            return ResponseEntity.status(HttpStatus.valueOf(ex.getStatusCode().value()))
                .body(ProblemDetailFactory.fromRestClientResponseException(ex, "alarm"));
        } catch (Exception e) {
            log.error("{} 중 예기치 않은 오류 발생", operation, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ProblemDetailFactory.of(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    operation + " 중 오류가 발생했습니다.",
                    operation + " 중 오류가 발생했습니다.",
                    null,
                    null,
                    ErrorCode.INTERNAL_SERVER_ERROR.getCode()
                ));
        }
    }

    private void logWebClientError(String operation, RestClientResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorBody = ex.getResponseBodyAsString();

        if (status == HttpStatus.BAD_REQUEST) {
            log.error("{} 실패 - 400 Bad Request. Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);
        } else if (status == HttpStatus.UNAUTHORIZED) {
            log.error("{} 실패 - 401 Unauthorized. Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);
        } else if (status == HttpStatus.FORBIDDEN) {
            log.error("{} 실패 - 403 Forbidden. Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);
        } else if (status == HttpStatus.NOT_FOUND) {
            log.error("{} 실패 - 404 Not Found. Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);
        } else if (status.is4xxClientError()) {
            log.error("{} 실패 - 4xx Client Error. Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);
        } else if (status.is5xxServerError()) {
            log.error("{} 실패 - 5xx Server Error. Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);
        } else {
            log.error("{} 실패 - 기타 오류. Status: {}, Body: {}", operation, ex.getStatusCode(), errorBody);
        }
    }

    private RestClient alarmClient() {
        return restClientProvider.getRestClient(ApiClientKey.ALARM);
    }

    @FunctionalInterface
    private interface RequestExecutor {
        ResponseEntity<Object> execute();
    }
}
