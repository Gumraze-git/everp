package org.ever._4ever_be_gw.config.security.principal;

import java.io.Serial;
import java.io.Serializable;
import java.security.Principal;
import java.time.Instant;
import java.util.Objects;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Gateway에서 JWT(AccessToken)을 해석하여 얻은 사용자의 기본 정보를 담는 객체임
 */
@Getter
@EqualsAndHashCode
@ToString
public final class EverUserPrincipal implements Principal, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String userId;
    private final String loginEmail;
    private final String userRole;
    private final String userType;
    private final Instant issuedAt;
    private final Instant expiresAt;

    @Builder
    private EverUserPrincipal(
        String userId,
        String loginEmail,
        String userRole,
        /*
        *   - **MM_USER / MM_ADMIN** — 구매관리 (Material Management)
            - **SD_USER / SD_ADMIN** — 영업관리 (Sales & Distribution)
            - **IM_USER / IM_ADMIN** — 재고관리 (Inventory Management)
            - **FCM_USER / FCM_ADMIN** — 재무관리 (Financial & Cost Management)
            - **HRM_USER / HRM_ADMIN** — 인적자원관리 (Human Resource Management)
            - **PP_USER / PP_ADMIN** — 생산관리 (Production Planning)
        * */

        String userType, // -> INTERNAL / SUPPLIER / CUSTOMER
        Instant issuedAt,
        Instant expiresAt
    ) {
        this.userId = userId;
        this.loginEmail = Objects.requireNonNull(loginEmail, "loginEmail must not be null");
        this.userRole = userRole;
        this.userType = userType;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    @Override
    public String getName() {
        return loginEmail;
    }
}
