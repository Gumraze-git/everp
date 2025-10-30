package org.ever._4ever_be_auth.user.application.port.in;

import org.ever.event.CreateAuthUserResultEvent;
import org.ever.event.CreateCustomerUserEvent;

public interface CustomerUserSagaPort {
    CreateAuthUserResultEvent handleCreateCustomerUser(CreateCustomerUserEvent event);
}
