package org.ever._4ever_be_auth.user.application.port.in;

import org.ever._4ever_be_auth.infrastructure.kafka.event.AuthUserCompletedEvent;

public interface AuthUserSagaPort {
    Void handleAuthUserCompleted(AuthUserCompletedEvent event);
}
