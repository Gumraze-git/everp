package org.ever._4ever_be_scm.scm.mm.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_scm.scm.mm.dto.MMStatisticsResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.SupplierOrderStatisticsResponseDto;
import org.ever._4ever_be_scm.scm.mm.dto.ToggleCodeLabelDto;
import org.ever._4ever_be_scm.scm.mm.service.MMStatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "구매관리", description = "구매 관리 API")
@RestController
@RequestMapping("/scm-pp/mm")
@RequiredArgsConstructor
public class MMStatisticsController {

    private final MMStatisticsService mmStatisticsService;

    @GetMapping("/metrics")
    public ResponseEntity<MMStatisticsResponseDto> getMMStatistics() {
        return ResponseEntity.ok(mmStatisticsService.getMMStatistics());
    }

    @GetMapping("/purchase-requisition-status-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getPurchaseRequisitionStatusOptions() {
        return ResponseEntity.ok(List.of(
            new ToggleCodeLabelDto("전체 상태", "ALL"),
            new ToggleCodeLabelDto("승인", "APPROVAL"),
            new ToggleCodeLabelDto("대기", "PENDING"),
            new ToggleCodeLabelDto("반려", "REJECTED")
        ));
    }

    @GetMapping("/purchase-requisition-search-type-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getPurchaseRequisitionSearchTypeOptions() {
        return ResponseEntity.ok(List.of(
            new ToggleCodeLabelDto("요청자 이름", "requesterName"),
            new ToggleCodeLabelDto("부서명", "departmentName"),
            new ToggleCodeLabelDto("구매요청서 번호", "productRequestNumber")
        ));
    }

    @GetMapping("/purchase-order-status-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getPurchaseOrderStatusOptions() {
        return ResponseEntity.ok(List.of(
            new ToggleCodeLabelDto("전체 상태", "ALL"),
            new ToggleCodeLabelDto("승인", "APPROVAL"),
            new ToggleCodeLabelDto("대기", "PENDING"),
            new ToggleCodeLabelDto("반려", "REJECTED"),
            new ToggleCodeLabelDto("배송중", "DELIVERING"),
            new ToggleCodeLabelDto("배송완료", "DELIVERED")
        ));
    }

    @GetMapping("/purchase-order-search-type-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getPurchaseOrderSearchTypeOptions() {
        return ResponseEntity.ok(List.of(
            new ToggleCodeLabelDto("발주서 번호", "PurchaseOrderNumber"),
            new ToggleCodeLabelDto("공급 업체명", "SupplierCompanyName")
        ));
    }

    @GetMapping("/supplier-status-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getSupplierStatusOptions() {
        return ResponseEntity.ok(List.of(
            new ToggleCodeLabelDto("전체 상태", "ALL"),
            new ToggleCodeLabelDto("활성", "ACTIVE"),
            new ToggleCodeLabelDto("비활성", "INACTIVE")
        ));
    }

    @GetMapping("/supplier-category-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getSupplierCategoryOptions() {
        return ResponseEntity.ok(List.of(
            new ToggleCodeLabelDto("전체 카테고리", "ALL"),
            new ToggleCodeLabelDto("자재", "MATERIAL"),
            new ToggleCodeLabelDto("품목", "ITEM"),
            new ToggleCodeLabelDto("기타", "ETC")
        ));
    }

    @GetMapping("/supplier-search-type-options")
    public ResponseEntity<List<ToggleCodeLabelDto>> getSupplierSearchTypeOptions() {
        return ResponseEntity.ok(List.of(
            new ToggleCodeLabelDto("공급 업체명", "SupplierCompanyNumber"),
            new ToggleCodeLabelDto("공급 업체 번호", "SupplierCompanyName")
        ));
    }

    @GetMapping("/supplier-users/{userId}/metrics/order-counts")
    @io.swagger.v3.oas.annotations.Operation(
        summary = "공급업체별 주문 통계 조회",
        description = "공급업체 사용자 ID를 기준으로 해당 공급업체의 주문 통계를 조회합니다. 주, 월, 분기, 년 단위로 제공됩니다."
    )
    public ResponseEntity<SupplierOrderStatisticsResponseDto> getSupplierOrderStatistics(
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(mmStatisticsService.getSupplierOrderStatistics(userId));
    }
}
