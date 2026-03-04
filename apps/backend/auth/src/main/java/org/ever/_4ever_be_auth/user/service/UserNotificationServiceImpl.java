package org.ever._4ever_be_auth.user.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final JavaMailSender mailSender;

    @Override
    public void sendUserNotification(String contactEmail, String loginEmail, String randomPassword) {
        log.info("초기 로그인 정보 발송: {} -> 로그인 이메일: {}, 비밀번호: {}", contactEmail, loginEmail, randomPassword);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(contactEmail);
            helper.setSubject("[EvERP] 🔐 초기 로그인 정보 안내");

            // HTML 본문 (inline 스타일로 Gmail 호환성 최적화)
            String htmlContent = """
                    <div style="font-family: 'Pretendard', Arial, sans-serif; max-width: 520px; margin: 0 auto; padding: 30px; background-color: #f9fafb; border-radius: 12px; border: 1px solid #e5e7eb;">
                        <h2 style="color: #111827; text-align: center; margin-bottom: 24px;">
                            EvERP 계정 로그인 정보
                        </h2>
                        <p style="color: #374151; font-size: 15px; line-height: 1.6;">
                            안녕하세요.<br>
                            아래는 발급된 초기 로그인 정보입니다. 반드시 첫 로그인 후 비밀번호를 변경해 주세요.
                        </p>
                        <div style="background-color: #ffffff; padding: 20px; margin: 20px 0; border-radius: 8px; border: 1px solid #d1d5db;">
                            <p style="margin: 0; color: #111827; font-size: 14px;">
                                <strong>로그인 이메일:</strong><br>
                                <span style="color: #2563eb;">%s</span>
                            </p>
                            <p style="margin-top: 12px; color: #111827; font-size: 14px;">
                                <strong>초기 비밀번호:</strong><br>
                                <span style="color: #dc2626;">%s</span>
                            </p>
                        </div>
                        <p style="color: #6b7280; font-size: 13px; text-align: center; margin-top: 24px;">
                            ⓒ 2025 EvERP Corp. All rights reserved.
                        </p>
                    </div>
                    """.formatted(loginEmail, randomPassword);

            helper.setText(htmlContent, true); // true → HTML 모드

            mailSender.send(message);
            log.info("초기 로그인 메일 발송 완료 → {}", contactEmail);

        } catch (MessagingException e) {
            log.error("초기 로그인 메일 발송 실패 → {}", contactEmail, e);
        }
    }
}