# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

HMIS (Hospital Management Information System) — a university database course project (South China Normal University). Spring Boot 4 backend with MyBatis + MySQL, Vue 3 + Ant Design Vue 4 frontend. The system manages outpatient registration, prescriptions, payments, doctor scheduling, and inpatient hospitalization across three roles: Admin, Doctor, Patient.

## Commands

### Database Setup (MySQL 8.x required)

```bash
# Create/reset the hospital database
mysql -u root -p123456 < src/main/resources/schema.sql
mysql -u root -p123456 hospital < src/main/resources/triggers.sql
mysql -u root -p123456 hospital < src/main/resources/views.sql
mysql -u root -p123456 hospital < src/main/resources/indexes.sql
mysql -u root -p123456 hospital < src/main/resources/data.sql
```

### Backend (Spring Boot on :8080)

```bash
./mvnw spring-boot:run          # start dev server
./mvnw test                     # run all tests
./mvnw package                  # build fat JAR
```

### Frontend (Vue + Vite on :5173, proxies /api → :8080)

```bash
cd frontend
npm install
npm run dev                     # dev server
npm run build                   # production build → frontend/dist/
```

## Architecture

```
Vue SPA (Ant Design Vue 4)  ──HTTP──▶  Controller  ──▶  Service  ──▶  Mapper (MyBatis XML)  ──▶  MySQL
       frontend/src/views/              controller/       service/       mapper/ + resources/mapper/
```

**Three-layer backend** with constructor-based DI, all returning `ApiResponse<T>`:

- **Controller** (4): `AdminController`, `DoctorController`, `PatientController`, `LoginController` — thin delegates, URL prefixes `/api/admin`, `/api/doctor`, `/api/patient`, `/api/auth`
- **Service** (5): `AdminService`, `ScheduleService`, `OutpatientService`, `InpatientService`, `StatisticsService` — business logic, `@Transactional` boundaries, cross-mapper orchestration
- **Mapper** (16): MyBatis `@Mapper` interfaces with XML SQL in `src/main/resources/mapper/`. Entities are Lombok `@Data` POJOs (no JPA)
- **Database** (20 tables, 10 triggers, 4 views): Business rules enforced at DB level via triggers (subtotal computation, bed status sync, balance checks, schedule conflict detection)

**Frontend** role-based SPA:

- Routes grouped by role in `router/index.ts`; `MainLayout.vue` renders role-specific sidebar
- API layer: `api/request.ts` (Axios with `/api` base, response interceptor unwrapping `ApiResponse.data`) → domain modules (`admin.ts`, `doctor.ts`, `patient.ts`)
- `utils/enums.ts` maps DB enum values to Chinese display labels
- Session-based auth: user info in `sessionStorage`, route guard redirects to `/login`

## Key Conventions

- **Adding a new entity**: Create entity class in `entity/`, mapper interface in `mapper/`, XML mapper in `resources/mapper/`, service method, controller endpoint, and frontend type in `types/index.ts`
- **MyBatis XML**: Located in `src/main/resources/mapper/*.xml`, matching mapper interfaces 1:1. Column-to-property mapping uses `underscore_to_camelCase` auto-mapping enabled in `application.properties`
- **SQL scripts**: `schema.sql` (DDL), `triggers.sql`, `views.sql`, `indexes.sql`, `data.sql` (seed) — all in `src/main/resources/`
- **Frontend enum sync**: When adding DB enum values, update both `utils/enums.ts` and `types/index.ts`
- **API docs**: `docs/API.md` documents all endpoints with request/response examples
- **Database design**: `docs/superpowers/specs/2026-05-22-hmis-database-design.md` has full schema DDL

## Test Accounts

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Doctor | zhangwei, lifang, wangqiang, zhaomin | 123456 |
| Patient | zhangsan, lisi, wangwu, zhaoliu | 123456 |
