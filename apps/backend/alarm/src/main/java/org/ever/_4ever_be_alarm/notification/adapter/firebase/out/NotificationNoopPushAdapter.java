package org.ever._4ever_be_alarm.notification.adapter.firebase.out;

import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.notification.domain.model.Noti;
import org.ever._4ever_be_alarm.notification.domain.port.out.NotificationDispatchPort;

@Slf4j
public class NotificationNoopPushAdapter implements NotificationDispatchPort {

    @Override
    public void dispatch(Noti alarm) {
        log.info(
            "[DISPATCH-PUSH:NOOP] fcm.enabled=false로 설정되어 푸시 전송을 건너뜁니다. notificationId={}, targetId={}",
            alarm.getId(),
            alarm.getTargetId()
        );
    }
}
