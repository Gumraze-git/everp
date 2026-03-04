package org.ever._4ever_be_alarm.notification.domain.port.in;

import java.util.List;
import org.ever._4ever_be_alarm.common.response.PageResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationCountResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationListResponseDto;
import org.ever._4ever_be_alarm.notification.adapter.web.dto.response.NotificationReadResponseDto;

public interface NotificationQueryUseCase {

    PageResponseDto<NotificationListResponseDto> getNotificationPage(
        String userId, String sortBy, String order, String source, int page, int size
    );

    NotificationCountResponseDto getNotificationCount(String userId, String status);

    NotificationReadResponseDto markAsReadList(String userId, List<String> notificationIds);

    NotificationReadResponseDto markAsReadAll(String userId);

    NotificationReadResponseDto markAsReadOne(String userId, String notificationId);
}
