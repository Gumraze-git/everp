package org.ever._4ever_be_business.infrastructure.kafka.consumer;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.saga.SagaTransactionManager;
import org.ever._4ever_be_business.common.util.UuidV7Generator;
import org.ever._4ever_be_business.company.entity.CustomerCompany;
import org.ever._4ever_be_business.hr.entity.CustomerUser;
import org.ever._4ever_be_business.hr.repository.CustomerUserRepository;
import org.ever._4ever_be_business.infrastructure.kafka.producer.KafkaProducerService;
import org.ever._4ever_be_business.infrastructure.redis.service.OrderDeliverySchedulerService;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.entity.OrderStatus;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.ever.event.AlarmEvent;
import org.ever.event.SalesOrderStatusChangeEvent;
import org.ever.event.SalesOrderStatusChangeCompletionEvent;
import org.ever.event.alarm.AlarmType;
import org.ever.event.alarm.LinkType;
import org.ever.event.alarm.SourceType;
import org.ever.event.alarm.TargetType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.SALES_ORDER_STATUS_CHANGE_COMPLETION_TOPIC;
import static org.ever._4ever_be_business.infrastructure.kafka.config.KafkaTopicConfig.SALES_ORDER_STATUS_CHANGE_TOPIC;

/**
 * 판매주문 상태 변경 이벤트 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SalesOrderStatusChangeListener {

    private final SagaTransactionManager sagaTransactionManager;
    private final OrderRepository orderRepository;
    private final KafkaProducerService kafkaProducerService;
    private final OrderDeliverySchedulerService orderDeliverySchedulerService;
    private final CustomerUserRepository customerUserRepository;

    @KafkaListener(topics = SALES_ORDER_STATUS_CHANGE_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void handleSalesOrderStatusChange(SalesOrderStatusChangeEvent event, Acknowledgment acknowledgment) {
        log.info("판매주문 상태 변경 이벤트 수신: transactionId={}, salesOrderId={}",
                event.getTransactionId(), event.getSalesOrderId());

        try {
            // Saga 트랜잭션으로 실행
            sagaTransactionManager.executeSagaWithId(event.getTransactionId(), () -> {
                // 1. SalesOrderId로 Order 조회
                Order order = orderRepository.findById(event.getSalesOrderId()).orElseThrow(() ->
                        new RuntimeException("Order not found: " + event.getSalesOrderId()));

                // 2. Order 상태를 DELIVERING으로 변경
                order.setStatus(OrderStatus.DELIVERING);
                orderRepository.save(order);

                log.info("Order 상태 업데이트 완료: orderId={}, status=DELIVERING", order.getId());

                // 3. 자동 배송 완료 예약
                scheduleAutoDeliveryCompletion(order);

                // TODO 알람 필요 : 주문서 상태 변경 -> 고객사
                log.info("[ALARM] 주문서 상태 변경 알림 생성 - : {}", order.getId());
                String targetId = order.getCustomerUserId();
                AlarmEvent alarmEventForCreate = AlarmEvent.builder()
                    .eventId(UuidV7Generator.generate())
                    .eventType(AlarmEvent.class.getName())
                    .timestamp(LocalDateTime.now())
                    .source(SourceType.BUSINESS.name())
                    .alarmId(UuidV7Generator.generate())
                    .alarmType(AlarmType.SD)
                    .targetId(targetId)
                    .targetType(TargetType.CUSTOMER)
                    .title("주문서 상태 변경")
                    .message("해당 주문서의 물품들이 출하되었습니다. 주문서 번호 = " + order.getOrderCode())
                    .linkId(order.getId())
                    .linkType(LinkType.SALES_ORDER)
                    .scheduledAt(null)
                    .build();

                log.info("[ALARM] 알림 요청 전송 준비 - alarmId: {}, targetId: {}, targetType: {}, linkType: {}",
                    alarmEventForCreate.getAlarmId(), targetId, alarmEventForCreate.getTargetType(),
                    alarmEventForCreate.getLinkType());
                kafkaProducerService.sendAlarmEvent(alarmEventForCreate)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("[ALARM] 알림 요청 전송 실패 - alarmId: {}, targetId: {}, error: {}",
                                alarmEventForCreate.getAlarmId(), targetId, ex.getMessage(), ex);
                        } else if (result != null) {
                            log.info("[ALARM] 알림 요청 전송 성공 - topic: {}, partition: {}, offset: {}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                        } else {
                            log.warn("[ALARM] 알림 요청 전송 결과가 null 입니다 - alarmId: {}, targetId: {}",
                                alarmEventForCreate.getAlarmId(), targetId);
                        }
                    });

                return null;
            });

            // 3. 완료 이벤트 발송
            SalesOrderStatusChangeCompletionEvent completionEvent = SalesOrderStatusChangeCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .salesOrderId(event.getSalesOrderId())
                    .success(true)
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(SALES_ORDER_STATUS_CHANGE_COMPLETION_TOPIC,
                    event.getSalesOrderId(), completionEvent);

            log.info("판매주문 상태 변경 완료 이벤트 발송: transactionId={}", event.getTransactionId());

            acknowledgment.acknowledge();

        } catch (Exception e) {
            log.error("판매주문 상태 변경 처리 실패: transactionId={}, salesOrderId={}",
                    event.getTransactionId(), event.getSalesOrderId(), e);

            // 실패 이벤트 발송
            SalesOrderStatusChangeCompletionEvent completionEvent = SalesOrderStatusChangeCompletionEvent.builder()
                    .transactionId(event.getTransactionId())
                    .salesOrderId(event.getSalesOrderId())
                    .success(false)
                    .errorMessage(e.getMessage())
                    .timestamp(System.currentTimeMillis())
                    .build();

            kafkaProducerService.sendToTopic(SALES_ORDER_STATUS_CHANGE_COMPLETION_TOPIC,
                    event.getSalesOrderId(), completionEvent);

            acknowledgment.acknowledge();
        }
    }

    /**
     * 주문 자동 배송 완료 예약
     */
    private void scheduleAutoDeliveryCompletion(Order order) {
        try {
            // 1. CustomerUser 조회
            CustomerUser customerUser = customerUserRepository.findByUserId(order.getCustomerUserId())
                    .orElse(null);

            if (customerUser == null) {
                log.warn("CustomerUser를 찾을 수 없어 자동 배송 완료 예약 생략 - customerUserId: {}", order.getCustomerUserId());
                return;
            }

            // 2. CustomerCompany 조회
            CustomerCompany customerCompany = customerUser.getCustomerCompany();
            if (customerCompany == null) {
                log.warn("CustomerCompany를 찾을 수 없어 자동 배송 완료 예약 생략 - customerUserId: {}", order.getCustomerUserId());
                return;
            }

            // 3. deliveryLeadTime 조회
            Duration deliveryLeadTime = customerCompany.getDeliveryLeadTime();
            if (deliveryLeadTime == null) {
                log.warn("deliveryLeadTime이 설정되지 않아 자동 배송 완료 예약 생략 - orderId: {}, customerCompany: {}",
                        order.getId(), customerCompany.getCompanyName());
                return;
            }

            // 4. 자동 배송 완료 예약
            orderDeliverySchedulerService.scheduleDeliveryCompletion(order.getId(), deliveryLeadTime);
            log.info("주문 자동 배송 완료 예약 성공 - orderId: {}, deliveryLeadTime: {}초",
                    order.getId(), deliveryLeadTime.getSeconds());

        } catch (Exception e) {
            log.error("주문 자동 배송 완료 예약 실패 - orderId: {}", order.getId(), e);
            // 예약 실패는 치명적이지 않으므로 예외를 throw하지 않음
        }
    }
}
