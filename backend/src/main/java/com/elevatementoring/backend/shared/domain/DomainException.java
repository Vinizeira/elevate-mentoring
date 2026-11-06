package com.elevatementoring.backend.shared.domain;

/**
 * A business rule was broken (e.g. "the slot is in the past"). Mapped to HTTP 422.
 */
public class DomainException extends RuntimeException {

	public DomainException(String message) {
		super(message);
	}

}
