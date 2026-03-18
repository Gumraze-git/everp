package org.ever._4ever_be_gw.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 대시보드 워크플로우 단일 탭 DTO
 * - 프론트가 추론하지 않도록 탭 식별자와 표시 라벨을 모두 포함합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWorkflowTabDto {
    private String tabId; // 탭 식별자
    private String label; // 탭 라벨
    private List<DashboardWorkflowItemDto> items; // 탭 항목 목록
}
