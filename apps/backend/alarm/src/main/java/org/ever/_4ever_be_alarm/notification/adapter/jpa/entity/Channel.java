package org.ever._4ever_be_alarm.notification.adapter.jpa.entity;

import com.fasterxml.uuid.Generators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_alarm.common.entity.TimeStamp;
import org.ever._4ever_be_alarm.notification.domain.model.constants.ChannelNameEnum;

@Entity
@Table(name = "channel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends TimeStamp {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_name", nullable = false, unique = true, length = 30)
    private ChannelNameEnum name;

//    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<NotificationChannel> notificationChannels = new ArrayList<>();

    @Builder
    public Channel(ChannelNameEnum name) {
        this.name = name;
    }

    @PrePersist
    public void perPersist() {
        if (this.id == null) {
            this.id = Generators.timeBasedEpochGenerator().generate();
        }
    }
}
