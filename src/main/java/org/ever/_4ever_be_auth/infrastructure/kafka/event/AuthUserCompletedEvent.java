package org.ever._4ever_be_auth.infrastructure.kafka.event;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_auth.common.response.ApiResponse;
import org.ever._4ever_be_auth.user.dto.response.EmployeeCreateResponseDto;
import org.springframework.http.HttpStatus;

// 내부 사용자 계정 생성 사가 완료 이벤트
// success=true: 안증 서비스에서 계정 생성이 끝났음을 의미함.
// success=false: 인증 서비스 단계에서 실패했음을 의미하며 failureReason 확인함.

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserCompletedEvent {
    // 공통 사가 메타데이터
    private String eventId;
    private String transactionId;
    private boolean success;

    // 성공 이벤트 데이터
    private String userId;
    private String email;
    private String departmentName;
    private String positionName;

    // 실패 이벤트 데이터
    private String failureReason;

    /**
     * 컨트롤러 응답으로 전달할 DTO 생성
     * - 프로젝트 상황에 맞춰 필드 구성 변경 가능
     */
    public ApiResponse<EmployeeCreateResponseDto> toApiResponse() {
        EmployeeCreateResponseDto payload = EmployeeCreateResponseDto.builder()
                .userId(userId)
                .email(email)
                .departmentName(departmentName)
                .positionName(positionName)
                .build();

        return ApiResponse.success(
                payload,
                "[SAGA][SUCCESS] 내부 사용자 계정 생성 완료",
                HttpStatus.OK
        );
    }

}
