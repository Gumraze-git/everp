package org.ever._4ever_be_gw.alarm.service;

import org.ever._4ever_be_gw.alarm.dto.request.NotificationFcmTokenRequestDto;
import org.ever._4ever_be_gw.alarm.dto.request.NotificationMarkReadRequestDto;
import org.springframework.http.ResponseEntity;

public interface AlarmHttpService {

    /**
     * 알림 목록 조회
     */
    ResponseEntity<Object> getNotificationList(
        String userId,
        String sortBy,
        String order,
        String source,
        Integer page,
        Integer size
    );

    /**
     * 알림 갯수 조회
     */
    ResponseEntity<Object> getNotificationCount(
        String userId,
        String status
    );

    /**
     * 알림 읽음 처리 (목록)
     */
    ResponseEntity<Object> markReadList(
        String userId,
        NotificationMarkReadRequestDto notificationMarkReadRequestDto
    );

    /**
     * 알림 읽음 처리 (전체)
     */
    ResponseEntity<Object> markReadAll(
        String userId
    );

    /**
     * 알림 읽음 처리 (단일)
     */
    ResponseEntity<Object> markReadOne(
        String userId,
        String notificationId
    );

    /**
     * FCM 토큰 등록
     */
    ResponseEntity<Object> registerFcmToken(
        String userId,
        NotificationFcmTokenRequestDto notificationFcmTokenRequestDto
    );
}
