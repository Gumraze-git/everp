package org.ever._4ever_be_gw.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 대시보드 워크플로우 단일 탭 DTO
 * - code/label과 해당 탭의 items(최대 5개)를 포함합니다. <br>
 * 탭 코드<br>
 * - PO: 발주 프로세스 <br>
 * - AP: 매입 프로세스 <br>
 * - AR: 매출 프로세스 <br>
 * - SO: 주문 프로세스 <br>
 * - PR: 구매 프로세스 <br>
 * - ATT: 근태 프로세스 <br>
 * - LV: 휴가 프로세스(Leave) <br>
 * - QT: 견적 프로세스 <br>
 * - MES: 생산 프로세스 <br>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWorkflowTabDto {
    private String tabCode; // 탭 코드
    private List<DashboardWorkflowItemDto> items; // 탭 항목 목록
}