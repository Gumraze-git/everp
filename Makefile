DOCKER_COMPOSE ?= docker compose
DC = $(DOCKER_COMPOSE) -f docker-compose.yml -p everp

.PHONY: help up down reset-db logs ps up-frontend up-auth up-business up-alarm up-scm up-gw test

help:
	@echo "핵심 타겟:"
	@echo "  make up            - 🚀 전체 서비스 빌드 및 기동"
	@echo "                       - frontend, gw, auth, business, alarm, scm, postgres, redis, kafka"
	@echo "                       - GW 포트 변경 예시: EVERP_GW_PORT=28080 make up"
	@echo "  make down          - 🛑 전체 서비스 중지 및 컨테이너 정리"
	@echo "  make reset-db      - 🧹 Postgres 볼륨 초기화"
	@echo "  make logs          - 📜 전체 로그 팔로우"
	@echo "  make ps            - 📦 컨테이너 상태 확인"
	@echo "  make test          - ✅ FE 빌드 + BE 전체 테스트 실행"
	@echo ""
	@echo "개별 서비스 재기동:"
	@echo "  make up-frontend   - 🔁 frontend만 재빌드/재기동"
	@echo "  make up-auth       - 🔁 auth만 재빌드/재기동"
	@echo "  make up-business   - 🔁 business만 재빌드/재기동"
	@echo "  make up-alarm      - 🔁 alarm만 재빌드/재기동"
	@echo "  make up-scm        - 🔁 scm만 재빌드/재기동"
	@echo "  make up-gw         - 🔁 gw만 재빌드/재기동"

up:
	$(DC) up -d --build

down:
	$(DC) down --remove-orphans

reset-db:
	$(DC) down -v --remove-orphans

logs:
	$(DC) logs -f --tail=200

ps:
	$(DC) ps

up-frontend:
	$(DC) up -d --build --no-deps frontend

up-auth:
	$(DC) up -d --build --no-deps auth

up-business:
	$(DC) up -d --build --no-deps business

up-alarm:
	$(DC) up -d --build --no-deps alarm

up-scm:
	$(DC) up -d --build --no-deps scm

up-gw:
	$(DC) up -d --build --no-deps gw

test:
	cd apps/frontend && npm ci && npm run build
	cd apps/backend/auth && ./gradlew clean test
	cd apps/backend/business && ./gradlew clean test
	cd apps/backend/alarm && ./gradlew clean test
	cd apps/backend/scm && ./gradlew clean test
	cd apps/backend/gw && ./gradlew clean test
