package org.ever._4ever_be_alarm.notification.domain.port.out;


import org.ever._4ever_be_alarm.notification.domain.model.Noti;

public interface NotificationDispatchPort {

    void dispatch(Noti alarm); // 예: 푸시 알림, 이메일, SMS 등
}
