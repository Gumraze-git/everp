package org.ever._4ever_be_auth.user.application.port.in;

import org.ever.event.CreateAuthUserResultEvent;
import org.ever.event.CreateSupplierUserEvent;

public interface SupplierUserSagaPort {
    CreateAuthUserResultEvent handleCreateSupplierUser(CreateSupplierUserEvent event);
}
