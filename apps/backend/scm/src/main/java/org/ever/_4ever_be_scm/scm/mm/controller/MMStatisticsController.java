package org.ever._4ever_be_scm.scm.mm.controller;

import org.ever._4ever_be_scm.api.scm.mm.MMStatisticsApi;
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


@RestController
@RequestMapping("/scm-pp/mm")
@RequiredArgsConstructor
public class MMStatisticsController implements MMStatisticsApi {

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

    public ResponseEntity<SupplierOrderStatisticsResponseDto> getSupplierOrderStatistics(
        @PathVariable String userId
    ) {
        return ResponseEntity.ok(mmStatisticsService.getSupplierOrderStatistics(userId));
    }
}
