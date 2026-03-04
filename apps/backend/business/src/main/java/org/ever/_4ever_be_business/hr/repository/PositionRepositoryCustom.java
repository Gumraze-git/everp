package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.dto.response.PositionDetailDto;
import org.ever._4ever_be_business.hr.dto.response.PositionListItemDto;

import java.util.List;
import java.util.Optional;

/**
 * Position Repository Custom
 */
public interface PositionRepositoryCustom {
    /**
     * 직급 목록 조회 (헤드카운트 포함)
     *
     * @return List<PositionListItemDto>
     */
    List<PositionListItemDto> findPositionListWithHeadCount();

    /**
     * 직급 상세 정보 조회 (직원 목록 포함)
     *
     * @param positionId 직급 ID
     * @return Optional<PositionDetailDto>
     */
    Optional<PositionDetailDto> findPositionDetailById(String positionId);
}
