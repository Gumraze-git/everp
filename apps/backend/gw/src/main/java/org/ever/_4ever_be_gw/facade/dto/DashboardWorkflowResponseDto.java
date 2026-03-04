package org.ever._4ever_be_gw.facade.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardWorkflowResponseDto {
    @Size(min = 2, max = 2)
    private List<DashboardWorkflowTabDto> tabs;
}
