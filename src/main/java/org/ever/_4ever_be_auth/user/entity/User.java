package org.ever._4ever_be_auth.user.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.ever._4ever_be_auth.common.entity.TimeStamp;
import org.ever._4ever_be_auth.user.enums.UserRole;
import org.ever._4ever_be_auth.user.enums.UserStatus;
import org.ever._4ever_be_auth.user.enums.UserType;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User extends TimeStamp {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "user_id", nullable = false, updatable = false, unique = true, length = 36)
    private String userId;

    @Column(name = "email", nullable = false)
    private String email;           // 사용자의 일반 이메일

    @Column(name = "login_email", nullable = false, unique = true, length = 320)
    private String loginEmail;      // 사용자의 로그인 이메일(*@everp.com)

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserType userType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus userStatus;

    @Column(name = "password_last_changed_at")
    private LocalDateTime passwordLastChangedAt;  // null 이면 최초 비밀번호 변경 필요

    @Builder(access = AccessLevel.PRIVATE)
    public User(String userId,
                String loginEmail,
                String passwordHash,
                UserRole userRole,
                UserType userType,
                UserStatus userStatus,
                LocalDateTime passwordLastChangedAt) {
        this.userId = userId;
        this.loginEmail = loginEmail;
        this.passwordHash = passwordHash;
        this.userRole = userRole;
        this.userType = userType;
        this.userStatus = userStatus;
        this.passwordLastChangedAt = passwordLastChangedAt;
    }

    public static User create(
            String loginEmail,
            String encodedPassword,
            @NotNull UserRole userRole
    ) {
        return User.builder()
                .loginEmail(loginEmail)
                .passwordHash(encodedPassword)
                .userRole(userRole)
                .userType(userRole.getType())
                .userStatus(UserStatus.ACTIVE)
                .passwordLastChangedAt(null) // 최초 로그인 시 변경을 요구하기 위해서 null으로 처리함.
                .build();
    }

    public void updatePassword(String encodedPassword, LocalDateTime changedAt) {
        this.passwordHash = encodedPassword;
        this.passwordLastChangedAt = changedAt;
    }

    public void updateContactEmail(String email) {
        this.email = email;
    }

    public static User createWithExternalId(
            String userId,
            String contactEmail,
            String loginEmail,
            String encodedPassword,
            UserRole userRole
    ) {
        User user = User.create(loginEmail, encodedPassword, userRole);
        user.userId = userId;
        user.email = contactEmail;
        return user;
    }

    @PrePersist
    public void prePersist() {
        if (userId == null) {
            userId = UuidCreator.getTimeOrdered().toString();
        }
    }
}
