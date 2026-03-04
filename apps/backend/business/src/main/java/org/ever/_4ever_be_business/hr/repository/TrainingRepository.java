package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Training;
import org.ever._4ever_be_business.hr.enums.TrainingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Training Repository
 * QueryDSL을 사용한 동적 쿼리는 TrainingRepositoryCustom에 정의
 */
public interface TrainingRepository extends JpaRepository<Training, String>, TrainingRepositoryCustom {
    /**
     * 교육 프로그램 상태별 개수 조회
     *
     * @param trainingStatus 교육 프로그램 상태
     * @return 개수
     */
    long countByTrainingStatus(TrainingStatus trainingStatus);
}
