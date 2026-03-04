package org.ever._4ever_be_alarm.notification.adapter.jpa.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_alarm.common.entity.TimeStamp;

@Entity
@Table(name = "notification_error_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationErrorCode extends TimeStamp {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "error_code", nullable = false)
    private Integer errorCode;

    @Column(name = "error_message", nullable = false, length = 100)
    private String errorMessage;

//    @OneToMany(mappedBy = "notificationErrorCode", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private final List<NotificationLog> notificationLogs = new ArrayList<>();

    @Builder
    public NotificationErrorCode(
        Integer errorCode,
        String errorMessage
    ) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @PrePersist
    public void perPersist() {
        if (this.id == null) {
            this.id = Generators.timeBasedEpochGenerator().generate();
        }
    }
}