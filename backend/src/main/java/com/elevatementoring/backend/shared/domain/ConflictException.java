package com.elevatementoring.backend.shared.domain;

/**
 * The request conflicts with the current state (e.g. the slot was already booked). Mapped to HTTP 409.
 */
public class ConflictException extends DomainException {

	public ConflictException(String message) {
		super(message);
	}

}
