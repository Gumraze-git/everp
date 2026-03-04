package org.ever._4ever_be_gw.user.dto;

public record SupplierUserProfileResponse(
    String userId,
    @com.fasterxml.jackson.annotation.JsonProperty("userName")
    String userName
) {
}
