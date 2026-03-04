package org.ever._4ever_be_alarm.notification.adapter.jpa.repository;

import java.util.Optional;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.Source;
import org.ever._4ever_be_alarm.notification.domain.model.constants.SourceTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceRepository extends JpaRepository<Source, UUID> {

    /**
     * 소스명으로 조회
     */
    @Query("SELECT s FROM Source s WHERE s.sourceName = :sourceName")
    Optional<Source> findBySourceName(@Param("sourceName") SourceTypeEnum sourceName);

    /**
     * 소스명 존재 여부 확인
     */
    @Query("SELECT COUNT(s) > 0 FROM Source s WHERE s.sourceName = :sourceName")
    boolean existsBySourceName(@Param("sourceName") SourceTypeEnum sourceName);

}
