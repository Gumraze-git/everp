package org.ever._4ever_be_scm.scm.pp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_scm.scm.pp.service.DashboardService;
import org.ever._4ever_be_scm.scm.pp.service.dto.DashboardWorkflowItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/scm-pp/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/purchase-orders/supplier")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getSupplierPurchaseOrders(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getSupplierPurchaseOrders(userId, size));
    }

    @GetMapping("/purchase-requests")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getPurchaseRequests(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getPurchaseRequests(userId, size));
    }

    @GetMapping("/purchase-orders/mm")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getMmPurchaseOrders(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getMmPurchaseOrders(size));
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getPurchaseOrdersOverall(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getMmPurchaseOrders(size));
    }

    @GetMapping("/purchase-requests/company")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getPurchaseRequestsOverall(
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getPurchaseRequestsOverall(size));
    }

    @GetMapping("/inbound")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getInboundDeliveries(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getInboundDeliveries(userId, size));
    }

    @GetMapping("/outbound")
    public ResponseEntity<List<DashboardWorkflowItemDto>> getOutboundDeliveries(
            @RequestParam("userId") String userId,
            @RequestParam(value = "size", defaultValue = "5") int size
    ) {
        return ResponseEntity.ok(dashboardService.getOutboundDeliveries(userId, size));
    }
}
