package org.ever._4ever_be_auth.auth.account.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.ever._4ever_be_auth.auth.account.demo.DemoLoginAccountCatalog;
import org.ever._4ever_be_auth.auth.account.service.AccountService;
import org.ever._4ever_be_auth.common.exception.BusinessException;
import org.ever._4ever_be_auth.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final DemoLoginAccountCatalog demoLoginAccountCatalog;
    private static final String AUTHZ_ORIGINAL_URL_KEY = "AUTHZ_ORIGINAL_URL";

    @Value("${everp.auth.demo-login.enabled:false}")
    private boolean demoLoginEnabled;

    // 로그인
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("demoLoginEnabled", demoLoginEnabled);
        if (demoLoginEnabled) {
            model.addAttribute("demoAccountGroups", demoLoginAccountCatalog.loginGroups());
        }
        return "login";
    }

    // 비밀번호 변경 페이지
    @GetMapping("/password/reset")
    public String passwordResetRequestPage() {
        return "password-reset-request";
    }

    // 비밀번호 변경을 위한 메일 송부
    @PostMapping("/password/reset")
    public String requestPasswordReset(
            @RequestParam("email") String email,
            RedirectAttributes redirectAttributes
    ) {
        try {
            accountService.sendResetLink(email.trim());
            redirectAttributes.addFlashAttribute("message", "입력하신 이메일로 재설정 링크를 전송했습니다.");
            return "redirect:/password/reset?sent";
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.USER_NOT_FOUND) {
                redirectAttributes.addFlashAttribute("error", "등록되지 않은 이메일입니다.");
                return "redirect:/password/reset?error";
            }
            throw e;
        }
    }

    // 비밀번호 confirm page
    @GetMapping("/password/reset/confirm")
    public String confirmResetToken(
            @RequestParam("token") String token,
            Model model
    ) {
        model.addAttribute("token", token);
        return "password-reset-confirm";
    }

    // 비밀번호 confirm post
    @PostMapping("/password/reset/confirm")
    public String resetPassword(
            @RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword,
            RedirectAttributes redirectAttributes
    ) {
        accountService.resetPassword(token, newPassword);
        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다. 새 비밀번호로 로그인해 주세요");
        return "redirect:/login?success";
    }

    @GetMapping("/password/change")
    public String passwordChangePage(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        return "password-change";
    }

    @PostMapping("/password/change")
    public String changePassword(@RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "redirect:/password/change?error";
        }

        try {
            accountService.changePassword(authentication.getName(), newPassword);
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.INVALID_PASSWORD) {
                redirectAttributes.addFlashAttribute("error", e.getMessage());
                return "redirect:/password/change?error";
            }
            throw e;
        }

        HttpSession session = request.getSession(false);
        String originalAuthRequest = null;
        if (session != null) {
            Object original = session.getAttribute(AUTHZ_ORIGINAL_URL_KEY);
            if (original instanceof String) {
                originalAuthRequest = (String) original;
            }
        }

        new SecurityContextLogoutHandler().logout(request, response, authentication);

        if (originalAuthRequest != null) {
            request.getSession(true).setAttribute(AUTHZ_ORIGINAL_URL_KEY, originalAuthRequest);
        }

        redirectAttributes.addFlashAttribute("message", "비밀번호가 변경되었습니다. 새 비밀번호로 다시 로그인해 주세요.");
        return "redirect:/login?passwordChanged";
    }
}
