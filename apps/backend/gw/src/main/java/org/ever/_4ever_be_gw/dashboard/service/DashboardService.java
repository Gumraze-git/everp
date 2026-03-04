package org.ever._4ever_be_gw.dashboard.service;

import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.facade.dto.DashboardWorkflowResponseDto;

public interface DashboardService {
    DashboardWorkflowResponseDto getDashboardWorkflow(EverUserPrincipal userPrincipal, Integer size);
}
