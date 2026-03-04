package org.ever._4ever_be_auth.auth.account.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class PasswordResetMailFactory {

    private final JavaMailSender mailSender;

    public MimeMessage createMessage(String receiverEmail, String token) throws MessagingException {
        String resetLink = UriComponentsBuilder
                .fromPath("/password/reset/confirm")
                .queryParam("token", token)
                .build()
                .toUriString();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(receiverEmail);
        helper.setSubject("[EvERP] 비밀번호 재설정 안내");
        helper.setText(buildHtmlBody(resetLink), true);

        return message;
    }

    private String buildHtmlBody(String resetLink) {
        return """
                <div style="font-family: 'Pretendard', Arial, sans-serif; max-width: 520px; margin: 0 auto; padding: 30px; background-color: #f9fafb; border-radius: 12px; border: 1px solid #e5e7eb;">
                    <h2 style="color: #111827; text-align: center; margin-bottom: 24px;">EvERP 비밀번호 재설정</h2>
                    <p style="color: #374151; font-size: 15px; line-height: 1.6;">
                        아래 버튼을 눌러 비밀번호를 재설정해 주세요. 링크는 30분 동안만 유효합니다.
                    </p>
                    <div style="text-align: center; margin: 24px 0;">
                        <a href="%s"
                           style="display: inline-block; padding: 12px 20px; border-radius: 8px; background-color: #2563eb; color: #ffffff; text-decoration: none; font-weight: 600;">
                            비밀번호 재설정
                        </a>
                    </div>
                    <p style="color: #6b7280; font-size: 13px; line-height: 1.6;">
                        버튼이 동작하지 않으면 아래 링크를 복사해 브라우저 주소창에 붙여 넣어 주세요.<br>
                        <span style="word-break: break-all;">%s</span>
                    </p>
                    <p style="color: #6b7280; font-size: 12px; text-align: center; margin-top: 24px;">
                        ⓒ 2025 EvERP Corp. All rights reserved.
                    </p>
                </div>
                """.formatted(resetLink, resetLink);
    }
}
