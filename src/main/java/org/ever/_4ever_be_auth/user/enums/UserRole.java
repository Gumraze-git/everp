package org.ever._4ever_be_auth.user.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    // <MODULE_NAME>_<ROLE_NAME>으로 구성되어 있는 사용자의 역할임
    // 사용자에게는 각 역할이 부여되며 역할마다 권한이 부여됨
    // 내부 사용자(USER, ADMIN)
    MM_USER(UserType.INTERNAL),
    MM_ADMIN(UserType.INTERNAL),
    SD_USER(UserType.INTERNAL),
    SD_ADMIN(UserType.INTERNAL),
    IM_USER(UserType.INTERNAL),
    IM_ADMIN(UserType.INTERNAL),
    FCM_USER(UserType.INTERNAL),
    FCM_ADMIN(UserType.INTERNAL),
    HRM_USER(UserType.INTERNAL),
    HRM_ADMIN(UserType.INTERNAL),
    PP_USER(UserType.INTERNAL),
    PP_ADMIN(UserType.INTERNAL),

    // 내부 관리자
    ALL_ADMIN(UserType.INTERNAL),       // 전사 관리자
    CEO_ADMIN(UserType.INTERNAL),       // 최고 경영자

    // 고객사
    CUSTOMER_USER(UserType.CUSTOMER),
    CUSTOMER_ADMIN(UserType.CUSTOMER),

    // 공급사
    SUPPLIER_USER(UserType.SUPPLIER),
    SUPPLIER_ADMIN(UserType.SUPPLIER);

    private final UserType type;
}
