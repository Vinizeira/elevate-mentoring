package com.elevatementoring.backend.shared.domain;

import java.util.Objects;
import java.util.UUID;

public record TenantId(UUID value) {

	public TenantId {
		Objects.requireNonNull(value, "TenantId value must not be null");
	}

	public static TenantId newId() {
		return new TenantId(UUID.randomUUID());
	}

	public static TenantId of(String value) {
		return new TenantId(UUID.fromString(value));
	}

	@Override
	public String toString() {
		return value.toString();
	}

}
