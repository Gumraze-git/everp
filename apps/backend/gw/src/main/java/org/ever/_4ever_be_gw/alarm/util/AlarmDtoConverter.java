package org.ever._4ever_be_gw.alarm.util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_gw.alarm.dto.request.AlarmServerRequestDto;
import org.ever._4ever_be_gw.alarm.dto.response.AlarmServerResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_gw.alarm.dto.response.NotificationReadResponseDto;
import org.ever._4ever_be_gw.common.dto.PageDto;
import org.ever._4ever_be_gw.common.dto.pagable.PageResponseDto;

@Slf4j
public class AlarmDtoConverter {

    /**
     * 알림 목록 조회 요청 변환
     */
    public static AlarmServerRequestDto.NotificationListRequest toServerRequest(
        UUID userId,
        String sortBy,
        String order,
        String source,
        Integer page,
        Integer size
    ) {
        return AlarmServerRequestDto.NotificationListRequest.builder()
            .userId(userId)
            .sortBy(sortBy)
            .order(order)
            .source(source)
            .page(page)
            .size(size)
            .build();
    }

    /**
     * 알림 갯수 조회 요청 변환
     */
    public static AlarmServerRequestDto.NotificationCountRequest toCountServerRequest(
        UUID userId,
        String status
    ) {
        return AlarmServerRequestDto.NotificationCountRequest.builder()
            .userId(userId)
            .status(status)
            .build();
    }

    /**
     * 알림 읽음 처리 요청 변환
     */
    public static AlarmServerRequestDto.NotificationMarkReadRequest toMarkReadServerRequest(
        UUID userId,
        List<String> notificationIds
    ) {
        return AlarmServerRequestDto.NotificationMarkReadRequest.builder()
            .userId(userId)
            .notificationIds(notificationIds)
            .build();
    }

    /**
     * 단일 알림 읽음 처리 요청 변환
     */
    public static AlarmServerRequestDto.NotificationMarkReadOneRequest toMarkReadOneServerRequest(
        UUID userId,
        String notificationId
    ) {
        return AlarmServerRequestDto.NotificationMarkReadOneRequest.builder()
            .userId(userId)
            .build();
    }

    /**
     * FCM 토큰 등록 요청 변환
     */
    public static AlarmServerRequestDto.NotificationFcmTokenRequest toFcmTokenServerRequest(
        UUID userId,
        String token,
        String deviceId,
        String deviceType
    ) {
        return AlarmServerRequestDto.NotificationFcmTokenRequest.builder()
            .userId(userId)
            .token(token)
            .deviceId(deviceId)
            .deviceType(deviceType)
            .build();
    }

    /**
     * 서버 응답을 클라이언트 응답으로 변환 (알림 목록)
     */
    public static PageResponseDto<NotificationListResponseDto> toClientResponse(
        AlarmServerResponseDto.NotificationListResponse serverResponse
    ) {
        List<NotificationListResponseDto> items = serverResponse.getItems().stream()
            .map(item -> NotificationListResponseDto.builder()
                .notificationId(java.util.UUID.fromString(item.getNotificationId()))
                .notificationTitle(item.getNotificationTitle())
                .notificationMessage(item.getNotificationMessage())
                .linkType(item.getLinkType())
                .linkId(java.util.UUID.fromString(item.getLinkId()))
                .source(item.getSource())
                .createdAt(item.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        AlarmServerResponseDto.NotificationListResponse.PageInfo pageInfo = serverResponse.getPage();
        PageDto pageDto = PageDto.builder()
            .number(pageInfo.getNumber())
            .size(pageInfo.getSize())
            .totalElements(pageInfo.getTotalElements())
            .totalPages(pageInfo.getTotalPages())
            .hasNext(pageInfo.isHasNext())
            .build();

        return PageResponseDto.<NotificationListResponseDto>builder()
            .items(items)
            .page(pageDto)
            .build();
    }

    /**
     * 서버 응답을 클라이언트 응답으로 변환 (알림 갯수)
     */
    public static NotificationCountResponseDto toClientResponse(
        AlarmServerResponseDto.NotificationCountResponse serverResponse
    ) {
        log.debug("변환전 - toClientResponse: dto={}", serverResponse);

        NotificationCountResponseDto dto = NotificationCountResponseDto.builder()
            .count(serverResponse.getCount())
            .build();

        log.debug("변환후 - NotificationCountResponseDto:  dto={}", dto);
        return dto;
    }

    /**
     * 서버 응답을 클라이언트 응답으로 변환 (읽음 처리)
     */
    public static NotificationReadResponseDto toClientResponse(
        AlarmServerResponseDto.NotificationMarkReadResponse serverResponse
    ) {
        return NotificationReadResponseDto.builder()
            .processedCount(serverResponse.getProcessedCount())
            .build();
    }
}
