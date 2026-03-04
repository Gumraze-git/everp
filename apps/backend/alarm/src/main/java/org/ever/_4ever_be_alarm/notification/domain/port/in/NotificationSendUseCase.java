package org.ever._4ever_be_alarm.notification.domain.port.in;

import java.util.UUID;
import org.ever.event.AlarmEvent;
import org.ever.event.StatusEvent;

public interface NotificationSendUseCase {

//    void sendNotification(AlarmSentEvent event);

    /**
     * 알림 생성 및 발송
     */
    UUID createNotification(AlarmEvent event);

    void updateNotificationStatus(StatusEvent event);


}
