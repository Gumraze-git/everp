package org.ever._4ever_be_alarm.infrastructure.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.FileInputStream;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${fcm.service-account-file}")
    private String serviceAccountPath;

    @Value("${fcm.project-id}")
    private String projectId;

    @Bean
    public FirebaseApp firebaseApp() {
        try {
            InputStream credentialsStream;
            try {
                ClassPathResource resource = new ClassPathResource(serviceAccountPath);
                credentialsStream = resource.getInputStream();
                log.info("[FCM] 서비스 계정 키를 classpath에서 로드: {}", serviceAccountPath);
            } catch (Exception e) {
                log.warn("[FCM] classpath 로드 실패, 파일 경로 시도: {} - {}", serviceAccountPath, e.getMessage());
                credentialsStream = new FileInputStream(serviceAccountPath);
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                .setProjectId(projectId)
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp app = FirebaseApp.initializeApp(options);
                log.info("[FCM] FirebaseApp 초기화 완료 - projectId: {}", projectId);
                return app;
            } else {
                log.info("[FCM] 기존 FirebaseApp 재사용");
                return FirebaseApp.getInstance();
            }
        } catch (Exception e) {
            log.error("[FCM] FirebaseApp 초기화 실패: {}", e.getMessage(), e);
            throw new IllegalStateException("FirebaseApp 초기화 실패", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}


