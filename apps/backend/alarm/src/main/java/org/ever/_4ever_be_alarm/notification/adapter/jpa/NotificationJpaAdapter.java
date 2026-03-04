package org.ever._4ever_be_alarm.notification.adapter.jpa;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.common.response.PageDto;
import org.ever._4ever_be_alarm.common.response.PageResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.Notification;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.NotificationStatus;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.NotificationTarget;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.Source;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.ChannelRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationChannelRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationErrorCodeRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationLogRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationStatusRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationTargetRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationTemplateRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.SourceRepository;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationReadResponseDto;
import org.ever._4ever_be_alarm.notification.domain.model.Noti;
import org.ever._4ever_be_alarm.notification.domain.model.constants.NotificationStatusEnum;
import org.ever._4ever_be_alarm.notification.domain.model.constants.SourceTypeEnum;
import org.ever._4ever_be_alarm.notification.domain.port.out.NotificationRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class NotificationJpaAdapter implements NotificationRepositoryPort {

    private final ChannelRepository channelRepository;
    private final NotificationChannelRepository notificationChannelRepository;
    private final NotificationErrorCodeRepository notificationErrorCodeRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationStatusRepository notificationStatusRepository;
    private final NotificationTargetRepository notificationTargetRepository;
    private final NotificationTemplateRepository notificationTemplateRepository;
    private final SourceRepository sourceRepository;
//    private final NotificationMapper notificationMapper;

    @Override
    @Transactional
    public Noti save(Noti alarm) {
        log.info("[JPA-SAVE] 알림 저장 시작 - notificationId: {}, targetId: {}, source: {}",
            alarm.getId(), alarm.getTargetId(), alarm.getSource());

        try {
            // Source 조회
            Source source = sourceRepository.findBySourceName(
                SourceTypeEnum.fromString(alarm.getSource().name()))
                .orElseThrow(() -> {
                    log.error("[JPA-SAVE] Source를 찾을 수 없음 - source: {}, notificationId: {}",
                        alarm.getSource(), alarm.getId());
                    return new IllegalStateException(
                        String.format("Source를 찾을 수 없습니다: %s", alarm.getSource()));
                });

            log.debug("[JPA-SAVE] Source 조회 완료 - sourceId: {}", source.getId());

            // Notification 엔티티 생성 및 저장
            Notification notification = Notification.builder()
                .id(alarm.getId())
                .title(alarm.getTitle())
                .message(alarm.getMessage())
                .referenceId(alarm.getReferenceId())
                .referenceType(alarm.getReferenceType())
                .source(source)
                .sendAt(LocalDateTime.now())
                .scheduledAt(Optional.ofNullable(alarm.getScheduledAt()).orElse(LocalDateTime.now()))
                .build();

            Notification savedNotification = notificationRepository.save(notification);
            log.debug("[JPA-SAVE] Notification 저장 완료 - notificationId: {}", savedNotification.getId());

            // NotificationStatus 조회
            NotificationStatus notificationStatus = notificationStatusRepository.findByStatusName(
                NotificationStatusEnum.PENDING)
                .orElseThrow(() -> {
                    log.error("[JPA-SAVE] NotificationStatus를 찾을 수 없음 - status: PENDING, notificationId: {}",
                        alarm.getId());
                    return new IllegalStateException("NotificationStatus를 찾을 수 없습니다: PENDING");
                });

            log.debug("[JPA-SAVE] NotificationStatus 조회 완료 - statusId: {}", 
                notificationStatus.getId());

            // NotificationTarget 생성 및 저장
            NotificationTarget target = NotificationTarget.builder()
                .notification(savedNotification)
                .notificationStatus(notificationStatus)
                .userId(alarm.getTargetId())
                .build();

            NotificationTarget savedTarget = notificationTargetRepository.save(target);
            log.debug("[JPA-SAVE] NotificationTarget 저장 완료 - targetId: {}", savedTarget.getId());

            log.info("[JPA-SAVE] 전체 알림 저장 완료 - notificationId: {}, targetId: {}",
                savedNotification.getId(), savedTarget.getId());

            // Domain 객체로 변환하여 반환
            return Noti.builder()
                .id(savedNotification.getId())
                .targetId(savedTarget.getUserId())
                .targetType(alarm.getTargetType())
                .title(savedNotification.getTitle())
                .message(savedNotification.getMessage())
                .referenceId(savedNotification.getReferenceId())
                .referenceType(savedNotification.getReferenceType())
                .source(SourceTypeEnum.fromString(savedNotification.getSource().getSourceName().name()))
                .sendAt(savedNotification.getSendAt())
                .scheduledAt(savedNotification.getScheduledAt())
                .build();

        } catch (Exception e) {
            log.error("[JPA-SAVE] 알림 저장 실패 - notificationId: {}, error: {}",
                alarm.getId(), e.getMessage(), e);
            // TODO: 예외를 다시 던져서 상위 레이어에서 처리하도록 함
            throw e;
        }
    }

    @Override
    public List<Noti> findByUserId(String userId) {
        return List.of();
    }

    @Override
    public PageResponseDto<NotificationListResponseDto> getNotificationList(
        UUID userId,
        String sortBy,
        String order,
        SourceTypeEnum source,
        int page, int size
    ) {
        // sortBy는 기본값 "createdAt", 나중에 동적으로 변경 가능하도록 파라미터 유지
        String sortField = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;

        // order는 기본값 DESC, asc가 명시적으로 지정된 경우만 ASC
        Sort.Direction direction = "asc".equalsIgnoreCase(order)
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Source 필터링: null이거나 UNKNOWN이면 전체 조회, 그 외에는 해당 source만 조회
        Page<NotificationTarget> notificationTargets;
        if (source == null || source == SourceTypeEnum.UNKNOWN) {
            notificationTargets = notificationTargetRepository.findByUserId(userId, pageable);
        } else {
            notificationTargets = notificationTargetRepository.findByUserIdAndSource(
                userId, source.name(), pageable);
        }

        // NotificationListResponseDto로 변환
        List<NotificationListResponseDto> items = notificationTargets.getContent().stream()
            .map(this::toNotificationListResponseDto)
            .collect(Collectors.toList());

        // PageResponseDto 생성
        return PageResponseDto.<NotificationListResponseDto>builder()
            .items(items)
            .page(PageDto.builder()
                .number(notificationTargets.getNumber())
                .size(notificationTargets.getSize())
                .totalElements((int) notificationTargets.getTotalElements())
                .totalPages(notificationTargets.getTotalPages())
                .hasNext(notificationTargets.hasNext())
                .build())
            .build();
    }

    @Override
    public NotificationCountResponseDto countUnreadByUserId(UUID userId) {
        return NotificationCountResponseDto.builder()
            .count(Math.toIntExact(notificationTargetRepository.countUnreadByUserId(userId)))
            .build();
    }

    @Override
    public NotificationCountResponseDto countByUserId(UUID userId) {
        return NotificationCountResponseDto.builder()
            .count(Math.toIntExact(notificationTargetRepository.countByUserId(userId)))
            .build();
    }

    @Override
    public NotificationCountResponseDto countByUserIdAndStatus(UUID userId, Boolean isRead) {
        return NotificationCountResponseDto.builder()
            .count(Math.toIntExact(
                notificationTargetRepository.countByUserIdAndIsRead(userId, isRead)))
            .build();
    }

    @Override
    @Transactional
    public NotificationReadResponseDto markAsReadList(UUID userId, List<UUID> notificationIds) {
        int totalProcessed = 0;
        LocalDateTime readAt = LocalDateTime.now();

        for (UUID notificationId : notificationIds) {
            int updated = notificationTargetRepository
                .markAsReadByUserIdAndNotificationId(userId, notificationId, readAt);
            totalProcessed += updated;
        }

        return NotificationReadResponseDto.builder()
            .processedCount(totalProcessed)
            .build();
    }

    @Override
    @Transactional
    public NotificationReadResponseDto markAsReadAll(UUID userId) {
        LocalDateTime readAt = LocalDateTime.now();
        int totalProcessed = notificationTargetRepository.markAllAsReadByUserId(userId, readAt);

        return NotificationReadResponseDto.builder()
            .processedCount(totalProcessed)
            .build();
    }

    @Override
    @Transactional
    public NotificationReadResponseDto markAsRead(UUID userId, UUID notificationId) {
        LocalDateTime readAt = LocalDateTime.now();
        int updated = notificationTargetRepository
            .markAsReadByUserIdAndNotificationId(userId, notificationId, readAt);
        return NotificationReadResponseDto.builder()
            .processedCount(updated)
            .build();
    }

    private NotificationListResponseDto toNotificationListResponseDto(NotificationTarget target) {
        var notification = target.getNotification();
        return NotificationListResponseDto.builder()
            .notificationId(notification.getId().toString())
            .notificationTitle(notification.getTitle())
            .notificationMessage(notification.getMessage())
            .linkType(
                notification.getReferenceType() != null
                    ? notification.getReferenceType().toString()
                    : null
            )
            .linkId(notification.getReferenceId() != null
                ? notification.getReferenceId().toString()
                : null
            )
            .source(String.valueOf(notification.getSource().getSourceName()))
            .createdAt(notification.getCreatedAt())
            .isRead(target.getIsRead())
            .build();
    }
}
