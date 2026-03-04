package org.ever._4ever_be_auth.infrastructure.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class UserEvent extends BaseEvent {

    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private String role;
    private UserAction action;
    private UserStatus status;

    public enum UserAction {
        REGISTERED,
        UPDATED,
        DELETED,
        LOGIN,
        LOGOUT,
        PASSWORD_CHANGED,
        PROFILE_UPDATED
    }

    public enum UserStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        DELETED
    }
}
