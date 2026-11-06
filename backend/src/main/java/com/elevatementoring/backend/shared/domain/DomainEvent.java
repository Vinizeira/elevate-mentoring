package com.elevatementoring.backend.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * A fact that happened in the domain (e.g. SessionBooked). Implementations are immutable records.
 */
public interface DomainEvent {

	UUID eventId();

	Instant occurredAt();

}
