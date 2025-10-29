package org.ever._4ever_be_auth.common.saga;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_auth.common.entity.SagaTransactionStatus;
import org.ever._4ever_be_auth.common.repository.SagaTransactionStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaTransactionStatusService {

    private final SagaTransactionStatusRepository repository;

    @Transactional
    public boolean startProcessing(String transactionId) {
        Optional<SagaTransactionStatus> existing = repository.findById(transactionId);
        if (existing.isPresent()) {
            SagaTransactionState current = existing.get().getStateEnum();
            if (current == SagaTransactionState.PROCESSING) {
                log.warn("[SAGA][STATUS] 트랜잭션 {} 은 이미 처리 중 상태입니다. 재실행을 건너뜁니다.", transactionId);
                return false;
            }
            if (current == SagaTransactionState.COMPLETED || current == SagaTransactionState.FAILED) {
                log.info("[SAGA][STATUS] 트랜잭션 {} 은 이미 {} 상태입니다. 이벤트를 무시합니다.", transactionId, current);
                return false;
            }
        }

        SagaTransactionStatus status = existing.orElseGet(() -> new SagaTransactionStatus(transactionId, SagaTransactionState.PROCESSING));
        status.setStateEnum(SagaTransactionState.PROCESSING);
        repository.save(status);
        return true;
    }

    @Transactional
    public void markCompleted(String transactionId) {
        updateStatus(transactionId, SagaTransactionState.COMPLETED);
    }

    @Transactional
    public void markFailed(String transactionId) {
        updateStatus(transactionId, SagaTransactionState.FAILED);
    }

    private void updateStatus(String transactionId, SagaTransactionState state) {
        SagaTransactionStatus status = repository.findById(transactionId)
                .orElseGet(() -> new SagaTransactionStatus(transactionId, state));
        status.setStateEnum(state);
        repository.save(status);
    }
}
