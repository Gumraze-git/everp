package org.ever._4ever_be_alarm.notification.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.ever._4ever_be_alarm.notification.domain.model.constants.ReferenceTypeEnum;
import org.ever._4ever_be_alarm.notification.domain.model.constants.SourceTypeEnum;
import org.ever.event.alarm.TargetType;

@Data
@Builder
@Getter
public class Noti {

    private UUID id;
    private UUID targetId;
    private TargetType targetType;
    private String title;
    private String message;
    private UUID referenceId;
    private ReferenceTypeEnum referenceType;
    private SourceTypeEnum source;
    private LocalDateTime sendAt;
    private LocalDateTime scheduledAt;

    // FCM 푸시 알림 전송용 토큰 (dispatch 시점에 주입됨)
    private String fcmToken;
}