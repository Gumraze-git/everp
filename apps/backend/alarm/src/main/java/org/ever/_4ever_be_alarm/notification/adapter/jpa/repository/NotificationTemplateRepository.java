package org.ever._4ever_be_alarm.notification.adapter.jpa.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

    /**
     * 템플릿명으로 조회
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.templateName = :templateName")
    Optional<NotificationTemplate> findByTemplateName(@Param("templateName") String templateName);

    /**
     * 템플릿명 존재 여부 확인
     */
    @Query("SELECT COUNT(nt) > 0 FROM NotificationTemplate nt WHERE nt.templateName = :templateName")
    boolean existsByTemplateName(@Param("templateName") String templateName);

    /**
     * 템플릿명으로 검색 (부분 일치)
     */
    @Query("SELECT nt FROM NotificationTemplate nt WHERE nt.templateName LIKE %:templateName%")
    List<NotificationTemplate> findByTemplateNameContaining(
        @Param("templateName") String templateName);

    /**
     * 활성화된 템플릿 목록 조회 (최근 업데이트된 템플릿)
     */
    @Query("SELECT nt FROM NotificationTemplate nt " +
        "ORDER BY nt.updatedAt DESC")
    List<NotificationTemplate> findAllOrderByUpdatedAtDesc();
}