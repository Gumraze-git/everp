package org.ever._4ever_be_business.hr.service;

import org.ever._4ever_be_business.hr.dto.response.PositionDetailDto;
import org.ever._4ever_be_business.hr.dto.response.PositionListItemDto;

import java.util.List;

/**
 * Position Service
 */
public interface PositionService {
    /**
     * 직급 목록 조회
     *
     * @return List<PositionListItemDto>
     */
    List<PositionListItemDto> getPositionList();

    /**
     * 직급 상세 정보 조회
     *
     * @param positionId 직급 ID
     * @return PositionDetailDto
     */
    PositionDetailDto getPositionDetail(String positionId);
}
