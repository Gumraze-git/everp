package org.ever._4ever_be_business.sd.service;

import org.ever._4ever_be_business.sd.dto.response.DashboardWorkflowItemDto;

import java.util.List;

public interface DashboardOrderService {
    List<DashboardWorkflowItemDto> getAllOrders(int size);
}
