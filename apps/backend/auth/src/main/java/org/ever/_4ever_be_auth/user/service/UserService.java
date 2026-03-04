package org.ever._4ever_be_auth.user.service;


import org.ever._4ever_be_auth.user.dto.CreateUserRequestDto;
import org.ever._4ever_be_auth.user.entity.User;
import org.ever._4ever_be_auth.user.enums.UserRole;

public interface UserService {
    User createUser(CreateUserRequestDto requestDto, UserRole requesterUserRole);
}
