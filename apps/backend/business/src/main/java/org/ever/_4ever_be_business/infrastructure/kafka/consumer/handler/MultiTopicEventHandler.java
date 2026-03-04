package org.ever._4ever_be_business.infrastructure.kafka.consumer.handler;

import org.ever.event.AlarmEvent;
import org.ever.event.BusinessEvent;
import org.ever.event.ScmEvent;
import org.ever.event.UserEvent;

/**
 * Multi Topic 이벤트 처리 핸들러 인터페이스
 */
public interface MultiTopicEventHandler {

    /**
     * User 이벤트 처리
     */
    void handleUserEvent(UserEvent event);

    /**
     * SCM 이벤트 처리
     */
    void handleScmEvent(ScmEvent event);

    /**
     * Business 이벤트 처리
     */
    void handleBusinessEvent(BusinessEvent event);

    /**
     * Alarm 이벤트 처리
     */
    void handleAlarmEvent(AlarmEvent event);
}
