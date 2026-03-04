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
import org.ever._4ever_be_gw.common.response.ApiResponse;
import org.ever._4ever_be_gw.config.webclient.ApiClientKey;
import org.ever._4ever_be_gw.config.webclient.WebClientProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmHttpServiceImpl implements AlarmHttpService {

    private final WebClientProvider webClientProvider;

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

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            Object serverResponse = alarmWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/notifications/list/{userId}")
                    .queryParamIfPresent("sortBy", Optional.ofNullable(request.getSortBy()))
                    .queryParamIfPresent("order", Optional.ofNullable(request.getOrder()))
                    .queryParamIfPresent("source", Optional.ofNullable(request.getSource()))
                    .queryParamIfPresent("page", Optional.ofNullable(request.getPage()))
                    .queryParamIfPresent("size", Optional.ofNullable(request.getSize()))
                    .build(userId)
                )
                .retrieve()
                .bodyToMono(Object.class)
                .block();

            log.info("알림 목록 조회 성공 - userId: {}", request.getUserId());

            return ResponseEntity.ok(serverResponse);

        } catch (WebClientResponseException ex) {
            handleWebClientError("알림 목록 조회", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("알림 목록 조회 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("알림 목록 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("알림 목록 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<Object> getNotificationCount(
        String userId,
        String status
    ) {
        log.debug("알림 갯수 조회 요청 - userId: {}, status: {}", userId, status);

        AlarmServerRequestDto.NotificationCountRequest request = AlarmDtoConverter.toCountServerRequest(
            UUID.fromString(userId), status
        );

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            Object serverResponse = alarmWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/notifications/count/{userId}")
                    .queryParamIfPresent("status", Optional.ofNullable(request.getStatus()))
                    .build(request.getUserId())
                )
                .retrieve()
                .bodyToMono(Object.class)
                .block();

            log.info("알림 갯수 조회 성공 - userId: {}, status: {}", request.getUserId(),
                request.getStatus());

            return ResponseEntity.ok(serverResponse);

        } catch (WebClientResponseException ex) {
            handleWebClientError("알림 갯수 조회", ex);
            HttpStatus responseStatus = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(responseStatus).body(
                ApiResponse.fail("알림 갯수 조회 중 오류가 발생했습니다.", responseStatus, null)
            );
        } catch (Exception e) {
            log.error("알림 갯수 조회 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("알림 갯수 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
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

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            Object serverResponse = alarmWebClient.patch()
                .uri("/notifications/list/read")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

            log.info("알림 읽음 처리 성공 - userId: {}, processedCount: {}",
                request.getUserId(), notificationMarkReadRequestDto.getNotificationId().size());

            return ResponseEntity.ok(serverResponse);

        } catch (WebClientResponseException ex) {
            handleWebClientError("알림 읽음 처리", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("알림 읽음 처리 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("알림 읽음 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("알림 읽음 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    @Override
    public ResponseEntity<Object> markReadAll(
        String userId
    ) {
        log.debug("전체 알림 읽음 처리 요청");

        AlarmServerRequestDto.NotificationMarkReadAllRequest req = AlarmServerRequestDto.NotificationMarkReadAllRequest.builder()
            .userId(UUID.fromString(userId))
            .build();

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            Object serverResponse = alarmWebClient.patch()
                .uri("/notifications/all/read")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

            log.info("전체 알림 읽음 처리 성공 - userId: {}", userId);

            return ResponseEntity.ok(serverResponse);

        } catch (WebClientResponseException ex) {
            handleWebClientError("전체 알림 읽음 처리", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("전체 알림 읽음 처리 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("전체 알림 읽음 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("전체 알림 읽음 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR,
                    null)
            );
        }
    }

    @Override
    public ResponseEntity<Object> markReadOne(
        String userId,
        String notificationId
    ) {
        log.debug("단일 알림 읽음 처리 요청 - userId: {}, notificationId: {}",
            userId, notificationId);

        AlarmServerRequestDto.NotificationMarkReadOneRequest request =
            NotificationMarkReadOneRequest.builder()
                .userId(UUID.fromString(userId))
                .build();

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            Object serverResponse = alarmWebClient.patch()
                .uri("/notifications/{notificationId}/read", notificationId)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

            log.info("단일 알림 읽음 처리 성공");
            return ResponseEntity.ok(serverResponse);

        } catch (WebClientResponseException ex) {
            handleWebClientError("단일 알림 읽음 처리", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("단일 알림 읽음 처리 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("단일 알림 읽음 처리 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("단일 알림 읽음 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR,
                    null)
            );
        }
    }

    @Override
    public ResponseEntity<Object> registerFcmToken(
        String userId,
        NotificationFcmTokenRequestDto notificationFcmTokenRequestDto
    ) {
        log.debug("FCM 토큰 등록 요청 - userId: {}, token: {}",
            userId, notificationFcmTokenRequestDto.getToken());

        AlarmServerRequestDto.NotificationFcmTokenRequest request = AlarmDtoConverter.toFcmTokenServerRequest(
            UUID.fromString(userId),
            notificationFcmTokenRequestDto.getToken(),
            notificationFcmTokenRequestDto.getDeviceId(),
            notificationFcmTokenRequestDto.getDeviceType()
        );

        try {
            WebClient alarmWebClient = webClientProvider.getWebClient(ApiClientKey.ALARM);

            Object serverResponse = alarmWebClient.post()
                .uri("/device-tokens/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();

            log.info("FCM 토큰 등록 성공 - userId: {}", request.getUserId());

            return ResponseEntity.ok(
                serverResponse
            );

        } catch (WebClientResponseException ex) {
            handleWebClientError("FCM 토큰 등록", ex);
            HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
            return ResponseEntity.status(status).body(
                ApiResponse.fail("FCM 토큰 등록 중 오류가 발생했습니다.", status, null)
            );
        } catch (Exception e) {
            log.error("FCM 토큰 등록 중 예기치 않은 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.fail("FCM 토큰 등록 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR, null)
            );
        }
    }

    /**
     * WebClient 오류를 처리하고 로깅하는 공통 메서드
     * 400, 500번대 에러에 대한 상세 처리
     */
    private void handleWebClientError(String operation, WebClientResponseException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String errorBody = ex.getResponseBodyAsString();

        if (status == HttpStatus.BAD_REQUEST) {
            log.error("{} 실패 - 400 Bad Request. Status: {}, Body: {}", operation,
                ex.getStatusCode(),
                errorBody);
        } else if (status == HttpStatus.UNAUTHORIZED) {
            log.error("{} 실패 - 401 Unauthorized. Status: {}, Body: {}", operation,
                ex.getStatusCode(),
                errorBody);
        } else if (status == HttpStatus.FORBIDDEN) {
            log.error("{} 실패 - 403 Forbidden. Status: {}, Body: {}", operation, ex.getStatusCode(),
                errorBody);
        } else if (status == HttpStatus.NOT_FOUND) {
            log.error("{} 실패 - 404 Not Found. Status: {}, Body: {}", operation, ex.getStatusCode(),
                errorBody);
        } else if (status.is4xxClientError()) {
            log.error("{} 실패 - 4xx Client Error. Status: {}, Body: {}", operation,
                ex.getStatusCode(),
                errorBody);
        } else if (status.is5xxServerError()) {
            log.error("{} 실패 - 5xx Server Error. Status: {}, Body: {}", operation,
                ex.getStatusCode(),
                errorBody);
        } else {
            log.error("{} 실패 - 기타 오류. Status: {}, Body: {}", operation, ex.getStatusCode(),
                errorBody);
        }
    }
}
