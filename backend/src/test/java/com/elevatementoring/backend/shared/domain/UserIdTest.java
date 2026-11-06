package com.elevatementoring.backend.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

class UserIdTest {

	@Test
	void rejectsNullValue() {
		assertThatThrownBy(() -> new UserId(null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	void ofParsesUuidString() {
		UUID uuid = UUID.randomUUID();

		assertThat(UserId.of(uuid.toString())).isEqualTo(new UserId(uuid));
	}

	@Test
	void newIdGeneratesDistinctIds() {
		assertThat(UserId.newId()).isNotEqualTo(UserId.newId());
	}

}
