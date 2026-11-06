# Architecture Decision Records

Short records of the decisions that shape the codebase: context, decision, consequences.
A decision is never edited after it is accepted; if it changes, a new ADR supersedes it.

| ADR | Decision | Status |
|---|---|---|
| [001](001-modular-monolith.md) | Modular monolith instead of microservices | Accepted |
| [002](002-clean-architecture-and-ddd.md) | Clean Architecture + DDD inside each module | Accepted |
| [003](003-own-jwt-authentication.md) | Own JWT authentication instead of Cognito | Accepted |
| [004](004-transactional-outbox.md) | Transactional Outbox to publish events to SQS | Accepted |
| [005](005-terraform-and-ecs-fargate.md) | Terraform + ECS Fargate for AWS infrastructure | Accepted |
