package org.ever._4ever_be_auth.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ever._4ever_be_auth.common.saga.SagaTransactionState;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "saga_transaction_status")
public class SagaTransactionStatus extends TimeStamp {

    @Id
    @Column(length = 36, nullable = false, updatable = false)
    private String transactionId;

    @Column(nullable = false, length = 20)
    private String state;

    public SagaTransactionStatus(String transactionId, SagaTransactionState state) {
        this.transactionId = transactionId;
        this.state = state.name();
    }

    public SagaTransactionState getStateEnum() {
        return SagaTransactionState.valueOf(this.state);
    }

    public void setStateEnum(SagaTransactionState state) {
        this.state = state.name();
    }
}
