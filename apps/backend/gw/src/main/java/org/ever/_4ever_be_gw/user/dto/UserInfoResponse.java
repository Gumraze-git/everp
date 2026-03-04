package org.ever._4ever_be_gw.user.dto;

public record UserInfoResponse(
    String userId,
    String userName,
    String loginEmail,
    String userRole,
    String userType
) {
}
