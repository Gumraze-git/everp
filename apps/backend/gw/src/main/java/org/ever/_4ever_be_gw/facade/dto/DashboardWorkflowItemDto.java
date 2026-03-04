package org.ever._4ever_be_gw.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대시보드 워크플로우 아이템 DTO
 * - 탭 배열 구조(tabs: [{ code, label, items }])의 items 요소를 구성하는 단일 항목입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWorkflowItemDto {
    private String itemId;          // 식별자: qoId, poId 등
    private String itemTitle;       // 내용 (ex, 고객사, 공급사, 한 줄 요약 제목)
    private String itemNumber;        // PO-2025-001, SO-2025-003
    private String name;        // 담당자 이름
    private String statusCode;  // 상태 코드 (모듈 표준 코드)
    private String date;       // ISO-8601 마감 시각
}
