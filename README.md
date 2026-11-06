# 🎓 Elevate Mentoring

> A mentoring platform where students book sessions with volunteer mentors, and hours only count once **both** the student and the mentor confirm the session took place. Built as a Spring Boot modular monolith with Clean Architecture and DDD.

<p align="left">
  <a href="https://github.com/Vinizeira/elevate-mentoring/actions/workflows/ci.yml"><img src="https://github.com/Vinizeira/elevate-mentoring/actions/workflows/ci.yml/badge.svg?branch=develop" alt="CI" /></a>
</p>

<p align="left">
  <img src="https://img.shields.io/badge/Java-21-red?style=for-the-badge" alt="Java 21" />
  <img src="https://img.shields.io/badge/Spring%20Boot-4.1.2-brightgreen?style=for-the-badge" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-16-blue?style=for-the-badge" alt="PostgreSQL 16" />
  <img src="https://img.shields.io/badge/Flyway-Database%20Migrations-red?style=for-the-badge" alt="Flyway" />
  <img src="https://img.shields.io/badge/AWS-SQS%20%7C%20S3%20%7C%20SES-FF9900?style=for-the-badge" alt="AWS SQS, S3 and SES" />
  <img src="https://img.shields.io/badge/LocalStack-4.9-1D2B53?style=for-the-badge" alt="LocalStack" />
  <img src="https://img.shields.io/badge/Testcontainers-Integration%20Tests-2E8B57?style=for-the-badge" alt="Testcontainers" />
  <img src="https://img.shields.io/badge/ArchUnit-Architecture%20Tests-8A2BE2?style=for-the-badge" alt="ArchUnit" />
  <img src="https://img.shields.io/badge/Maven-Wrapper-orange?style=for-the-badge" alt="Maven" />
  <img src="https://img.shields.io/badge/Status-In%20Development-yellow?style=for-the-badge" alt="Status" />
</p>

---

## 📌 About the Project

Companies want to report the volunteer hours their employees give as mentors (an ESG metric), and students want an easy way to book time with someone who has been where they want to go.

The tricky part is trust: an hour should only count if the session really happened. In Elevate Mentoring, after a session **the mentor and the student each confirm it separately**, choosing from a fixed list the topics that were covered. The session is only completed when both have confirmed and at least one topic matches. Only then do the hours go to the mentor's recognition and to the company's impact report.

The project is being built in weekly stages. **Week 1 (foundation) is done**: architecture, local infrastructure, CI and the shared kernel are in place, and the business modules come next.

## 🚀 Features

### Foundation (done)

- 🧱 **Modular monolith** with one package per bounded context, plus a `shared` kernel
- 🛡️ **Architecture rules tested in CI** with ArchUnit: breaking a layer or module boundary fails the build
- 🗃️ **Versioned database** with Flyway migrations on PostgreSQL 16
- ☁️ **Local AWS** with LocalStack: SQS queues (each with a DLQ), S3 buckets and a verified SES sender, created automatically on startup
- 🧪 **Integration tests** with Testcontainers, against real Postgres and LocalStack containers
- ⚠️ **Standard error responses** with `ProblemDetail` (RFC 9457) through a global exception handler
- 📖 **API documentation** with Swagger UI / OpenAPI
- 🔒 **Spring Security** set up: only the docs and the health check are public
- 🔄 **CI on GitHub Actions** running the full build and tests, with a JaCoCo coverage report on every run

### Coming next

- 📅 **Scheduling**: book, cancel and confirm sessions, with no double-booking
- ✅ **Double confirmation**: a session counts only when the student and the mentor confirm it, with matching topics
- 🏅 **Recognition**: hours per mentor and a certificate after 10 hours
- 📊 **Impact**: volunteer hours per company, for ESG reports
- 🔔 **Notifications**: email confirmations and reminders
- 🔐 **Authentication**: the project's own JWT login ([ADR-003](docs/adr/003-own-jwt-authentication.md))

## 🏗️ Architecture

A single Spring Boot application with one database ([ADR-001](docs/adr/001-modular-monolith.md)). Each bounded context is a module with four layers ([ADR-002](docs/adr/002-clean-architecture-and-ddd.md)):

```text
HTTP Request
     │
     ▼
com.elevatementoring.backend.<module>
 ├── web/             → Controllers and DTOs; calls use cases
 ├── application/     → Use cases; defines ports for what it needs from outside
 ├── domain/          → Aggregates, value objects, domain events and rules (plain Java)
 └── infrastructure/  → Adapters: JPA, SQS, S3, SES
```

Rules enforced by ArchUnit on every build:

- `domain` does not depend on Spring, JPA, or any other layer.
- `application` does not depend on `infrastructure` or `web`.
- `web` does not depend on `infrastructure`.
- A module reaches another module only through its `application` package or its domain events.

Events between modules go to SQS through a Transactional Outbox ([ADR-004](docs/adr/004-transactional-outbox.md)).

The Event Storming found eight bounded contexts: **IAM, Organization, Profiles, Scheduling, Matching, Notification, Recognition and Impact**. The Scheduling rules are written down in [`docs/regras-scheduling.md`](docs/regras-scheduling.md).

## 📦 Project Structure

```text
elevate-mentoring-workspace/
├── backend/                         ← Java API (Spring Boot)
│   └── src/
│       ├── main/
│       │   ├── java/com/elevatementoring/backend/
│       │   │   ├── shared/
│       │   │   │   ├── domain/          ← Shared kernel: UserId, TenantId, DomainEvent, exceptions
│       │   │   │   ├── infrastructure/  ← Clock configuration
│       │   │   │   └── web/             ← Security, OpenAPI and global error handling
│       │   │   └── BackendApplication.java
│       │   └── resources/
│       │       ├── db/migration/        ← Flyway migrations
│       │       ├── application.properties
│       │       └── application-local.properties
│       └── test/                        ← Unit, integration (Testcontainers) and ArchUnit tests
├── frontend/                        ← React UI (empty for now)
├── infra/
│   ├── docker-compose.yml           ← Postgres 16 + LocalStack
│   └── localstack/init-aws.sh       ← Creates the SQS queues, S3 buckets and SES sender
├── docs/
│   ├── adr/                         ← Architecture Decision Records
│   ├── event-storming-context-map.excalidraw
│   └── regras-scheduling.md         ← Scheduling business rules
└── .github/workflows/ci.yml         ← Build, tests and coverage
```

## 🔗 Endpoints

The business endpoints arrive with the modules. Right now the API exposes:

| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/swagger-ui.html` | Public | Swagger UI (interactive API docs) |
| GET | `/v3/api-docs` | Public | OpenAPI specification (JSON) |
| GET | `/actuator/health` | Public | Application health check |

## ▶️ How to Run

### Prerequisites

- Java 21+
- Docker Desktop (running)
- Maven Wrapper (included in the repository)

### 1. Start the local infrastructure

Postgres 16 and LocalStack (SQS, S3 and SES):

```bash
cd infra
docker compose up -d
```

LocalStack runs `infra/localstack/init-aws.sh` on startup, which creates the SQS queues (each with a DLQ), the S3 buckets and the verified SES sender. Wait until both containers show `healthy` in `docker compose ps`.

### 2. Run the backend with the `local` profile

On Windows (PowerShell):

```powershell
cd backend
./mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=local"
```

> ⚠️ Keep the quotes around `-Dspring-boot.run.profiles=local`. Without them, PowerShell splits the argument at the dot and Maven fails with `Unknown lifecycle phase ".run.profiles=local"`.

On Linux or macOS:

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The application is ready when the log shows `Started BackendApplication`.

### 3. Open the API docs

Open **http://localhost:8080/swagger-ui.html** (use `http`, not `https`).

### Stop everything

```bash
cd infra
docker compose down      # stop the containers, keep the data
docker compose down -v   # stop the containers and wipe the database
```

## 🧪 Run Tests

Docker must be running: the integration tests start their own Postgres and LocalStack containers with Testcontainers, so they do not use the ones from `infra/`.

```powershell
cd backend
./mvnw.cmd verify
```

On Linux or macOS, use `./mvnw verify`. This runs the unit, integration and architecture tests, and writes the coverage report to `backend/target/site/jacoco/index.html`.

## 📚 Architecture Decisions

| ADR | Decision |
|---|---|
| [001](docs/adr/001-modular-monolith.md) | Modular monolith instead of microservices |
| [002](docs/adr/002-clean-architecture-and-ddd.md) | Clean Architecture + DDD inside each module |
| [003](docs/adr/003-own-jwt-authentication.md) | Own JWT authentication instead of Cognito |
| [004](docs/adr/004-transactional-outbox.md) | Transactional Outbox to publish events to SQS |
| [005](docs/adr/005-terraform-and-ecs-fargate.md) | Terraform + ECS Fargate for AWS infrastructure |

## 🧠 Development Approach

```text
Event Storming → Business rules → ADRs → Foundation → One module at a time → Test → Refactor
```

Git Flow: each card gets a `feature/*` branch from `develop`, merged back with `--no-ff`. `develop` goes to `master` only after review.

## 🛠️ Tech Stack

- **Java 21** with virtual threads
- **Spring Boot 4.1.2**: Web MVC, Data JPA, Security, Validation and Actuator
- **PostgreSQL 16**: main database
- **Flyway**: database migrations
- **Spring Cloud AWS 4.2**: SQS, S3 and SES
- **LocalStack 4.9**: local AWS
- **springdoc-openapi**: Swagger UI
- **JUnit 5, Testcontainers and ArchUnit**: unit, integration and architecture tests
- **JaCoCo**: test coverage
- **GitHub Actions**: CI
- **Docker Compose**: local infrastructure
- **Maven Wrapper**: build and dependency management

## 👨‍💻 Author

**Vinicius Pereira**

Backend Developer — Java & Spring Boot

[LinkedIn](https://www.linkedin.com/in/oviniciuspereira/)
