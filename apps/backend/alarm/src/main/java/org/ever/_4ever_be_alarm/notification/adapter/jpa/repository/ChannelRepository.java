package org.ever._4ever_be_alarm.notification.adapter.jpa.repository;

import java.util.Optional;
import java.util.UUID;
import org.ever._4ever_be_alarm.notification.adapter.jpa.entity.Channel;
import org.ever._4ever_be_alarm.notification.domain.model.constants.ChannelNameEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    /**
     * 타입명으로 조회
     */
    @Query("SELECT c FROM Channel c WHERE c.name = :name")
    Optional<Channel> findByName(@Param("name") ChannelNameEnum name);

    /**
     * 타입명 존재 여부 확인
     */
    @Query("SELECT COUNT(c) > 0 FROM Channel c WHERE c.name = :name")
    boolean existsByName(@Param("name") ChannelNameEnum Name);
}
