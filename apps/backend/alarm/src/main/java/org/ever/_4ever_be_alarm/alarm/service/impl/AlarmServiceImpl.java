package org.ever._4ever_be_alarm.alarm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.common.exception.ErrorCode;
import org.ever._4ever_be_alarm.common.exception.AlarmException;
import org.ever._4ever_be_alarm.alarm.dto.response.AlarmResponseDto;
import org.ever._4ever_be_alarm.alarm.service.AlarmService;
import org.ever._4ever_be_alarm.alarm.vo.AlarmRequestVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmServiceImpl implements AlarmService {

    // 임시 저장소 (실제로는 DB 사용)
    private final List<AlarmResponseDto> alarms = new ArrayList<>();

    @Override
    public AlarmResponseDto processPayment(AlarmRequestVo request) {
        log.info("알람 처리 시작 - orderId: {}, userId: {}", request.getOrderId(), request.getUserId());

        // 알람 전송 방법 검증
        if (!isValidAlarmMethod(request.getAlarmMethod())) {
            throw new AlarmException(ErrorCode.INVALID_PAYMENT_METHOD,
                "지원하지 않는 알람 전송 방법입니다: " + request.getAlarmMethod());
        }

        // 알람 처리 (실제로는 알람 서비스 연동)
        String alarmId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        String status = "SENT";

        // 비즈니스 로직 결과로 DTO 생성
        AlarmResponseDto alarm = AlarmResponseDto.builder()
            .alarmId(alarmId)
            .orderId(request.getOrderId())
            .userId(request.getUserId())
            .amount(request.getAmount())
            .alarmMethod(request.getAlarmMethod())
            .status(status)
            .description(request.getDescription())
            .createdAt(now)
            .updatedAt(now)
            .build();

        alarms.add(alarm);
        log.info("알람 처리 완료 - alarmId: {}", alarm.getAlarmId());

        return alarm;
    }

    @Override
    public AlarmResponseDto getPayment(String alarmId) {
        log.info("알람 정보 조회 - alarmId: {}", alarmId);

        return alarms.stream()
            .filter(p -> p.getAlarmId().equals(alarmId))
            .findFirst()
            .orElseThrow(() -> new AlarmException(ErrorCode.PAYMENT_NOT_FOUND,
                "알람 정보를 찾을 수 없습니다: " + alarmId));
    }

    @Override
    public List<AlarmResponseDto> getAllPayments() {
        log.info("전체 알람 정보 조회 - 총 개수: {}", alarms.size());
        return new ArrayList<>(alarms);
    }

    @Override
    public AlarmResponseDto cancelPayment(String alarmId) {
        log.info("알람 취소 시작 - alarmId: {}", alarmId);

        AlarmResponseDto alarm = alarms.stream()
            .filter(p -> p.getAlarmId().equals(alarmId))
            .findFirst()
            .orElseThrow(() -> new AlarmException(ErrorCode.PAYMENT_NOT_FOUND,
                "알람 정보를 찾을 수 없습니다: " + alarmId));

        if ("CANCELLED".equals(alarm.getStatus())) {
            throw new AlarmException(ErrorCode.PAYMENT_ALREADY_CANCELLED,
                "이미 취소된 알람입니다: " + alarmId);
        }

        // 알람 취소 비즈니스 로직 처리
        alarms.removeIf(p -> p.getAlarmId().equals(alarmId));

        // 취소된 알람 정보로 DTO 생성
        AlarmResponseDto cancelledAlarm = AlarmResponseDto.builder()
            .alarmId(alarm.getAlarmId())
            .orderId(alarm.getOrderId())
            .userId(alarm.getUserId())
            .amount(alarm.getAmount())
            .alarmMethod(alarm.getAlarmMethod())
            .status("CANCELLED")
            .description(alarm.getDescription())
            .createdAt(alarm.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

        alarms.add(cancelledAlarm);

        log.info("알람 취소 완료 - alarmId: {}", alarmId);
        return cancelledAlarm;
    }

    private boolean isValidAlarmMethod(String alarmMethod) {
        return List.of("EMAIL", "SMS", "PUSH", "KAKAO").contains(alarmMethod);
    }
}
