package org.ever._4ever_be_alarm.notification.domain.port.config;

import com.google.firebase.messaging.FirebaseMessaging;
import org.ever._4ever_be_alarm.notification.adapter.firebase.out.NotificationNoopPushAdapter;
import org.ever._4ever_be_alarm.notification.adapter.firebase.out.NotificationPushAdapter;
import org.ever._4ever_be_alarm.notification.adapter.kafka.out.NotificationEventProducerAdapter;
import org.ever._4ever_be_alarm.notification.domain.port.out.NotificationDispatchPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class NotificationDispatchConfig {

    /**
     * SSE 발송 전략 빈 정의
     */
    @Bean(name = "sse")
    public NotificationDispatchPort sseDispatchPort(KafkaTemplate<String, Object> kafkaTemplate) {
        return new NotificationEventProducerAdapter(kafkaTemplate);
    }

    /**
     * APP PUSH 발송 전략 빈 정의
     */
    @Bean(name = "app_push")
    @ConditionalOnProperty(name = "fcm.enabled", havingValue = "true", matchIfMissing = true)
    public NotificationDispatchPort pushDispatchPort(FirebaseMessaging firebaseMessaging) {
        return new NotificationPushAdapter(firebaseMessaging);
    }

    /**
     * 로컬 환경에서 FCM 비활성화 시 no-op 발송 전략
     */
    @Bean(name = "app_push")
    @ConditionalOnProperty(name = "fcm.enabled", havingValue = "false")
    public NotificationDispatchPort noopPushDispatchPort() {
        return new NotificationNoopPushAdapter();
    }

    // 참고: 빈 이름은 DispatchStrategy의 beanName 값과 일치해야 합니다.
    // DispatchStrategy.SSE.getBeanName() => "sse"
    // DispatchStrategy.APP_PUSH.getBeanName() => "app_push"
}
