package com.elevatementoring.backend.shared.domain;

/**
 * The requested resource does not exist. Mapped to HTTP 404.
 */
public class NotFoundException extends DomainException {

	public NotFoundException(String message) {
		super(message);
	}

}
