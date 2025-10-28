package org.ever._4ever_be_auth.user.application.port.in;

import org.ever.event.CreateAuthUserEvent;
import org.ever.event.CreateAuthUserResultEvent;

public interface AuthUserSagaPort {

    CreateAuthUserResultEvent handleCreateAuthUser(CreateAuthUserEvent event);
}
