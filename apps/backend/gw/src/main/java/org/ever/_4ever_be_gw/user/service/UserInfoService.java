package org.ever._4ever_be_gw.user.service;

import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_gw.common.exception.BusinessException;
import org.ever._4ever_be_gw.common.exception.ErrorCode;
import org.ever._4ever_be_gw.config.security.principal.EverUserPrincipal;
import org.ever._4ever_be_gw.user.client.UserProfileRemoteClient;
import org.ever._4ever_be_gw.user.dto.UserInfoResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserProfileRemoteClient userProfileRemoteClient;

    public UserInfoResponse getUserInfo(EverUserPrincipal principal, String accessToken) {
        if (principal == null) {
            throw new BusinessException(ErrorCode.AUTH_TOKEN_REQUIRED);
        }

        String userName = userProfileRemoteClient
            .fetchUserName(principal.getUserType(), principal.getUserId(), accessToken)
            .orElse(null);

        return new UserInfoResponse(
            principal.getUserId(),
            userName,
            principal.getLoginEmail(),
            principal.getUserRole(),
            principal.getUserType()
        );
    }
}
