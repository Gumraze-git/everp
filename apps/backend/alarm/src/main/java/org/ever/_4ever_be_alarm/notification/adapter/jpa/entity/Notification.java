package org.ever._4ever_be_alarm.notification.adapter.jpa.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_alarm.common.entity.TimeStamp;
import org.ever._4ever_be_alarm.notification.domain.model.constants.ReferenceTypeEnum;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends TimeStamp {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "reference_id", columnDefinition = "uuid")
    private UUID referenceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_type", length = 50)
    private ReferenceTypeEnum referenceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private Source source;

    @Column(name = "send_at")
    private LocalDateTime sendAt;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

//    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private final List<NotificationChannel> notificationChannels = new ArrayList<>();
//    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private final List<NotificationTarget> notificationTargets = new ArrayList<>();
//    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private final List<NotificationLog> notificationLogs = new ArrayList<>();

    @Builder
    public Notification(
        UUID id, // Notification ID를 외부에서 주입받을 수 있도록 수정
        String title,
        String message,
        UUID referenceId,
        ReferenceTypeEnum referenceType,
        Source source,
        LocalDateTime sendAt,
        LocalDateTime scheduledAt
    ) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.referenceId = referenceId;
        this.referenceType = referenceType;
        this.source = source;
        this.sendAt = sendAt;
        this.scheduledAt = scheduledAt;
    }

    public void updateScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public void updateSendAt(LocalDateTime sendAt) {
        this.sendAt = sendAt;
    }

    @PrePersist
    public void perPersist() {
        if (this.id == null) {
            this.id = Generators.timeBasedEpochGenerator().generate();
        }
    }
}