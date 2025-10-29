package org.ever._4ever_be_auth.common.repository;

import org.ever._4ever_be_auth.common.entity.SagaTransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaTransactionStatusRepository extends JpaRepository<SagaTransactionStatus, String> {
}
