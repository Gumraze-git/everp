package org.ever._4ever_be_business.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_business.common.util.UuidV7Generator;

import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_change_logs", 
       indexes = {
           @Index(name = "idx_transaction_id", columnList = "transactionId"),
           @Index(name = "idx_compensated_timestamp", columnList = "compensated, timestamp")
       })
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionChangeLog extends TimeStamp {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String transactionId;

    @Column(columnDefinition = "TEXT")
    private String changesJson;  // 전체 변경 내역을 JSON 형태로 저장

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean compensated;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UuidV7Generator.generate();
        }
    }
}
