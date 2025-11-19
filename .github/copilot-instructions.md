## Purpose
Short, actionable guidance to help a coding AI be productive in this repository. Focus on the microservices backend (Spring Boot/Maven), the Vite + Vue frontend, and the local Docker setup.

## Big picture
- Architecture: a small Spring Boot microservices ecosystem under `backend_logic/` (services: `eureka-server`, `api-gateway`, `user-service`, `property-service`, `location-service`, `analytics-service`) plus a `frontend/` (Vue 3 + Vite).
- Service discovery: `eureka-server` (port 8761) is used for service registration; services register against it (see `docker-compose.yml`).
- API gateway and shared module: `api-gateway/` and `shared/` contain cross-cutting code; changes here affect multiple services.

## Key files and commands (examples)
- Start everything locally (dev): `npm run start:all` from repo root — this runs the Eureka server and backend services via each module's `./mvnw`, then the frontend (see `package.json` scripts).
- Start an individual backend service: `cd backend_logic/user-service && ./mvnw spring-boot:run` (or use the service-specific `start:user` script from root).
- Build and run with Docker Compose: `docker-compose up --build` (compose consumes `.env` values next to the compose file; see `docker-compose.yml`).
- Frontend dev: `cd frontend && npm install && npm run dev` (Vite). See `frontend/README.md` for test and lint commands.

## Project-specific conventions and patterns
- Use the Maven wrapper (`./mvnw`) inside each backend module; prefer it over a local maven install.
- Backend services are Spring Boot apps; configuration secrets live in `backend_logic/*/.env` (example: `backend_logic/user-service/.env`) and `config/` (JWT keys `private.pem`, `public.pem`). Avoid printing secrets in logs or code changes.
- Some services include Dockerfiles (e.g., `property-service/Dockerfile`, `user-service/Dockerfile`) — Docker builds rely on artifacts in each service's `target/` when packaging.
- Shared code lives in `shared/` Maven module; updating its API may require rebuilding dependent services.

## Integration points & external dependencies
- Service discovery: `eureka-server` — ensure it's running before starting other services in dev (scripts implement small delays; `start:frontend:delayed` waits 20s).
- Database: PostgreSQL is referenced via `SPRING_DATASOURCE_*` env vars (the repo uses a Supabase-hosted DB in `.env`). Docker Compose passes these envs through.
- JWT keys & cookies: See `backend_logic/user-service/.env` and `config/*.pem`. JWT KID and PEMs are base64-embedded in `.env` for local dev.

## Testing and debugging
- Backend unit/integration: run `./mvnw test` inside the target backend module.
- Frontend unit tests: `npm run test:unit` (Vitest). E2E: Cypress configs in `frontend/cypress/` and scripts in `frontend/README.md`.
- Logs: backend services print SQL when `SPRING_JPA_SHOW_SQL=true` in `.env`. For debugging, run individual services (not all) to reduce log noise.

## How AI agents should propose code changes
- Small, focused PRs that touch one service or one frontend feature at a time. If you change `shared/`, include a rebuild and test of dependents.
- When editing backend config or adding env variables, update `.env` examples and avoid committing secrets (replace with placeholders). Note when a change requires updating Dockerfile or compose volumes (e.g., `secrets/jwt_public.pem`).

## Helpful file references (quick links)
- Root scripts: `package.json` (multiple `start:*` scripts) — preferred for dev orchestration.
- Local orchestration: `docker-compose.yml` — shows how services are wired and what envs are required.
- Backend example env: `backend_logic/user-service/.env` — shows JWT, DB, OAuth keys (sensitive).
- Frontend entry: `frontend/README.md` and `frontend/package.json` (dev server, build, tests).

## Final notes for agents
- Be conservative with secrets and large binary changes. If unsure about environment-specific behavior, document the assumption and include a short verification step (command to run).
- If modifying service registration, double-check `EUREKA_*` env vars and `docker-compose.yml` to avoid breaking discovery.

If anything here is unclear or you'd like more specifics (example PR templates, CI commands, or unit-test examples), tell me which part and I will expand.
