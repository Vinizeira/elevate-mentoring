# ADR-001: Modular monolith instead of microservices

- **Status:** Accepted
- **Date:** 2026-11-06

## Context

The Event Storming (`docs/event-storming-context-map.excalidraw`) found eight bounded contexts: IAM, Organization, Profiles, Scheduling, Matching, Notification, Recognition and Impact.
The MVP has one developer, about 20 hours a week and a fixed deadline (end of December 2026).
The hardest part of the product is booking without double-booking, which needs strong consistency in one database, not distributed coordination.

## Decision

Build a single Spring Boot application with one Postgres database, split into one package per bounded context under `com.elevatementoring.backend.<module>`, plus `shared` for the shared kernel.

Modules talk to each other only through the other module's `application` package or through domain events. This is enforced by ArchUnit (`ArchitectureTest`), so the boundaries are tested in CI instead of left to discipline.

## Consequences

- One build, one deploy, one database: local setup and CI stay simple, and transactions across a use case are plain local transactions.
- Boundaries can still erode inside one codebase. The ArchUnit rules are the guard; a rule failure blocks the build.
- A module can later be extracted into a service: it already talks to the rest through use cases and events. Splitting the database would be the expensive part.
- The whole app scales as one unit. That is fine for the MVP load.
