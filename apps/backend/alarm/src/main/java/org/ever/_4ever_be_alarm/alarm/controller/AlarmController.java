package org.ever._4ever_be_alarm.alarm.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_alarm.alarm.dto.request.AlarmRequestDto;
import org.ever._4ever_be_alarm.alarm.dto.response.AlarmResponseDto;
import org.ever._4ever_be_alarm.alarm.service.AlarmService;
import org.ever._4ever_be_alarm.alarm.vo.AlarmRequestVo;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService paymentService;

    @PostMapping
    public ResponseEntity<AlarmResponseDto> createPayment(@Valid @RequestBody AlarmRequestDto requestDto) {
        log.info("결제 요청 수신 - orderId: {}, userId: {}", requestDto.getOrderId(), requestDto.getUserId());

        AlarmRequestVo requestVo = new AlarmRequestVo(
            requestDto.getOrderId(),
            requestDto.getUserId(),
            requestDto.getAmount(),
            requestDto.getAlarmMethod(),
            requestDto.getDescription()
        );

        AlarmResponseDto responseDto = paymentService.processPayment(requestVo);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<AlarmResponseDto> getPayment(@PathVariable String paymentId) {
        log.info("결제 정보 조회 요청 - paymentId: {}", paymentId);
        AlarmResponseDto responseDto = paymentService.getPayment(paymentId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping
    public ResponseEntity<List<AlarmResponseDto>> getAllPayments() {
        log.info("전체 결제 정보 조회 요청");
        List<AlarmResponseDto> responseDtoList = paymentService.getAllPayments();
        return ResponseEntity.ok(responseDtoList);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<AlarmResponseDto> cancelPayment(@PathVariable String paymentId) {
        log.info("결제 취소 요청 - paymentId: {}", paymentId);
        AlarmResponseDto responseDto = paymentService.cancelPayment(paymentId);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is running");
    }
}
