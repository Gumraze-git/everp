package org.ever._4ever_be_business.hr.dao;

import org.ever._4ever_be_business.hr.dto.response.PositionDetailDto;
import org.ever._4ever_be_business.hr.dto.response.PositionListItemDto;

import java.util.List;
import java.util.Optional;

public interface PositionDAO {
    /**
     * 직급 상세 정보 조회
     *
     * @param positionId 직급 ID
     * @return 직급 상세 정보
     */
    Optional<PositionDetailDto> findPositionDetailById(String positionId);

    /**
     * 직급 목록 조회
     *
     * @return 직급 목록
     */
    List<PositionListItemDto> findPositionList();
}
