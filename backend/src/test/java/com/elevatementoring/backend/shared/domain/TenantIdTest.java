package com.elevatementoring.backend.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class TenantIdTest {

	@Test
	void rejectsNullValue() {
		assertThatThrownBy(() -> new TenantId(null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	void ofParsesUuidString() {
		UUID uuid = UUID.randomUUID();

		assertThat(TenantId.of(uuid.toString())).isEqualTo(new TenantId(uuid));
	}

	@Test
	void newIdGeneratesDistinctIds() {
		assertThat(TenantId.newId()).isNotEqualTo(TenantId.newId());
	}

}
