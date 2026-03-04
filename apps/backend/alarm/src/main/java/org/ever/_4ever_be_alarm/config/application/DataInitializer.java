package org.ever._4ever_be_alarm.config.application;

import com.fasterxml.uuid.Generators;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.Channel;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.Notification;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.NotificationStatus;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.NotificationTarget;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.Source;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.ChannelRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationStatusRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.NotificationTargetRepository;
import org.ever._4ever_be_alarm.notification.adapter.jpa.repository.SourceRepository;
import org.ever._4ever_be_alarm.notification.domain.model.constants.ChannelNameEnum;
import org.ever._4ever_be_alarm.notification.domain.model.constants.NotificationStatusEnum;
import org.ever._4ever_be_alarm.notification.domain.model.constants.ReferenceTypeEnum;
import org.ever._4ever_be_alarm.notification.domain.model.constants.SourceTypeEnum;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    // 시연용 고정 userId 목록 (3명의 사용자)
    private static final List<UUID> DEMO_USER_IDS = List.of(
        UUID.fromString("019a3dee-732c-79a4-916a-09336277ee92"),
        UUID.fromString("019a3e3b-5592-7541-84a9-dce035f6b424"),
        UUID.fromString("019a3df1-7843-7590-a5fd-94aa9aae7d0a")
    );
    private static final Random random = new Random();
    private final SourceRepository sourceRepository;
    private final ChannelRepository channelRepository;
    private final NotificationStatusRepository notificationStatusRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationTargetRepository notificationTargetRepository;

    @Override
    @Transactional // 모든 초기화 작업을 하나의 트랜잭션으로 묶음
    public void run(ApplicationArguments args) throws Exception {
        log.info("애플리케이션 시작... 기본 데이터 초기화를 시작합니다.");

        initSourceData();
        initChannelData();
        initNotificationStatusData();
        initNotificationErrorCodeData();
        initNotificationTemplateData();
        initDemoNotificationData();

        log.info("기본 데이터 초기화 완료.");
    }

    private void initSourceData() {
        log.info("Source 데이터 확인 중...");
        SourceTypeEnum[] sources = SourceTypeEnum.values();

        for (SourceTypeEnum source : sources) {
            if (!sourceRepository.existsBySourceName(source)) {
                sourceRepository.save(new Source(source));
                log.info("'{}' Source 추가됨.", source);
            }
        }
    }

    private void initChannelData() {
        log.info("Channel 데이터 확인 중...");
        ChannelNameEnum[] channels = ChannelNameEnum.values();

        for (ChannelNameEnum channel : channels) {
            if (!channelRepository.existsByName(channel)) {
                channelRepository.save(new Channel(channel));
                log.info("'{}' Channel 추가됨.", channel);
            }
        }
    }

    private void initNotificationStatusData() {
        log.info("NotificationStatus 데이터 확인 중...");
        NotificationStatusEnum[] statuses = NotificationStatusEnum.values();

        for (NotificationStatusEnum status : statuses) {
            if (!notificationStatusRepository.existsByStatusName(status)) {
                notificationStatusRepository.save(new NotificationStatus(status));
                log.info("'{}' NotificationStatus 추가됨.", status);
            }
        }
    }

    // TODO : Notification_Error_Code, Notification_Template 초기화 구현
    private void initNotificationErrorCodeData() {
        log.info("NotificationErrorCode 데이터 확인 중...");
        // 구현 예정
    }

    private void initNotificationTemplateData() {
        log.info("NotificationTemplate 데이터 확인 중...");
        // 구현 예정
    }

    /**
     * 시연용 Notification 데이터 30개 생성
     * 각 ReferenceTypeEnum별로 최소 1개 이상 생성
     */
    private void initDemoNotificationData() {
        log.info("시연용 Notification 데이터 생성 중...");

        // 이미 데이터가 있으면 스킵
        if (notificationRepository.count() > 0) {
            log.info("Notification 데이터가 이미 존재합니다. 시연 데이터 생성을 건너뜁니다.");
            return;
        }

        // 필요한 Source와 NotificationStatus 조회
        List<Source> sources = sourceRepository.findAll();
        NotificationStatus pendingStatus = notificationStatusRepository.findByStatusName(
                NotificationStatusEnum.PENDING)
            .orElseThrow(() -> new IllegalStateException("PENDING 상태를 찾을 수 없습니다."));

        if (sources.isEmpty()) {
            log.warn("Source 데이터가 없어 시연 데이터를 생성할 수 없습니다.");
            return;
        }

        // 시연용 알림 데이터 생성
        LocalDateTime now = LocalDateTime.now();
        int createdCount = 0;
        
        // ReferenceTypeEnum별로 생성된 알림 추적
        Set<ReferenceTypeEnum> createdReferenceTypes = new HashSet<>();
        
        // ReferenceTypeEnum 목록 (UNKNOWN 제외, null도 고려)
        List<ReferenceTypeEnum> allReferenceTypes = new ArrayList<>(
            Arrays.asList(ReferenceTypeEnum.values()));
        allReferenceTypes.remove(ReferenceTypeEnum.UNKNOWN);
        
        // 1단계: 각 ReferenceTypeEnum별로 최소 1개씩 생성
        log.info("각 ReferenceTypeEnum별 필수 알림 생성 중...");
        for (ReferenceTypeEnum refType : allReferenceTypes) {
            try {
                // ReferenceType에 맞는 Source 찾기
                SourceTypeEnum sourceType = getSourceTypeForReferenceType(refType);
                Source source = sources.stream()
                    .filter(s -> s.getSourceName() == sourceType)
                    .findFirst()
                    .orElse(sources.get(random.nextInt(sources.size()))); // 없으면 랜덤 선택
                
                // 알림 데이터 생성
                NotificationData notificationData = generateNotificationDataForReferenceType(
                    source.getSourceName(), refType, createdCount);
                
                // Notification 생성
                LocalDateTime createdAt = now.minusDays(random.nextInt(30))
                    .minusHours(random.nextInt(24));
                Notification notification = createAndSaveNotification(
                    notificationData, source, createdAt, pendingStatus);
                
                if (notification != null) {
                    createdReferenceTypes.add(refType);
                    createdCount++;
                    log.debug("ReferenceType {} 알림 생성 완료", refType);
                }
                
            } catch (Exception e) {
                log.error("ReferenceType {} 알림 생성 중 오류 발생 - error: {}", 
                    refType, e.getMessage(), e);
            }
        }
        
        // 2단계: 나머지 알림을 랜덤으로 생성하여 총 30개 맞추기
        log.info("랜덤 알림 생성 중... (현재: {}, 목표: 30개)", createdCount);
        while (createdCount < 30) {
            try {
                // 랜덤 Source 선택
                Source source = sources.get(random.nextInt(sources.size()));
                
                // Source에 맞는 알림 데이터 생성 (랜덤 ReferenceType)
                NotificationData notificationData = generateNotificationData(
                    source.getSourceName(), createdCount);
                
                // Notification 생성
                LocalDateTime createdAt = now.minusDays(random.nextInt(30))
                    .minusHours(random.nextInt(24));
                Notification notification = createAndSaveNotification(
                    notificationData, source, createdAt, pendingStatus);
                
                if (notification != null) {
                    if (notificationData.referenceType != null) {
                        createdReferenceTypes.add(notificationData.referenceType);
                    }
                    createdCount++;
                }
                
            } catch (Exception e) {
                log.error("랜덤 알림 생성 중 오류 발생 - index: {}, error: {}", 
                    createdCount, e.getMessage(), e);
                // 무한 루프 방지
                if (createdCount == 0) {
                    break;
                }
            }
        }

        log.info("시연용 Notification 데이터 생성 완료 - 생성된 알림 수: {}", createdCount);
        log.info("생성된 ReferenceType 종류: {}개", createdReferenceTypes.size());
        log.info("생성된 ReferenceType 목록: {}", createdReferenceTypes);
    }
    
    /**
     * Notification 생성 및 저장 (공통 로직)
     * 3명의 사용자 모두에게 NotificationTarget 생성
     */
    private Notification createAndSaveNotification(
        NotificationData notificationData,
        Source source,
        LocalDateTime createdAt,
        NotificationStatus pendingStatus
    ) {
        try {
            Notification notification = Notification.builder()
                .id(Generators.timeBasedEpochGenerator().generate())
                .title(notificationData.title)
                .message(notificationData.message)
                .referenceId(notificationData.referenceId)
                .referenceType(notificationData.referenceType)
                .source(source)
                .sendAt(createdAt)
                .scheduledAt(createdAt)
                .build();

            Notification savedNotification = notificationRepository.save(notification);

            // 3명의 사용자 모두에게 NotificationTarget 생성
            for (UUID userId : DEMO_USER_IDS) {
                // 각 사용자별로 읽음/안읽음 랜덤
                boolean isRead = random.nextBoolean();
                NotificationTarget target = NotificationTarget.builder()
                    .notification(savedNotification)
                    .notificationStatus(pendingStatus)
                    .userId(userId)
                    .build();

                // 읽은 알림의 경우 읽음 처리
                if (isRead) {
                    target.markAsRead();
                }

                notificationTargetRepository.save(target);
            }

            return savedNotification;
            
        } catch (Exception e) {
            log.error("Notification 저장 중 오류 발생 - error: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * ReferenceType에 맞는 SourceType 반환
     */
    private SourceTypeEnum getSourceTypeForReferenceType(ReferenceTypeEnum refType) {
        switch (refType) {
            case PURCHASE_REQUISITION:
            case PURCHASE_ORDER:
            case PR_ETC:
                return SourceTypeEnum.PR;
            case QUOTATION:
            case SALES_ORDER:
            case SD_ETC:
                return SourceTypeEnum.SD;
            case IM_ETC:
                return SourceTypeEnum.IM;
            case SALES_INVOICE:
            case PURCHASE_INVOICE:
            case FCM_ETC:
                return SourceTypeEnum.FCM;
            case HRM_ETC:
                return SourceTypeEnum.HRM;
            case ESTIMATE:
            case INSUFFICIENT_STOCK:
            case PP_ETC:
                return SourceTypeEnum.PP;
            default:
                return SourceTypeEnum.UNKNOWN;
        }
    }
    
    /**
     * 특정 ReferenceType에 맞는 알림 데이터 생성
     */
    private NotificationData generateNotificationDataForReferenceType(
        SourceTypeEnum source,
        ReferenceTypeEnum referenceType,
        int index
    ) {
        UUID referenceId = Generators.timeBasedEpochGenerator().generate();
        String title;
        String message;
        
        // ReferenceType에 맞는 제목과 메시지 생성
        switch (referenceType) {
            case PURCHASE_REQUISITION:
                title = "구매 요청서 승인 요청";
                message = String.format("구매 요청서 #%s에 대한 승인이 필요합니다. 금액: %,d원",
                    referenceId.toString().substring(0, 8), 1000000 + index * 50000);
                break;
            case PURCHASE_ORDER:
                title = "발주서 접수 알림";
                message = String.format("발주서 #%s가 생성되었습니다. 확인 부탁드립니다.",
                    referenceId.toString().substring(0, 8));
                break;
            case PR_ETC:
                title = "구매 관련 알림";
                message = String.format("구매 관련 알림 #%s입니다.", referenceId.toString().substring(0, 8));
                break;
            case QUOTATION:
                title = "견적서 생성 알림";
                message = String.format("견적서 #%s가 생성되었습니다.",
                    referenceId.toString().substring(0, 8));
                break;
            case SALES_ORDER:
                title = "새로운 주문이 접수되었습니다";
                message = String.format("고객사로부터 새로운 주문이 접수되었습니다. 주문번호: %s",
                    referenceId.toString().substring(0, 8));
                break;
            case SD_ETC:
                title = "영업 관련 알림";
                message = String.format("영업 관련 알림 #%s입니다.", referenceId.toString().substring(0, 8));
                break;
            case IM_ETC:
                title = "재고 부족 알림";
                message = String.format("품목 코드 %s의 재고가 부족합니다. 현재 재고: %d개",
                    referenceId.toString().substring(0, 8), 10 - index);
                break;
            case SALES_INVOICE:
                title = "매출 청구서 만기일 알림";
                message = String.format("매출 청구서 #%s의 만기일이 %d일 남았습니다. 금액: %,d원",
                    referenceId.toString().substring(0, 8), 30 - index, 5000000 + index * 100000);
                break;
            case PURCHASE_INVOICE:
                title = "매입 청구서 만기일 알림";
                message = String.format("매입 청구서 #%s의 만기일이 %d일 남았습니다. 금액: %,d원",
                    referenceId.toString().substring(0, 8), 30 - index, 3000000 + index * 100000);
                break;
            case FCM_ETC:
                title = "재무 관련 알림";
                message = String.format("재무 관련 알림 #%s입니다.", referenceId.toString().substring(0, 8));
                break;
            case HRM_ETC:
                String[] hrmTitles = {
                    "휴가 신청서 승인 요청",
                    "급여 명세서 발행 완료",
                    "교육 일정 안내",
                    "인사 평가 기간 안내"
                };
                title = hrmTitles[index % hrmTitles.length];
                message = String.format("%s 관련 알림입니다. 확인 부탁드립니다.", title);
                break;
            case ESTIMATE:
                title = "견적서 생성 알림";
                message = String.format("생산 견적서 #%s가 생성되었습니다.",
                    referenceId.toString().substring(0, 8));
                break;
            case INSUFFICIENT_STOCK:
                title = "가용 재고 부족 알림";
                message = String.format("생산 계획 #%s의 가용 재고가 부족합니다.",
                    referenceId.toString().substring(0, 8));
                break;
            case PP_ETC:
                title = "생산 계획 수정 요청";
                message = String.format("생산 계획 #%s에 대한 수정이 필요합니다. 납기일: %d일 후",
                    referenceId.toString().substring(0, 8), 7 + index);
                break;
            default:
                title = "시스템 알림";
                message = String.format("시스템 알림 #%d입니다.", index + 1);
                break;
        }
        
        return new NotificationData(title, message, referenceId, referenceType);
    }

    /**
     * Source에 맞는 알림 데이터 생성
     */
    private NotificationData generateNotificationData(SourceTypeEnum source, int index) {
        UUID referenceId = Generators.timeBasedEpochGenerator().generate();
        String title;
        String message;
        ReferenceTypeEnum referenceType;

        switch (source) {
            case PR: // 구매부
                referenceType = getRandomReferenceType(
                    ReferenceTypeEnum.PURCHASE_REQUISITION,
                    ReferenceTypeEnum.PURCHASE_ORDER,
                    ReferenceTypeEnum.PR_ETC
                );
                title = "구매 요청서 승인 요청";
                message = String.format("구매 요청서 #%s에 대한 승인이 필요합니다. 금액: %,d원",
                    referenceId.toString().substring(0, 8), 1000000 + index * 50000);
                break;

            case SD: // 영업부
                referenceType = getRandomReferenceType(
                    ReferenceTypeEnum.QUOTATION,
                    ReferenceTypeEnum.SALES_ORDER,
                    ReferenceTypeEnum.SD_ETC
                );
                title = "새로운 주문이 접수되었습니다";
                message = String.format("고객사로부터 새로운 주문이 접수되었습니다. 주문번호: %s",
                    referenceId.toString().substring(0, 8));
                break;

            case IM: // 재고부
                referenceType = ReferenceTypeEnum.IM_ETC;
                title = "재고 부족 알림";
                message = String.format("품목 코드 %s의 재고가 부족합니다. 현재 재고: %d개",
                    referenceId.toString().substring(0, 8), 10 - index);
                break;

            case FCM: // 재무부
                referenceType = getRandomReferenceType(
                    ReferenceTypeEnum.SALES_INVOICE,
                    ReferenceTypeEnum.PURCHASE_INVOICE,
                    ReferenceTypeEnum.FCM_ETC
                );
                title = "청구서 만기일 알림";
                message = String.format("청구서 #%s의 만기일이 %d일 남았습니다. 금액: %,d원",
                    referenceId.toString().substring(0, 8), 30 - index, 5000000 + index * 100000);
                break;

            case HRM: // 인사부
                referenceType = ReferenceTypeEnum.HRM_ETC;
                String[] hrmTitles = {
                    "휴가 신청서 승인 요청",
                    "급여 명세서 발행 완료",
                    "교육 일정 안내",
                    "인사 평가 기간 안내"
                };
                title = hrmTitles[index % hrmTitles.length];
                message = String.format("%s 관련 알림입니다. 확인 부탁드립니다.", title);
                break;

            case PP: // 생산부
                referenceType = getRandomReferenceType(
                    ReferenceTypeEnum.ESTIMATE,
                    ReferenceTypeEnum.INSUFFICIENT_STOCK,
                    ReferenceTypeEnum.PP_ETC
                );
                title = "생산 계획 수정 요청";
                message = String.format("생산 계획 #%s에 대한 수정이 필요합니다. 납기일: %d일 후",
                    referenceId.toString().substring(0, 8), 7 + index);
                break;

            case CUS: // 고객사
                referenceType = null;
                title = "주문 상태 변경 알림";
                message = String.format("주문번호 %s의 상태가 변경되었습니다.",
                    referenceId.toString().substring(0, 8));
                break;

            case SUP: // 공급사
                referenceType = null;
                title = "발주서 접수 알림";
                message = String.format("새로운 발주서 #%s가 접수되었습니다. 확인 부탁드립니다.",
                    referenceId.toString().substring(0, 8));
                break;

            default:
                referenceType = null;
                title = "시스템 알림";
                message = String.format("시스템 알림 #%d입니다.", index + 1);
                break;
        }

        return new NotificationData(title, message, referenceId, referenceType);
    }

    /**
     * 랜덤 ReferenceType 선택
     */
    private ReferenceTypeEnum getRandomReferenceType(ReferenceTypeEnum... types) {
        return types[random.nextInt(types.length)];
    }

    /**
     * 알림 데이터를 담는 내부 클래스
     */
    private static class NotificationData {

        String title;
        String message;
        UUID referenceId;
        ReferenceTypeEnum referenceType;

        NotificationData(
            String title,
            String message,
            UUID referenceId,
            ReferenceTypeEnum referenceType
        ) {
            this.title = title;
            this.message = message;
            this.referenceId = referenceId;
            this.referenceType = referenceType;
        }
    }
}