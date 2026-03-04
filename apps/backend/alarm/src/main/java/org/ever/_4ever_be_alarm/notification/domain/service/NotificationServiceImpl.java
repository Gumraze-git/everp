package org.ever._4ever_be_alarm.notification.domain.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.common.response.PageResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationReadResponseDto;
import org.ever._4ever_be_alarm.notification.domain.model.Noti;
import org.ever._4ever_be_alarm.notification.domain.model.UserDeviceInfo;
import org.ever._4ever_be_alarm.notification.domain.model.constants.ReferenceTypeEnum;
import org.ever._4ever_be_alarm.notification.domain.model.constants.SourceTypeEnum;
import org.ever._4ever_be_alarm.notification.domain.port.in.NotificationQueryUseCase;
import org.ever._4ever_be_alarm.notification.domain.port.in.NotificationSendUseCase;
import org.ever._4ever_be_alarm.notification.domain.port.out.NotificationDispatchPort;
import org.ever._4ever_be_alarm.notification.domain.port.out.NotificationRepositoryPort;
import org.ever._4ever_be_alarm.notification.domain.port.out.UserDeviceTokenRepositoryPort;
import org.ever._4ever_be_alarm.notification.domain.port.out.strategy.DispatchStrategy;
import org.ever.event.AlarmEvent;
import org.ever.event.StatusEvent;
import org.ever.event.alarm.TargetType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationQueryUseCase, NotificationSendUseCase {

    private final NotificationRepositoryPort notificationRepository;
    private final Map<String, NotificationDispatchPort> notificationDispatchPorts;
    private final UserDeviceTokenRepositoryPort userDeviceTokenRepository;

    @Transactional(readOnly = true)
    @Override
    public PageResponseDto<NotificationListResponseDto> getNotificationPage(
        String userId,
        String sortBy,
        String order,
        String source,
        int page, int size
    ) {
        SourceTypeEnum sourceType = SourceTypeEnum.fromString(source);

        UUID userUuid = UUID.fromString(userId);

        return notificationRepository
            .getNotificationList(userUuid, sortBy, order, sourceType, page, size);
    }

    @Transactional(readOnly = true)
    @Override
    public NotificationCountResponseDto getNotificationCount(String userId, String status) {
        UUID userUuid = UUID.fromString(userId);

        if (status == null || status.isEmpty()) { // 전체 카운트
            return notificationRepository.countByUserId(userUuid);
        } else if (status.equalsIgnoreCase("READ")) { // 읽음 카운트
            return notificationRepository.countByUserIdAndStatus(userUuid, true);
        } else if (status.equalsIgnoreCase("UNREAD")) { // 안읽음 카운트
            return notificationRepository.countByUserIdAndStatus(userUuid, false);
        } else { // 잘못된 상태 값
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
    }

    @Transactional
    @Override
    public NotificationReadResponseDto markAsReadList(String userId, List<String> notificationIds) {
        UUID userUuid = UUID.fromString(userId);
        List<UUID> notificationUuidList = notificationIds.stream()
            .map(UUID::fromString)
            .toList();

        return notificationRepository.markAsReadList(userUuid, notificationUuidList);
    }

    @Transactional
    @Override
    public NotificationReadResponseDto markAsReadAll(String userId) {
        UUID userUuid = UUID.fromString(userId);

        return notificationRepository.markAsReadAll(userUuid);
    }

    @Transactional
    @Override
    public NotificationReadResponseDto markAsReadOne(String userId, String notificationId) {
        UUID userUuid = UUID.fromString(userId);
        UUID notificationUuid = UUID.fromString(notificationId);

        return notificationRepository.markAsRead(userUuid, notificationUuid);
    }

//    @Override
//    public void sendNotification(AlarmSentEvent event) {
//        // TODO: 알림 전송 로직 구현
//        NotificationDispatchPort sseEmitter = notificationDispatchAdapters.get(SSE_STRATEGY_NAME);
//
//        if (event.getTargetId().isEmpty()) {
//            NotificationDispatchPort pushAlarm = notificationDispatchAdapters.get(
//                APP_PUSH_STRATEGY_NAME);
//            // TODO: Push 알림 전송 로직 구현
//        }
//        // TODO: SSE 알림 전송 로직 구현
//    }

    @Transactional
    @Override
    public UUID createNotification(AlarmEvent event) {
        log.info("[NOTIFICATION-CREATE] 요청 시작 - alarmId: {}, targetId: {}, targetType: {}",
            event.getAlarmId(), event.getTargetId(), event.getTargetType());

        try {
            // 필수 필드 검증
            validateAlarmEvent(event);

            // ID 변환 (linkId는 선택적)
            UUID alarmId = UUID.fromString(event.getAlarmId());
            UUID targetId = UUID.fromString(event.getTargetId());
            UUID linkId = event.getLinkId() != null
                ? UUID.fromString(event.getLinkId())
                : null;

            log.debug("[NOTIFICATION-CREATE] 필드 변환 완료 - alarmId: {}, targetId: {}, linkId: {}",
                alarmId, targetId, linkId);

            Noti notification = Noti.builder()
                .id(alarmId)
                .targetId(targetId)
                .targetType(event.getTargetType())
                .title(event.getTitle())
                .message(event.getMessage())
                .referenceId(linkId)
                .referenceType(event.getLinkType() != null
                    ? ReferenceTypeEnum.fromString(event.getLinkType().name())
                    : null)
                .source(SourceTypeEnum.fromString(event.getSource()))
                .scheduledAt(event.getScheduledAt())
                .build();

            log.debug("[NOTIFICATION-CREATE] Notification 도메인 객체 생성 완료");

            // TODO: 부서 대상(DEPARTMENT) 알림 처리 구현 필요
            // 부서 구성원 조회 및 각 구성원별 알림 생성 로직 필요
            if (event.getTargetType() == TargetType.DEPARTMENT) {
                log.warn("[NOTIFICATION-CREATE] DEPARTMENT 타입은 아직 미구현 - alarmId: {}", alarmId);
                // TODO: 부서 구성원 조회 및 알림 생성 로직 구현
                throw new UnsupportedOperationException("DEPARTMENT 타입 알림은 아직 지원하지 않습니다.");
            }

            // 알림 저장
            log.info("[NOTIFICATION-SAVE] 알림 저장 시작 - alarmId: {}", alarmId);
            Noti result = notificationRepository.save(notification);
            log.info("[NOTIFICATION-SAVE] 알림 저장 완료 - notificationId: {}", result.getId());

            // 알림 발송
            log.info("[NOTIFICATION-DISPATCH] 알림 발송 시작 - notificationId: {}", result.getId());
            dispatchNotification(result);
            log.info("[NOTIFICATION-DISPATCH] 알림 발송 완료 - notificationId: {}", result.getId());

            log.info("[NOTIFICATION-CREATE] 전체 프로세스 완료 - notificationId: {}", result.getId());
            return result.getId();

        } catch (IllegalArgumentException e) {
            log.error("[NOTIFICATION-CREATE] 잘못된 인자 - alarmId: {}, error: {}",
                event.getAlarmId(), e.getMessage(), e);
            throw new IllegalArgumentException("알림 생성 실패: " + e.getMessage(), e);
        } catch (UnsupportedOperationException e) {
            log.error("[NOTIFICATION-CREATE] 지원하지 않는 기능 - alarmId: {}, error: {}",
                event.getAlarmId(), e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("[NOTIFICATION-CREATE] 예상치 못한 오류 - alarmId: {}, error: {}",
                event.getAlarmId(), e.getMessage(), e);
            throw new RuntimeException("알림 생성 중 오류 발생", e);
        }
    }

    /**
     * AlarmEvent 필수 필드 검증
     * TODO: 더 세밀한 검증 규칙 추가 필요 (예: 제목/내용 길이 제한 등)
     */
    private void validateAlarmEvent(AlarmEvent event) {
        if (event.getAlarmId() == null || event.getAlarmId().isBlank()) {
            throw new IllegalArgumentException("alarmId는 필수입니다.");
        }
        if (event.getTargetId() == null || event.getTargetId().isBlank()) {
            throw new IllegalArgumentException("targetId는 필수입니다.");
        }
        if (event.getTitle() == null || event.getTitle().isBlank()) {
            throw new IllegalArgumentException("title은 필수입니다.");
        }
        if (event.getMessage() == null || event.getMessage().isBlank()) {
            throw new IllegalArgumentException("message는 필수입니다.");
        }
        if (event.getSource() == null || event.getSource().isBlank()) {
            throw new IllegalArgumentException("source는 필수입니다.");
        }
        log.info("[VALIDATION] 필수 필드 검증 통과");
    }

    /**
     * 알림 발송
     * TODO: 발송 실패 시 재시도 로직 추가 필요
     * TODO: 발송 성공 여부를 DB에 기록하는 로직 추가 필요
     */
    public void dispatchNotification(Noti notification) {
        log.info("[NOTIFICATION-DISPATCH] 발송 전략 결정 - targetType: {}, notificationId: {}",
            notification.getTargetType(), notification.getId());

        try {
            // 외부 대상(CUSTOMER, SUPPLIER)에게는 PUSH 알림 발송
            if (notification.getTargetType() == TargetType.CUSTOMER
                || notification.getTargetType() == TargetType.SUPPLIER) {
                sendPushNotification(notification);
            }

            sendSseNotification(notification);

        } catch (Exception e) {
            log.error("[NOTIFICATION-DISPATCH] 알림 발송 실패 - notificationId: {}, error: {}",
                notification.getId(), e.getMessage(), e);
            // TODO: 발송 실패 시 재시도 로직 또는 DLQ 전송 로직 추가 필요
            throw new RuntimeException("알림 발송 중 오류 발생 - notificationId: " + notification.getId(),
                e);
        }
    }

    @Transactional
    @Override
    public void updateNotificationStatus(StatusEvent event) {
        // TODO: 알림 상태 업데이트 로직 구현
//        notificationRepository.updateNotificationStatus(event.getEventId(), event.isSuccess());
    }

    private void sendPushNotification(Noti notification) {
        // 외부 사용자(CUSTOMER, SUPPLIER)에게는 PUSH 알림 발송
        log.info("[NOTIFICATION-DISPATCH] PUSH 알림 발송 시작 - notificationId: {}, targetId: {}",
            notification.getId(), notification.getTargetId());

        // FCM 토큰 조회
        List<UserDeviceInfo> tokens = userDeviceTokenRepository
            .findActiveTokensByUserId(notification.getTargetId());

        if (tokens == null || tokens.isEmpty()) {
            log.warn(
                "[NOTIFICATION-DISPATCH] 등록된 FCM 토큰이 없습니다 - notificationId: {}, targetId: {}, 알림 전송 종료",
                notification.getId(), notification.getTargetId());
            return;
        }

        log.info(
            "[NOTIFICATION-DISPATCH] FCM 토큰 조회 완료 - notificationId: {}, tokenCount: {}",
            notification.getId(), tokens.size());

        NotificationDispatchPort pushAlarm = notificationDispatchPorts
            .get(DispatchStrategy.APP_PUSH.getBeanName());

        if (pushAlarm == null) {
            log.error(
                "[NOTIFICATION-DISPATCH] PUSH 어댑터를 찾을 수 없음 - strategy: {}, notificationId: {}",
                DispatchStrategy.APP_PUSH.getBeanName(), notification.getId());
            // TODO: PUSH 어댑터 없을 때 처리 로직 추가 필요
        } else {
            // 각 토큰에 대해 푸시 알림 발송
            for (UserDeviceInfo token : tokens) {
                try {
                    // Noti 객체에 FCM 토큰 설정
                    notification.setFcmToken(token.getFcmToken());

                    pushAlarm.dispatch(notification);

                    log.info(
                        "[NOTIFICATION-DISPATCH] PUSH 알림 발송 완료 - notificationId: {}, tokenId: {}",
                        notification.getId(), token.getId());
                } catch (Exception e) {
                    log.error(
                        "[NOTIFICATION-DISPATCH] PUSH 알림 발송 실패 (개별 토큰) - notificationId: {}, tokenId: {}, error: {}",
                        notification.getId(), token.getId(), e.getMessage(), e);
                    // TODO: 실패 로그 수집하여 별도 처리 로직 추가 가능
                }
            }
        }
    }

    private void sendSseNotification(Noti notification) {
        // 내부 사용자(EMPLOYEE) & 외부 사용자(CUSTOMER, SUPPLIER)에게는 SSE 알림 발송
        // TODO: EMPLOYEE일 때만 SSE 발송하도록 조건 추가 검토 필요
        log.info("[NOTIFICATION-DISPATCH] SSE 알림 발송 시작 - notificationId: {}",
            notification.getId());

        NotificationDispatchPort sseEmitter = notificationDispatchPorts
            .get(DispatchStrategy.SSE.getBeanName());

        if (sseEmitter == null) {
            log.error(
                "[NOTIFICATION-DISPATCH] SSE 어댑터를 찾을 수 없음 - strategy: {}, notificationId: {}",
                DispatchStrategy.SSE.getBeanName(), notification.getId());
            // TODO: SSE 어댑터 없을 때 처리 로직 추가 필요
        } else {
            sseEmitter.dispatch(notification);
            log.info("[NOTIFICATION-DISPATCH] SSE 알림 발송 완료 - notificationId: {}",
                notification.getId());
        }
    }


}
