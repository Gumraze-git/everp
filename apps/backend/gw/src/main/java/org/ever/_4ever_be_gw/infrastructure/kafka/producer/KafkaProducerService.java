package org.ever._4ever_be_gw.infrastructure.kafka.producer;

import java.util.concurrent.CompletableFuture;
import org.ever.event.AlarmEvent;
import org.ever.event.BusinessEvent;
import org.ever.event.ScmEvent;
import org.ever.event.UserEvent;
import org.springframework.kafka.support.SendResult;

/**
 * Kafka Producer 서비스 인터페이스
 */
public interface KafkaProducerService {

    /**
     * User 서비스로 이벤트 발행
     */
    CompletableFuture<SendResult<String, Object>> sendUserEvent(UserEvent event);

    /**
     * SCM 서비스로 이벤트 발행
     */
    CompletableFuture<SendResult<String, Object>> sendScmEvent(ScmEvent event);

    /**
     * Business 서비스로 이벤트 발행
     */
    CompletableFuture<SendResult<String, Object>> sendBusinessEvent(BusinessEvent event);

    /**
     * Alarm 서비스로 이벤트 발행
     */
    CompletableFuture<SendResult<String, Object>> sendAlarmEvent(AlarmEvent event);

    /**
     * 동기 방식 이벤트 발행
     */
    void sendEventSync(String topic, String key, Object event);
}
