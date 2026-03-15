package org.ever._4ever_be_auth.auth.account.demo;

import java.util.List;

public record DemoLoginAccountGroup(
        String label,
        String description,
        List<DemoLoginAccount> accounts
) {
}
