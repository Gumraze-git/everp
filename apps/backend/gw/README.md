# 4EVER Gateway (_4EVER_BE_GW)

경량 API 게이트웨이/백엔드 서비스로, 구매·발주·입고 관련 엔드포인트를 제공하고 Swagger 문서, Actuator 헬스체크, Redis 캐시, Kafka 연동을 지원합니다.

## 개요
- 런타임: Spring Boot 3 (Java 17)
- 빌드: Gradle
- 기본 컨텍스트 경로: `/api` (application.yml 참고)
- 주요 의존성: Web, Security, Validation, Actuator, Springdoc, Redis, JPA, Kafka
- 메인 클래스: `src/main/java/org/ever/_4ever_be_gw/Application.java:1`
- 설정: `application.yml:1`

## 주요 기능
- 발주서 목록/상세 조회, 승인/반려 처리
- 입고 대기/입고 완료 발주 조회
- 공급업체 목록/등록
- Swagger UI, OpenAPI 문서, Actuator 헬스·메트릭

## 요구 사항
- JDK 17 이상
- Gradle Wrapper 사용 권장 (`./gradlew`)
- (선택) Redis, Kafka

## 실행 방법
### 1) Gradle로 로컬 실행
- 개발 실행: `./gradlew bootRun`
- 빌드: `./gradlew build`

### 2) JAR로 실행
- JAR 생성: `./gradlew bootJar`
- 실행: `java -jar build/libs/*.jar`

### 3) Docker
- 빌드: `docker build -t 4ever-gw .`
- 실행 예시:
  - `docker run --rm -p 8080:8080 \
      -e SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092 \
      -e SPRING_REDIS_HOST=localhost \
      -e SPRING_REDIS_PORT=6379 \
      --name 4ever-gw 4ever-gw`

## 환경 변수
- `SERVER_PORT` (기본 8080)
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` (기본 `localhost:9092`)
- `SPRING_REDIS_HOST` (기본 `localhost`)
- `SPRING_REDIS_PORT` (기본 `6379`)
- `SPRING_REDIS_PASSWORD` (기본 빈 값)
- Swagger/Actuator 경로 등은 `application.yml:1` 참조

## API 요약 (기본 경로: `/api`)
- 발주서 목록: `GET /purchase-orders`
  - 상태 필터: `PENDING|APPROVED|REJECTED|DELIVERED`
  - 구현: `src/main/java/org/ever/_4ever_be_gw/scmpp/controller/MmController.java:900`
- 발주서 상세: `GET /purchase-orders/{purchaseId}`
  - 구현: `src/main/java/org/ever/_4ever_be_gw/scmpp/controller/MmController.java:1095`
- 발주서 승인: `POST /purchase-orders/{poId}/approve`
  - 구현: `src/main/java/org/ever/_4ever_be_gw/scmpp/controller/MmController.java:1455`
- 발주서 반려: `POST /purchase-orders/{poId}/reject`
  - 구현: `src/main/java/org/ever/_4ever_be_gw/scmpp/controller/MmController.java:1544`
- 입고 완료 목록: `GET /purchase-orders/received`
  - 구현: `src/main/java/org/ever/_4ever_be_gw/scmpp/controller/InventoryController.java:1004`
- 입고 대기 목록: `GET /purchase-orders/pending`
  - 구현: `src/main/java/org/ever/_4ever_be_gw/scmpp/controller/InventoryController.java:1059`
- 공급업체 목록: `GET /vendors`
  - 구현: `src/main/java/org/ever/_4ever_be_gw/scmpp/controller/MmController.java:1160`

예시 호출
- 발주서 목록: `curl 'http://localhost:8080/api/purchase-orders?status=PENDING'`
- 발주서 승인: `curl -X POST 'http://localhost:8080/api/purchase-orders/1002/approve' -H 'Authorization: Bearer PO_APPROVER'`

## 보안/권한
- 일부 쓰기/승인 API는 `Authorization` 헤더에 특정 권한 토큰을 요구합니다.
  - 예: 승인/반려는 `PO_APPROVER`, `PURCHASING_MANAGER`, `ADMIN` 중 하나 필요
  - 로직 예시는 `MmController` 승인/반려 핸들러 참고

## 모니터링/문서
- Actuator: `/actuator` (health, info, metrics, prometheus 노출)
- Swagger UI: `/swagger-ui.html`
- OpenAPI 문서: `/v3/api-docs`

## 개발/테스트
- 테스트 실행: `./gradlew test`
- 코드 스타일: Java 17, Lombok 사용, Validation 예외는 공통 에러 코드를 통해 응답
- DB: H2/PostgreSQL 의존성 포함. 현재 컨트롤러는 목업 데이터 기반 응답이 다수입니다.

## 프로젝트 구조
- 애플리케이션: `src/main/java/org/ever/_4ever_be_gw/Application.java:1`
- 컨트롤러: `src/main/java/org/ever/_4ever_be_gw/scmpp/controller/`
- DTO: `src/main/java/org/ever/_4ever_be_gw/scmpp/dto/`
- 설정: `application.yml:1`, `build.gradle:1`, `Dockerfile:1`