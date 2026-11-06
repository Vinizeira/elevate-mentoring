package com.elevatementoring.backend.architecture.fixtures.scheduling.domain;

import java.time.Instant;
import java.util.UUID;

import com.elevatementoring.backend.shared.domain.DomainEvent;

public record SessionCompleted(UUID eventId, Instant occurredAt) implements DomainEvent {
}
