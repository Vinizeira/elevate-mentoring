# ADR-002: Clean Architecture + DDD inside each module

- **Status:** Accepted
- **Date:** 2026-11-06

## Context

The core rules (who can book, cancel or confirm a session, and when a session counts as completed; see `docs/regras-scheduling.md`) must be easy to unit test and must not depend on Spring, JPA or AWS.
Tests that need a database or LocalStack are slow; business rules should not need them.

## Decision

Each module has four layers:

```
com.elevatementoring.backend.<module>
 ├── domain/          aggregates, value objects, domain events, rules. Plain Java.
 ├── application/     use cases; defines ports (interfaces) for what it needs from the outside
 ├── infrastructure/  adapters: JPA repositories, SQS, S3, SES
 └── web/             controllers and DTOs
```

Dependency rules, enforced by ArchUnit:

- `domain` does not depend on Spring, JPA/Hibernate, or any other layer.
- `application` does not depend on `infrastructure` or `web`.
- `web` does not depend on `infrastructure`; it calls use cases.
- A module only reaches another module through its `application` package or its `DomainEvent`s.

DDD building blocks: aggregates own their invariants (each "NO when…" rule from the Event Storming becomes a check that throws a `DomainException`), value objects are `record`s (`UserId`, `TenantId`), and state changes produce `DomainEvent`s. Time comes from an injected `Clock`.

## Consequences

- Business rules are tested with fast unit tests, no Spring context.
- JPA entities and domain objects are separate classes, so there is mapping code in `infrastructure`. That is the price of keeping JPA out of the domain.
- More packages and interfaces than a layered CRUD app. For simple read-only screens, `application` can return query DTOs directly instead of going through aggregates.
