package org.ever._4ever_be_auth.user.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    // 사용자 계정의 현재 상태 정의
    ACTIVE,     // 활성
    INACTIVE,   // 비활성
    DELETED     // 삭제
}
