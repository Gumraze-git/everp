package org.ever._4ever_be_business.hr.repository;

import org.ever._4ever_be_business.hr.entity.InternelUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InternelUserRepository extends JpaRepository<InternelUser, String> {
    /**
     * userId로 InternelUser 조회
     */
    Optional<InternelUser> findByUserId(String userId);
}
