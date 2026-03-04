package org.ever._4ever_be_business.fcm.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.service.impl.SupplierCompanyResolverImpl;
import org.ever.event.SupplierCompanyResolveResultEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.SUPPLIER_COMPANY_RESOLVE_RESULT_TOPIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class SupplierCompanyResolveResultListener {

    private final SupplierCompanyResolverImpl supplierCompanyResolver;

    @KafkaListener(
            topics = SUPPLIER_COMPANY_RESOLVE_RESULT_TOPIC,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleResult(SupplierCompanyResolveResultEvent event) {
        log.info("[KAFKA][FCM] 공급사 회사 정보 응답 수신 - txId={}, userId={}, success={}",
                event.getTransactionId(), event.getSupplierUserId(), event.isSuccess());
        supplierCompanyResolver.complete(event);
    }
}
