package org.ever._4ever_be_auth.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // 인증 서비스 토픽
    public static final String AUTH_USER_REGISTER_TOPIC = "auth-user-register";
    public static final String AUTH_USER_UPDATE_TOPIC = "auth-user-update";
    public static final String AUTH_USER_DELETE_TOPIC = "auth-user-delete";
    public static final String AUTH_USER_LOGIN_TOPIC = "auth-user-login";
    public static final String AUTH_USER_LOGOUT_TOPIC = "auth-user-logout";
    public static final String USER_EVENT_TOPIC = "user-event";  // 통합 사용자 이벤트 토픽

    // 다른 서비스 토픽 (발행/수신용)
    public static final String PAYMENT_EVENT_TOPIC = "payment-event";
    public static final String SCM_EVENT_TOPIC = "scm-event";
    public static final String BUSINESS_EVENT_TOPIC = "business-event";
    public static final String ALARM_EVENT_TOPIC = "alarm-event";

    // 고객사 등록 토픽
    public static final String CREATE_USER_TOPIC = "create-user";
    public static final String USER_CREATED_TOPIC = "user-created";
    public static final String USER_CREATION_FAILED_TOPIC = "user-creation-failed";
    public static final String USER_ROLLBACK_TOPIC = "user-rollback";
    public static final String PROCESS_COMPLETED_TOPIC = "process-completed";
    public static final String AUTH_USER_RESULT_TOPIC = "auth-user-result";
    public static final String CREATE_CUSTOMER_USER_TOPIC = "create-customer-user";
    public static final String CUSTOMER_USER_RESULT_TOPIC = "customer-user-result";
    public static final String CREATE_SUPPLIER_USER_TOPIC = "create-supplier-user";
    public static final String SUPPLIER_USER_RESULT_TOPIC = "supplier-user-result";

    @Bean
    public NewTopic authUserRegisterTopic() {
        return TopicBuilder.name(AUTH_USER_REGISTER_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic authUserUpdateTopic() {
        return TopicBuilder.name(AUTH_USER_UPDATE_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic authUserDeleteTopic() {
        return TopicBuilder.name(AUTH_USER_DELETE_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic authUserLoginTopic() {
        return TopicBuilder.name(AUTH_USER_LOGIN_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic authUserLogoutTopic() {
        return TopicBuilder.name(AUTH_USER_LOGOUT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic userEventTopic() {
        return TopicBuilder.name(USER_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder.name(PAYMENT_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic scmEventTopic() {
        return TopicBuilder.name(SCM_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic businessEventTopic() {
        return TopicBuilder.name(BUSINESS_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic alarmEventTopic() {
        return TopicBuilder.name(ALARM_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic createCustomerUserTopic() {
        return TopicBuilder.name(CREATE_CUSTOMER_USER_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic customerUserResultTopic() {
        return TopicBuilder.name(CUSTOMER_USER_RESULT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic createSupplierUserTopic() {
        return TopicBuilder.name(CREATE_SUPPLIER_USER_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic supplierUserResultTopic() {
        return TopicBuilder.name(SUPPLIER_USER_RESULT_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
