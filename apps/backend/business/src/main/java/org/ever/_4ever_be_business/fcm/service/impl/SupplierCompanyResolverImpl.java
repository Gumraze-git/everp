package org.ever._4ever_be_business.fcm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.fcm.service.SupplierCompanyResolver;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever.event.SupplierCompanyResolveRequestEvent;
import org.ever.event.SupplierCompanyResolveResultEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.SUPPLIER_COMPANY_RESOLVE_REQUEST_TOPIC;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierCompanyResolverImpl implements SupplierCompanyResolver {

    private static final long DEFAULT_TIMEOUT_SECONDS = 5;

    private final KafkaProducerService kafkaProducerService;

    private final Map<String, CompletableFuture<SupplierCompanyResolveResultEvent>> pending = new ConcurrentHashMap<>();

    @Override
    public SupplierCompanyInfo resolve(String supplierUserId) {
        String transactionId = UUID.randomUUID().toString();
        CompletableFuture<SupplierCompanyResolveResultEvent> future = new CompletableFuture<>();
        pending.put(transactionId, future);

        SupplierCompanyResolveRequestEvent event = SupplierCompanyResolveRequestEvent.builder()
                .transactionId(transactionId)
                .supplierUserId(supplierUserId)
                .timestamp(Instant.now().toEpochMilli())
                .build();

        kafkaProducerService.sendToTopic(
                SUPPLIER_COMPANY_RESOLVE_REQUEST_TOPIC,
                supplierUserId,
                event
        );

        try {
            SupplierCompanyResolveResultEvent result = future.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!result.isSuccess()) {
                throw new IllegalStateException(result.getErrorMessage());
            }
            return new SupplierCompanyInfo(result.getSupplierCompanyId(), result.getSupplierCompanyName());
        } catch (Exception e) {
            throw new IllegalStateException("공급사 회사 정보를 조회하지 못했습니다.", e);
        } finally {
            pending.remove(transactionId);
        }
    }

    public void complete(SupplierCompanyResolveResultEvent event) {
        CompletableFuture<SupplierCompanyResolveResultEvent> future = pending.remove(event.getTransactionId());
        if (future != null) {
            future.complete(event);
        }
    }
}
