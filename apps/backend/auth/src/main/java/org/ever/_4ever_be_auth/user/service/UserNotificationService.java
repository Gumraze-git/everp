package org.ever._4ever_be_auth.user.service;

public interface UserNotificationService {

    void sendUserNotification(String contactEmail, String loginEmail, String randomPassword);
}
