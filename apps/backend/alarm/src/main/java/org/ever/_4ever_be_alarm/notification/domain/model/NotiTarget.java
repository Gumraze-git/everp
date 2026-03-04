package org.ever._4ever_be_alarm.notification.domain.model;


import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.ever._4ever_be_alarm.notification.domain.model.constants.NotificationStatusEnum;

@Data
@Builder
@Getter
public class NotiTarget {

    private UUID id; // targetId
    private UUID notificationId;
    private NotificationStatusEnum notificationStatus;
    private Boolean isRead;
    private LocalDateTime readAt;
}