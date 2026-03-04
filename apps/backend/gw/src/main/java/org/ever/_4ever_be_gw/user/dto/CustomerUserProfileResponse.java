package org.ever._4ever_be_gw.user.dto;

public record CustomerUserProfileResponse(
    String userId,
    @com.fasterxml.jackson.annotation.JsonProperty("userName")
    String userName
) {
}
