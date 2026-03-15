package org.ever._4ever_be_auth.auth.account.demo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.user.enums.UserRole;

@Getter
@RequiredArgsConstructor
public class DemoLoginAccount {

    private final String label;
    private final String email;
    private final String password;
    private final UserRole role;
}
