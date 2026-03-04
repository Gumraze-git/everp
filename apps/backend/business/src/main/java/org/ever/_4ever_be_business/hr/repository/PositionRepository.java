package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Position Repository
 */
@Repository
public interface PositionRepository extends JpaRepository<Position, String>, PositionRepositoryCustom {
    Optional<Position> findByPositionCode(String positionCode);

    /**
     * 부서 ID로 해당 부서에 속한 직급 목록 조회
     *
     * @param departmentId 부서 ID
     * @return 해당 부서의 직급 목록
     */
    List<Position> findByDepartmentId(String departmentId);
}
