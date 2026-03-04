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
@Table(name = "notification_template")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationTemplate extends TimeStamp {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Column(name = "template_name", nullable = false, unique = true, length = 50)
    private String templateName;

    @Column(name = "title_template", length = 100)
    private String titleTemplate;

    @Column(name = "message_template", length = 100)
    private String messageTemplate;

    @Column(name = "variables", length = 100)
    private String variables;

    @Builder
    public NotificationTemplate(
        String templateName,
        String titleTemplate,
        String messageTemplate,
        String variables
    ) {
        this.templateName = templateName;
        this.titleTemplate = titleTemplate;
        this.messageTemplate = messageTemplate;
        this.variables = variables;
    }

    @PrePersist
    public void perPersist() {
        if (this.id == null) {
            this.id = Generators.timeBasedEpochGenerator().generate();
        }
    }
}