package org.ever._4ever_be_alarm.notification.domain.port.out;

import java.util.List;
import java.util.UUID;
import org.ever._4ever_be_alarm.common.response.PageResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationReadResponseDto;
import org.ever._4ever_be_alarm.notification.domain.model.Noti;
import org.ever._4ever_be_alarm.notification.domain.model.constants.SourceTypeEnum;

public interface NotificationRepositoryPort {

    Noti save(Noti alarm);

    List<Noti> findByUserId(String userId);

    PageResponseDto<NotificationListResponseDto> getNotificationList(
        UUID userId, String sortBy, String order, SourceTypeEnum source, int page, int size
    );

    NotificationCountResponseDto countUnreadByUserId(UUID userId);

    NotificationCountResponseDto countByUserId(UUID userId);

    NotificationCountResponseDto countByUserIdAndStatus(UUID userId, Boolean isRead);

    NotificationReadResponseDto markAsReadList(UUID userId, List<UUID> notificationIds);

    NotificationReadResponseDto markAsReadAll(UUID userId);

    NotificationReadResponseDto markAsRead(UUID userId, UUID notificationId);
}
