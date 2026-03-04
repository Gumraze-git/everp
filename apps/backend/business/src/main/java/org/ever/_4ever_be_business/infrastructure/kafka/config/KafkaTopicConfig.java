package org.ever._4ever_be_business.infrastructure.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    // BUSINESS 서비스 토픽 - 고객사 관리
    public static final String CLIENT_CREATED_TOPIC = "client-created";
    public static final String CLIENT_UPDATED_TOPIC = "client-updated";
    public static final String CLIENT_DELETED_TOPIC = "client-deleted";

    // BUSINESS 서비스 토픽 - 계약 관리
    public static final String CONTRACT_CREATED_TOPIC = "contract-created";
    public static final String CONTRACT_UPDATED_TOPIC = "contract-updated";
    public static final String CONTRACT_EXPIRED_TOPIC = "contract-expired";

    // BUSINESS 서비스 토픽 - 비즈니스 이벤트
    public static final String BUSINESS_EVENT_TOPIC = "business-event";
    public static final String BUSINESS_NOTIFICATION_TOPIC = "business-notification";

    // 다른 서비스 연동 토픽
    public static final String PAYMENT_EVENT_TOPIC = "payment-event";
    public static final String USER_EVENT_TOPIC = "user-event";
    public static final String SCM_EVENT_TOPIC = "scm-event";
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

    // ALARM 서비스 토픽
    public static final String ALARM_REQUEST_TOPIC = "alarm-request"; // 알림 요청
    public static final String ALARM_SENT_STATUS_TOPIC = "alarm-sent-status"; // 알림 발송 상태
    public static final String ALARM_SENT_TOPIC = "alarm-sent"; // 알림 발송
    public static final String ALARM_REQUEST_STATUS_TOPIC = "alarm-request-status"; // 알림 요청 상태


    // 고객사 생성 토픽
    @Bean
    public NewTopic clientCreatedTopic() {
        return TopicBuilder.name(CLIENT_CREATED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 고객사 수정 토픽
    @Bean
    public NewTopic clientUpdatedTopic() {
        return TopicBuilder.name(CLIENT_UPDATED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 고객사 삭제 토픽
    @Bean
    public NewTopic clientDeletedTopic() {
        return TopicBuilder.name(CLIENT_DELETED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 계약 생성 토픽
    @Bean
    public NewTopic contractCreatedTopic() {
        return TopicBuilder.name(CONTRACT_CREATED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 계약 수정 토픽
    @Bean
    public NewTopic contractUpdatedTopic() {
        return TopicBuilder.name(CONTRACT_UPDATED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 계약 만료 토픽
    @Bean
    public NewTopic contractExpiredTopic() {
        return TopicBuilder.name(CONTRACT_EXPIRED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 비즈니스 이벤트 토픽
    @Bean
    public NewTopic businessEventTopic() {
        return TopicBuilder.name(BUSINESS_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 비즈니스 알림 토픽
    @Bean
    public NewTopic businessNotificationTopic() {
        return TopicBuilder.name(BUSINESS_NOTIFICATION_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 결제 이벤트 토픽 (Consumer용)
    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder.name(PAYMENT_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 사용자 이벤트 토픽 (Consumer용)
    @Bean
    public NewTopic userEventTopic() {
        return TopicBuilder.name(USER_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // SCM 이벤트 토픽 (Consumer용)
    @Bean
    public NewTopic scmEventTopic() {
        return TopicBuilder.name(SCM_EVENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 알림 이벤트 토픽 (Consumer용)
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

    // 알림 요청 토픽
    @Bean
    public NewTopic alarmRequestTopic() {
        return TopicBuilder.name(ALARM_REQUEST_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 알림 발송 상태 토픽
    @Bean
    public NewTopic alarmSentStatusTopic() {
        return TopicBuilder.name(ALARM_SENT_STATUS_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 알림 발송 결과 토픽
    @Bean
    public NewTopic alarmSentTopic() {
        return TopicBuilder.name(ALARM_SENT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // 알림 요청 상태 토픽
    @Bean
    public NewTopic alarmRequestStatusTopic() {
        return TopicBuilder.name(ALARM_REQUEST_STATUS_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    // PP 모듈 - 견적 업데이트 토픽
    public static final String QUOTATION_UPDATE_TOPIC = "quotation-update";
    public static final String QUOTATION_UPDATE_COMPLETION_TOPIC = "quotation-update-completion";

    // PP 모듈 - MES 관련 토픽
    public static final String MES_START_TOPIC = "mes-start";
    public static final String MES_START_COMPLETION_TOPIC = "mes-start-completion";
    public static final String MES_COMPLETE_TOPIC = "mes-complete";
    public static final String MES_COMPLETE_COMPLETION_TOPIC = "mes-complete-completion";

    // IV 모듈 - 판매주문 상태 변경 토픽
    public static final String SALES_ORDER_STATUS_CHANGE_TOPIC = "sales-order-status-change";
    public static final String SALES_ORDER_STATUS_CHANGE_COMPLETION_TOPIC = "sales-order-status-change-completion";
    public static final String SUPPLIER_COMPANY_RESOLVE_REQUEST_TOPIC = "supplier-company-resolve-request";
    public static final String SUPPLIER_COMPANY_RESOLVE_RESULT_TOPIC = "supplier-company-resolve-result";

    // MM 모듈 - 구매주문 승인 토픽
    public static final String PURCHASE_ORDER_APPROVAL_TOPIC = "purchase-order-approval";
    public static final String PURCHASE_ORDER_APPROVAL_COMPLETION_TOPIC = "purchase-order-approval-completion";

    @Bean
    public NewTopic quotationUpdateTopic() {
        return TopicBuilder.name(QUOTATION_UPDATE_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic quotationUpdateCompletionTopic() {
        return TopicBuilder.name(QUOTATION_UPDATE_COMPLETION_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic mesStartTopic() {
        return TopicBuilder.name(MES_START_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic mesStartCompletionTopic() {
        return TopicBuilder.name(MES_START_COMPLETION_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic mesCompleteTopic() {
        return TopicBuilder.name(MES_COMPLETE_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic mesCompleteCompletionTopic() {
        return TopicBuilder.name(MES_COMPLETE_COMPLETION_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic salesOrderStatusChangeTopic() {
        return TopicBuilder.name(SALES_ORDER_STATUS_CHANGE_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic salesOrderStatusChangeCompletionTopic() {
        return TopicBuilder.name(SALES_ORDER_STATUS_CHANGE_COMPLETION_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic purchaseOrderApprovalTopic() {
        return TopicBuilder.name(PURCHASE_ORDER_APPROVAL_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic purchaseOrderApprovalCompletionTopic() {
        return TopicBuilder.name(PURCHASE_ORDER_APPROVAL_COMPLETION_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic supplierCompanyResolveRequestTopic() {
        return TopicBuilder.name(SUPPLIER_COMPANY_RESOLVE_REQUEST_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic supplierCompanyResolveResultTopic() {
        return TopicBuilder.name(SUPPLIER_COMPANY_RESOLVE_RESULT_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }
}
