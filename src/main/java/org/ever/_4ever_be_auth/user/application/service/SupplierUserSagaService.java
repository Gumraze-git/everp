package org.ever._4ever_be_auth.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.user.application.port.in.SupplierUserSagaPort;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever.event.CreateAuthUserResultEvent;
import org.ever.event.CreateSupplierUserEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierUserSagaService implements SupplierUserSagaPort {

    private final UserAccountCreationSupport accountCreationSupport;
    private final SagaRollbackService sagaRollbackService;

    @Override
    @Transactional
    public CreateAuthUserResultEvent handleCreateSupplierUser(CreateSupplierUserEvent event) {
        try {
            UserAccountCreationSupport.AccountCreationResult result =
                    accountCreationSupport.createAccount(
                            event.getTransactionId(),
                            event.getEventId(),
                            event.getUserId(),
                            event.getManagerEmail(),
                            UserRole.SUPPLIER_ADMIN
                    );

            return CreateAuthUserResultEvent.builder()
                    .eventId(event.getEventId())
                    .transactionId(event.getTransactionId())
                    .success(true)
                    .userId(result.user().getUserId())
                    .build();
        } catch (Exception error) {
            log.error(\"[SAGA][SUPPLIER][FAIL] 공급사 사용자 계정 생성 실패 - txId: {}, cause: {}\", event.getTransactionId(), error.getMessage(), error);
                    sagaRollbackService.rollbackUser(event.getUserId(), event.getTransactionId());
            return CreateAuthUserResultEvent.builder()
                    .eventId(event.getEventId())
                    .transactionId(event.getTransactionId())
                    .success(false)
                    .userId(event.getUserId())
                    .failureReason(error.getMessage())
                    .build();
        }
    }
}
