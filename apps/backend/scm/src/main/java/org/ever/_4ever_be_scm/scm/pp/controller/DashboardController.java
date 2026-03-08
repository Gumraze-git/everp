package org.ever._4ever_be_scm.scm.pp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_scm.scm.pp.service.DashboardService;
import org.ever._4ever_be_scm.scm.pp.service.dto.DashboardWorkflowItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/scm-pp")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/mm/supplier-users/{userId}/workflow-items/purchase-orders")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getSupplierPurchaseOrders(
            @PathVariable String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getSupplierPurchaseOrders(userId, size));
    }

    @GetMapping("/mm/internal-users/{userId}/workflow-items/purchase-requests")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getPurchaseRequests(
            @PathVariable String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getPurchaseRequests(userId, size));
    }

    @GetMapping("/mm/workflow-items/purchase-orders")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getMmPurchaseOrders(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getMmPurchaseOrders(size));
    }

    @GetMapping("/mm/workflow-items/purchase-requests")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getPurchaseRequestsOverall(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getPurchaseRequestsOverall(size));
    }

    @GetMapping("/iv/workflow-items/inbound")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getInboundDeliveries(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getInboundDeliveries(size));
    }

    @GetMapping("/iv/workflow-items/outbound")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getOutboundDeliveries(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getOutboundDeliveries(size));
    }
}
