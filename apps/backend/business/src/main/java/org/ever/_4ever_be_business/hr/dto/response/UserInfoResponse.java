package org.ever._4ever_be_business.hr.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * User Service 응답 데이터 구조
 */
@Getter
@NoArgsConstructor
public class UserInfoResponse {

    @JsonProperty("internelMemberInfos")
    private List<InternalMemberInfo> internelMemberInfos;

    @Getter
    @NoArgsConstructor
    public static class InternalMemberInfo {
        private String email;
        private String name;
        private String phoneNumber;
        private String employeeCode;
        private String gender;
        private String hireDate;
        private String departmentStartAt;
        private String education;
        private String career;
        private Integer salary;
        private Affiliation affiliation;
    }

    @Getter
    @NoArgsConstructor
    public static class Affiliation {
        private String jobName;
        private Boolean isManager;
        private String departmentName;
        private String description;
        private String establishmentDate;
    }
}
