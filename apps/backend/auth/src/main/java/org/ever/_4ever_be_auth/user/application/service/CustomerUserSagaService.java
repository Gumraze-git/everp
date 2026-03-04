package org.ever._4ever_be_auth.user.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.user.application.port.in.CustomerUserSagaPort;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever.event.CreateAuthUserResultEvent;
import org.ever.event.CreateCustomerUserEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerUserSagaService implements CustomerUserSagaPort {

    private final UserAccountCreationSupport accountCreationSupport;
    private final SagaRollbackService sagaRollbackService;

    @Override
    @Transactional
    public CreateAuthUserResultEvent handleCreateCustomerUser(CreateCustomerUserEvent event) {
        try {
            UserRole userRole = resolveCustomerUserRole(event);

            UserAccountCreationSupport.AccountCreationResult result =
                    accountCreationSupport.createAccount(
                            event.getTransactionId(),
                            event.getEventId(),
                            event.getUserId(),
                            event.getManagerEmail(),
                            userRole
                    );

            return CreateAuthUserResultEvent.builder()
                    .eventId(event.getEventId())
                    .transactionId(event.getTransactionId())
                    .success(true)
                    .userId(result.user().getUserId())
                    .build();
        } catch (Exception error) {
            log.error("[SAGA][CUSTOMER][FAIL] 고객사 사용자 계정 생성 실패 - txId: {}, cause: {}", event.getTransactionId(), error.getMessage(), error);
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

    private UserRole resolveCustomerUserRole(CreateCustomerUserEvent event) {
        // 현재 이벤트 스키마에서는 모두 고객사 관리자 권한으로 처리한다.
        // 향후 필요 시 companyCode 등으로 세분화 가능.
        return UserRole.CUSTOMER_ADMIN;
    }
}
