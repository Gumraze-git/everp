package org.ever._4ever_be_auth.auth.account.demo;

import org.ever._4ever_be_auth.user.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class DemoLoginAccountCatalog {

    private static final String DEFAULT_PASSWORD = "password";

    private final DemoLoginAccount primaryAdminAccount = account("전사 관리자", "admin@everp.com", UserRole.ALL_ADMIN);

    private final List<DemoLoginAccount> moduleAccounts = List.of(
            account("구매 사용자", "mm-user@everp.com", UserRole.MM_USER),
            account("영업 사용자", "sd-user@everp.com", UserRole.SD_USER),
            account("재고 사용자", "im-user@everp.com", UserRole.IM_USER),
            account("재무 사용자", "fcm-user@everp.com", UserRole.FCM_USER),
            account("인사 사용자", "hrm-user@everp.com", UserRole.HRM_USER),
            account("생산 사용자", "pp-user@everp.com", UserRole.PP_USER)
    );

    private final List<DemoLoginAccount> partnerAccounts = List.of(
            account("고객사 관리자", "customer-admin@everp.com", UserRole.CUSTOMER_ADMIN),
            account("공급사 관리자", "supplier-admin@everp.com", UserRole.SUPPLIER_ADMIN)
    );

    private final List<DemoLoginAccountGroup> loginGroups = List.of(
            new DemoLoginAccountGroup(
                    "전사 운영",
                    "전체 관리 권한으로 바로 진입할 수 있습니다.",
                    List.of(primaryAdminAccount)
            ),
            new DemoLoginAccountGroup(
                    "모듈 사용자",
                    "구매, 영업, 재고, 재무, 인사, 생산 모듈 계정을 빠르게 고를 수 있습니다.",
                    moduleAccounts
            ),
            new DemoLoginAccountGroup(
                    "외부 파트너",
                    "고객사와 공급사 포털 관리자 계정을 제공합니다.",
                    partnerAccounts
            )
    );

    private final List<DemoLoginAccount> representativeAccounts = Stream.concat(
            Stream.of(primaryAdminAccount),
            Stream.concat(moduleAccounts.stream(), partnerAccounts.stream())
    ).toList();

    public List<DemoLoginAccount> representativeAccounts() {
        return representativeAccounts;
    }

    public List<DemoLoginAccountGroup> loginGroups() {
        return loginGroups;
    }

    public DemoLoginAccount primaryAdminAccount() {
        return primaryAdminAccount;
    }

    public String defaultPassword() {
        return DEFAULT_PASSWORD;
    }

    private DemoLoginAccount account(String label, String email, UserRole role) {
        return new DemoLoginAccount(label, email, DEFAULT_PASSWORD, role);
    }
}
