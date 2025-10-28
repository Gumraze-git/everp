package org.ever._4ever_be_auth.user.repository;

import org.ever._4ever_be_auth.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsByLoginEmail(String email);

    Optional<User> findByLoginEmail(String email);

    Optional<User> findByEmail(String email);

    void deleteUserByUserId(String userId);
}
