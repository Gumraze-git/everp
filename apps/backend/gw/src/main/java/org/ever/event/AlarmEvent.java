package org.ever.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ever.event.alarm.AlarmType;
import org.ever.event.alarm.LinkType;
import org.ever.event.alarm.TargetType;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmEvent extends BaseEvent {

    String alarmId;
    AlarmType alarmType; // source와 구분하기 위해 alarm 명명
    String targetId;
    TargetType targetType;
    String title;
    String message;
    String linkId;
    LinkType linkType;
    LocalDateTime scheduledAt; // 예약 발송 시간(아직 미구현) -> null 이면 즉시 발송
}
