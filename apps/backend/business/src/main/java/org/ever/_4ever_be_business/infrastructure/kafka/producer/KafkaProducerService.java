package org.ever._4ever_be_business.infrastructure.kafka.producer;

import org.ever.event.*;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Producer 서비스 인터페이스
 */
public interface KafkaProducerService {

    /**
     * Payment 이벤트 발행
     */
    CompletableFuture<SendResult<String, Object>> sendPaymentEvent(String topic, PaymentEvent event);

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

    void sendCreateUserEvent(CreateUserEvent event);

    /**
     * 특정 토픽으로 이벤트 발행 (범용)
     */
    CompletableFuture<SendResult<String, Object>> sendToTopic(String topic, String key, Object event);
}
