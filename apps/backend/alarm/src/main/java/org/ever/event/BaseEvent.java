package org.ever.event;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEvent {

    private String eventId;             // 이벤트 고유 ID - UUID
    private String eventType;           // 이벤트 타입 클래스명 - AlarmEvent, AlarmStatusEvent 등
    private LocalDateTime timestamp;    // 이벤트  발생 시간
    private String source;              // 이벤트 발생 출처 - 서비스명 등 ALARM, USER 등

}
