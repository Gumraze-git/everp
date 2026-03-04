package org.ever._4ever_be_business.hr.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateResponseDto {
    private LocalDateTime createdAt;        // 등록 시각
    private String status;                  // 사용자의 상태(ACTIVE)
}

