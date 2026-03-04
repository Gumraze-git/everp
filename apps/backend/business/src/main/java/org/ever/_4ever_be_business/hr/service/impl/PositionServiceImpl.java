package org.ever._4ever_be_business.hr.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ever._4ever_be_business.common.exception.BusinessException;
import org.ever._4ever_be_business.common.exception.ErrorCode;
import org.ever._4ever_be_business.hr.dto.response.PositionDetailDto;
import org.ever._4ever_be_business.hr.dto.response.PositionListItemDto;
import org.ever._4ever_be_business.hr.entity.Position;
import org.ever._4ever_be_business.hr.repository.PositionRepository;
import org.ever._4ever_be_business.hr.service.PositionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PositionServiceImpl implements PositionService {

    private final PositionRepository positionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PositionListItemDto> getPositionList() {
        log.info("직급 목록 조회 요청");

        List<PositionListItemDto> result = positionRepository.findPositionListWithHeadCount();

        log.info("직급 목록 조회 성공 - count: {}", result.size());

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PositionDetailDto getPositionDetail(String positionId) {
        log.info("직급 상세 정보 조회 요청 - positionId: {}", positionId);

        PositionDetailDto result = positionRepository.findPositionDetailById(positionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUSINESS_LOGIC_ERROR, "직급 정보를 찾을 수 없습니다."));

        log.info("직급 상세 정보 조회 성공 - positionId: {}, positionName: {}, headCount: {}",
                positionId, result.getPositionName(), result.getHeadCount());

        return result;
    }
}
