package org.ever._4ever_be_business.hr.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

/**
 * User Service 호출용 요청 DTO
 */
@Getter
public class UserInfoRequest {

    @JsonProperty("internelMemberIds")
    private final List<Long> internelMemberIds;

    public UserInfoRequest(List<Long> internelMemberIds) {
        this.internelMemberIds = internelMemberIds;
    }
}
