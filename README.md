# everp Monorepo

AutoEver-4Ever 조직의 FE/BE 레포를 하나로 통합한 포트폴리오용 모노레포입니다.

## 구성

- frontend: `apps/frontend`
- auth: `apps/backend/auth`
- business: `apps/backend/business`
- alarm: `apps/backend/alarm`
- scm: `apps/backend/scm`
- gateway: `apps/backend/gw`

모바일(`_4EVER_IOS`, `_4EVER_AOS`)과 org `.github` 레포는 포함하지 않았습니다.

## 실행 포트

- FE: `${EVERP_FRONTEND_PORT:-13000}` -> `frontend:3000`
- GW: `${EVERP_GW_PORT:-18080}` -> `gw:8080`
- AUTH: `${EVERP_AUTH_PORT:-18081}` -> `auth:8080`
- BUSINESS: `${EVERP_BUSINESS_PORT:-18082}` -> `business:8080`
- ALARM: `${EVERP_ALARM_PORT:-18083}` -> `alarm:8080`
- SCM: `${EVERP_SCM_PORT:-18084}` -> `scm:8080`
- Postgres: `${EVERP_POSTGRES_PORT:-15432}`
- Redis: `${EVERP_REDIS_PORT:-16379}`
- Kafka: `${EVERP_KAFKA_PORT:-19092}`

## 빠른 시작

```bash
make up
```

기본 로컬 포트는 FE `13000`, GW `18080`, AUTH `18081`입니다.

GW 호스트 포트를 바꾸려면:

```bash
EVERP_GW_PORT=28080 make up
```

전체 포트/오리진 설정은 `env/compose.env.example`를 참고해 루트 `.env`로 관리할 수 있습니다.
FE 또는 AUTH 포트를 바꾸면 `EVERP_FRONTEND_ORIGIN`, `EVERP_AUTH_ISSUER`, `EVERP_OAUTH_REDIRECT_URI`도 함께 맞춰야 합니다.

중지:

```bash
make down
```

로그:

```bash
make logs
```

## 검증 URL

- FE: <http://localhost:13000>
- GW Swagger: <http://localhost:18080/api/swagger-ui/index.html>
- AUTH JWK: <http://localhost:18081/.well-known/jwks.json>

## FE 런타임 환경변수

- `NEXT_PUBLIC_API_BASE_URL` (기본: `http://localhost:${EVERP_GW_PORT:-18080}/api`)
- `NEXT_PUBLIC_AUTH_BASE_URL` (기본: `http://localhost:${EVERP_AUTH_PORT:-18081}`)
- `NEXT_PUBLIC_OAUTH_CLIENT_ID` (기본: `everp-spa`)
- `NEXT_PUBLIC_OAUTH_REDIRECT_URI` (기본: `http://localhost:${EVERP_FRONTEND_PORT:-13000}/callback`)

## Alarm FCM 로컬 모드

- 로컬 기본값은 `fcm.enabled=false`입니다.
- 이때 Firebase를 초기화하지 않고 no-op push adapter가 동작합니다.
- 실서버에서는 `fcm.enabled=true`와 실제 서비스 계정 키를 사용하세요.

## 트러블슈팅

- Docker Desktop이 꺼져 있으면 `make up`이 실패합니다.
- 첫 빌드는 Gradle/NPM 의존성 다운로드로 시간이 오래 걸릴 수 있습니다.
- 로컬 AUTH issuer 기본값은 `http://localhost:18081`입니다.
