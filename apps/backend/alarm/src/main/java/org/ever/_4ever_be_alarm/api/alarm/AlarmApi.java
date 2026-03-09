package org.ever._4ever_be_alarm.api.alarm;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.ever._4ever_be_alarm.alarm.dto.request.AlarmRequestDto;
import org.ever._4ever_be_alarm.alarm.dto.response.AlarmResponseDto;
import org.ever._4ever_be_alarm.api.common.ApiServerErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "결제", description = "결제 API")
@ApiServerErrorResponse
public interface AlarmApi {

    @Operation(summary = "결제 생성", description = "결제 요청을 생성합니다.")
    ResponseEntity<AlarmResponseDto> createPayment(@RequestBody AlarmRequestDto requestDto);

    @Operation(summary = "결제 상세 조회", description = "결제 정보를 단건 조회합니다.")
    ResponseEntity<AlarmResponseDto> getPayment(@PathVariable String paymentId);

    @Operation(summary = "전체 결제 조회", description = "전체 결제 목록을 조회합니다.")
    ResponseEntity<List<AlarmResponseDto>> getAllPayments();

    @Operation(summary = "결제 취소", description = "기존 결제를 취소합니다.")
    ResponseEntity<AlarmResponseDto> cancelPayment(@PathVariable String paymentId);

    @Operation(summary = "결제 서비스 헬스 체크", description = "결제 서비스 상태를 확인합니다.")
    ResponseEntity<String> healthCheck();
}
