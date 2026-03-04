package org.ever._4ever_be_gw.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InternalUserProfileResponse(
    String userId,
    @JsonProperty("userName")
    String userName
) {
}
