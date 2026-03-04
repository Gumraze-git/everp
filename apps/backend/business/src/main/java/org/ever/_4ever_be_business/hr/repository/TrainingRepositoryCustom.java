package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.vo.TrainingSearchConditionVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Training Custom Repository Interface
 * QueryDSL을 사용한 동적 쿼리 정의
 */
public interface TrainingRepositoryCustom {

    /**
     * 교육 프로그램 목록 검색 (동적 쿼리)
     *
     * @param condition 검색 조건 (name, status, category)
     * @param pageable  페이징 정보
     * @return Page<Training> 교육 프로그램 페이지
     */
    Page<Training> searchTrainingPrograms(TrainingSearchConditionVo condition, Pageable pageable);
}
