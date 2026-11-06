# ADR-004: Transactional Outbox to publish events to SQS

- **Status:** Accepted
- **Date:** 2026-11-06

## Context

Domain events trigger work in other modules: `SessionBooked` sends confirmation emails, `SessionCompleted` adds hours to the mentor (Recognition) and impact hours to the company (Impact).
These reactions go through SQS so they are asynchronous and retried.
Saving to Postgres and sending to SQS cannot happen in one transaction. Sending after commit can lose the event if the app crashes; sending before commit can publish an event for a change that was rolled back.

## Decision

Use a Transactional Outbox:

1. The use case saves the aggregate and inserts its events into an `outbox` table in the same database transaction.
2. A scheduled relay reads unpublished rows, sends them to the SQS queue of each consuming context (`notification-events`, `recognition-events`, `impact-events`, `matching-events`), and marks them as published.
3. Each queue has a DLQ (max 3 receives). Consumers are idempotent, keyed by `eventId`, because the relay delivers at least once.

## Consequences

- An event is published if and only if its transaction committed.
- Delivery is at-least-once, so every consumer must handle duplicates.
- Reactions are delayed by the relay's polling interval (a few seconds), acceptable for emails and counters.
- One more table and a background job to operate; the DLQ needs an alarm (OBS epic).
