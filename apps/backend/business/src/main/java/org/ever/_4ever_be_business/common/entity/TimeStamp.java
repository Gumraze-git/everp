package org.ever._4ever_be_business.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class TimeStamp {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 글로벌 트랜잭션 ID (분산 트랜잭션 추적용)
     * Saga 패턴에서 여러 서비스에 걸친 트랜잭션을 추적하는 데 사용
     */
    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    /**
     * 글로벌 트랜잭션 ID를 설정합니다.
     * 분산 트랜잭션 추적을 위해 Saga 패턴에서 사용됩니다.
     *
     * @param transactionId 글로벌 트랜잭션 ID (UUID 권장)
     */
    public void assignTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
