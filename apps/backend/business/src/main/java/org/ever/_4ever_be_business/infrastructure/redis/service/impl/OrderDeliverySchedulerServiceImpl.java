package org.ever._4ever_be_business.infrastructure.redis.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.infrastructure.redis.dto.OrderDeliveryCompletionTask;
import org.ever._4ever_be_business.infrastructure.redis.service.OrderDeliverySchedulerService;
import org.ever._4ever_be_business.order.entity.Order;
import org.ever._4ever_be_business.order.entity.OrderStatus;
import org.ever._4ever_be_business.order.repository.OrderRepository;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OrderDeliverySchedulerServiceImpl implements OrderDeliverySchedulerService {

    private final RedissonClient redissonClient;
    private final OrderRepository orderRepository;

    private RBlockingQueue<OrderDeliveryCompletionTask> blockingQueue;
    private RDelayedQueue<OrderDeliveryCompletionTask> delayedQueue;
    private ExecutorService executorService;

    public OrderDeliverySchedulerServiceImpl(
            RedissonClient redissonClient,
            OrderRepository orderRepository) {
        this.redissonClient = redissonClient;
        this.orderRepository = orderRepository;
    }

    @PostConstruct
    public void init() {
        log.info("OrderDeliverySchedulerService 초기화 시작");
        blockingQueue = redissonClient.getBlockingQueue("order_delivery_completion_queue");
        delayedQueue = redissonClient.getDelayedQueue(blockingQueue);
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::startListener);
        log.info("OrderDeliverySchedulerService 초기화 완료");
    }

    @Override
    public void scheduleDeliveryCompletion(String orderId, Duration delay) {
        OrderDeliveryCompletionTask task = OrderDeliveryCompletionTask.builder()
                .orderId(orderId)
                .scheduledCompletionTime(LocalDateTime.now().plus(delay))
                .createdAt(LocalDateTime.now())
                .build();

        // 지연 시간 후 자동으로 BlockingQueue로 이동
        delayedQueue.offer(task, delay.toMillis(), TimeUnit.MILLISECONDS);

        log.info("주문 배송 완료 자동 처리 예약 완료 - orderId: {}, delay: {}초, scheduledTime: {}",
                orderId, delay.getSeconds(), task.getScheduledCompletionTime());
    }

    @Override
    public boolean cancelScheduledDelivery(String orderId) {
        boolean removed = delayedQueue.removeIf(task ->
                task.getOrderId().equals(orderId));

        if (removed) {
            log.info("주문 배송 완료 예약 취소 성공 - orderId: {}", orderId);
        } else {
            log.warn("주문 배송 완료 예약 취소 실패 (작업 없음) - orderId: {}", orderId);
        }

        return removed;
    }

    /**
     * 백그라운드 리스너 - 시간 되면 자동 실행
     */
    private void startListener() {
        log.info("주문 배송 완료 자동 처리 리스너 시작");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // 블로킹 대기 - CPU 사용 거의 없음
                OrderDeliveryCompletionTask task = blockingQueue.take();
                log.info("주문 배송 완료 자동 처리 시작 - orderId: {}, scheduledTime: {}",
                        task.getOrderId(), task.getScheduledCompletionTime());

                completeDelivery(task.getOrderId());

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("주문 배송 완료 자동 처리 리스너 중단");
                break;
            } catch (Exception e) {
                log.error("주문 배송 완료 자동 처리 실패", e);
            }
        }
    }

    /**
     * 주문 배송 완료 처리
     */
    @Transactional
    public void completeDelivery(String orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

            // 상태가 DELIVERING인 경우에만 DELIVERED로 변경
            if (order.getStatus() == OrderStatus.DELIVERING) {
                order.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);
                log.info("주문 배송 완료 처리 성공 - orderId: {}, status: DELIVERED", orderId);
            } else {
                log.warn("주문 상태가 DELIVERING이 아니어서 배송 완료 처리 생략 - orderId: {}, currentStatus: {}",
                        orderId, order.getStatus());
            }

        } catch (Exception e) {
            log.error("주문 배송 완료 처리 실패 - orderId: {}", orderId, e);
            throw e;
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("OrderDeliverySchedulerService 종료 시작");
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        log.info("OrderDeliverySchedulerService 종료 완료");
    }
}
