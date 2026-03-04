package org.ever._4ever_be_alarm.notification.adapter.jpa.repository;

import java.util.Optional;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.NotificationErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationErrorCodeRepository extends
    JpaRepository<NotificationErrorCode, UUID> {

    /**
     * 에러 코드로 조회
     */
    @Query("SELECT nec FROM NotificationErrorCode nec WHERE nec.errorCode = :errorCode")
    Optional<NotificationErrorCode> findByErrorCode(@Param("errorCode") Integer errorCode);

    /**
     * 에러 메시지로 조회
     */
    @Query("SELECT nec FROM NotificationErrorCode nec WHERE nec.errorMessage = :errorMessage")
    Optional<NotificationErrorCode> findByErrorMessage(@Param("errorMessage") String errorMessage);

    /**
     * 에러 코드 존재 여부 확인
     */
    @Query("SELECT COUNT(nec) > 0 FROM NotificationErrorCode nec WHERE nec.errorCode = :errorCode")
    boolean existsByErrorCode(@Param("errorCode") Integer errorCode);

    /**
     * 에러 메시지 존재 여부 확인
     */
    @Query("SELECT COUNT(nec) > 0 FROM NotificationErrorCode nec WHERE nec.errorMessage = :errorMessage")
    boolean existsByErrorMessage(@Param("errorMessage") String errorMessage);

}