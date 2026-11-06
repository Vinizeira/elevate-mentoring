# Elevate Mentoring

A mentoring platform where students book sessions with mentors, and hours only count once both the student and the mentor confirm the session took place.

## How to run

Requirements: Docker and Java 21.

1. Start the local infrastructure (Postgres 16 + LocalStack with SQS, S3 and SES):

   ```bash
   cd infra
   docker compose up -d
   ```

   LocalStack runs `infra/localstack/init-aws.sh` on startup, which creates the SQS queues (each with a DLQ), the S3 buckets and the verified SES sender.

2. Run the backend with the `local` profile:

   ```bash
   cd backend
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

3. Open the API docs (Swagger UI) at http://localhost:8080/swagger-ui.html

To stop everything and wipe the database: `docker compose down -v` (inside `infra/`).

## Folder structure

```
elevate-mentoring-workspace/
├── backend/     ← Java API (Spring Boot)
├── frontend/    ← React UI (empty for now)
├── infra/       ← Docker and, later, Terraform
├── docs/        ← Event Storming, context map and business rules
├── README.md
└── .gitignore
```
